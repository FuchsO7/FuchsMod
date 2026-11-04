package de.fuchsmod.features.partycommands;

import de.fuchsmod.commands.Debug;
import de.fuchsmod.config.controllers.PartyCommandRecord;
import de.fuchsmod.events.ChatEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.fuchsmod.FuchsMod.LOGGER;
import static de.fuchsmod.FuchsMod.CLIENT;
import static de.fuchsmod.FuchsMod.CONFIG;

public class PartyCommands {
    protected static final HashMap<String, PartyCommand> commands = new HashMap<>();
    private static final Queue<ScheduledMessage> scheduledMessages = new LinkedList<>();
    private static long lastMessageSentMillis = 0;
    public static boolean enablePartyCommandsDebug = false;

    private record ScheduledMessage(long time, String message) {
    }

    public static void init() {
        loadCommands();
        ChatEvents.MESSAGE.register(message -> {
            String messageString = message.content().getString()
                    .replaceAll("\r", "\\\\r")
                    .replaceAll("\n", "\\\\n")
                    .replaceAll("§.", "");
            onChatMessage(messageString);
        });
        ClientTickEvents.END_CLIENT_TICK.register(_ -> handleScheduledMessages());
        LOGGER.debug("Initialized Party Commands!");
    }

    public static void handleScheduledMessages() {
        if (scheduledMessages.isEmpty())
            return;
        ScheduledMessage scheduledMessage = scheduledMessages.peek();
        if (CLIENT.player != null && scheduledMessage.time() < Util.getMillis() && lastMessageSentMillis + CONFIG.commandDelay < Util.getMillis()) {
            String message = StringUtils.normalizeSpace(scheduledMessage.message());
            ChatScreen chatScreen = new ChatScreen("", false);
            chatScreen.init(0, 0); // For compatibility with other mods which mess with chat screens
            chatScreen.handleChatInput(message, false);
            scheduledMessages.poll();
            lastMessageSentMillis = Util.getMillis();
        }
    }

    public static void scheduleChatMessage(String message) {
        if (message.isEmpty())
            return;
        Debug.sendDebugMessage("Scheduling Message to send in %s ms: %s".formatted(CONFIG.commandDelay, message), enablePartyCommandsDebug);
        scheduledMessages.offer(new ScheduledMessage(Util.getMillis() + CONFIG.commandDelay, message));
    }

    public static List<Scope> getScopes(int scopesInteger) {
        List<Scope> scopes = new ArrayList<>();
        for (Scope scope : Scope.values()) {
            if ((scopesInteger >> scope.ordinal()) % 2 == 1)
                scopes.add(scope);
        }
        return scopes;
    }

    public static void loadCommands() {
        commands.clear();
        for (PartyCommandRecord command : CONFIG.partyCommandsList) {
            commands.put(command.trigger(), new PartyCommand(
                    getScopes(command.scopes()),
                    command.command(),
                    PartyCommandUtils.replacementCommands.get(command.replacementFunction())));
        }
    }

    public static void onChatMessage(String message) {
        if (!CONFIG.enablePartyCommands)
            return;
        for (String regex : CONFIG.partyCommandsPatterns) {
            try {
                Matcher matcher = Pattern.compile(regex).matcher(message);
                if (!matcher.find())
                    continue;

                String scopeString = matcher.group("scope");
                Scope scope;
                try {
                    scope = Scope.valueOf(scopeString.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException exception) {
                    if (CONFIG.partyCommandsCustomScopes.contains(scopeString))
                        scope = Scope.CUSTOM;
                    else
                        scope = Scope.PUBLIC;
                }

                String senderName = matcher.group("sender");
                String command = matcher.group("command");
                String[] arguments = message.substring(matcher.end()).strip().split(" ");

                Debug.sendDebugMessage("Executing Party Command '%s':\n- Scope: %s\n- Sender: %s\n- Arguments: %s".formatted(
                        command, scopeString, senderName, Arrays.toString(arguments)), enablePartyCommandsDebug);

                PartyCommand partyCommand = commands.get(command);
                if (partyCommand != null)
                    partyCommand.run(scope, scopeString, senderName, arguments);
                return;
            } catch (IllegalArgumentException exception) {
                if (!message.contains("Invalid Regex"))
                    Debug.sendDebugMessage("Invalid Regex %s: %s".formatted(regex, exception.getMessage()));
            }
        }
    }
}
