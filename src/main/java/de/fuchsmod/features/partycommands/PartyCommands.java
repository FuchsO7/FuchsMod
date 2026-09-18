package de.fuchsmod.features.partycommands;

import de.fuchsmod.commands.Debug;
import de.fuchsmod.config.controllers.PartyCommandRecord;
import de.fuchsmod.events.ChatEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
        ClientTickEvents.END_CLIENT_TICK.register((clientLevel) -> {
            ScheduledMessage scheduledMessage = scheduledMessages.peek();
            if (scheduledMessage == null)
                return;
            String message = StringUtils.normalizeSpace(scheduledMessage.message());
            if (CLIENT.player != null && scheduledMessage.time < Util.getMillis() && lastMessageSentMillis + CONFIG.commandDelay < Util.getMillis()) {
                if (message.startsWith("/"))
                    CLIENT.player.connection.sendCommand(message.substring(1));
                else
                    CLIENT.player.connection.sendChat(message);
                scheduledMessages.poll();
                lastMessageSentMillis = Util.getMillis();
            }
        });
        LOGGER.debug("Initialized Party Commands!");
    }

    public static void sendChatMessage(String message) {
        if (message.isEmpty())
            return;
        Debug.sendDebugMessage("Scheduling Message to send in %s ms: %s".formatted(CONFIG.commandDelay, message), enablePartyCommandsDebug);
        scheduledMessages.offer(new ScheduledMessage(Util.getMillis() + CONFIG.commandDelay, message));
    }

    public static List<String> getScopes(int scopesInteger) {
        List<String> scopes = new ArrayList<>();
        if (scopesInteger % 2 == 1)
            scopes.add("public");
        if ((scopesInteger >> 1) % 2 == 1)
            scopes.add("party");
        if ((scopesInteger >> 2) % 2 == 1)
            scopes.add("guild");
        if ((scopesInteger >> 3) % 2 == 1)
            scopes.add("officer");
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

                String scope = matcher.group(1).toLowerCase(Locale.ROOT);
                if (!getScopes(15).contains(scope))
                    scope = "public";
                String senderName = matcher.group(2);
                String command = matcher.group(3);
                String[] arguments = message.substring(matcher.end()).strip().split(" ");

                Debug.sendDebugMessage("Executing Party Command '%s':\n- Scope: %s\n- Sender: %s\n- Arguments: %s".formatted(
                        command, scope, senderName, Arrays.toString(arguments)), enablePartyCommandsDebug);

                PartyCommand partyCommand = commands.get(command);
                if (partyCommand != null)
                    partyCommand.run(scope, senderName, arguments);
                return;
            } catch (PatternSyntaxException exception) {
                if (!message.contains("Invalid Regex"))
                    Debug.sendDebugMessage("Invalid Regex %s: %s".formatted(regex, exception.getDescription()));
            }
        }
    }
}
