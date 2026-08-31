package de.fuchsmod.features.partycommands;

import de.fuchsmod.commands.Debug;
import de.fuchsmod.config.controllers.PartyCommandRecord;
import de.fuchsmod.events.ChatEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.util.Util;

import java.util.*;

import static de.fuchsmod.FuchsMod.LOGGER;
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
        ClientTickEvents.END_LEVEL_TICK.register((clientLevel) -> {
            ScheduledMessage scheduledMessage = scheduledMessages.peek();
            if (scheduledMessage == null)
                return;
            if (scheduledMessage.time < Util.getMillis() && lastMessageSentMillis + CONFIG.commandDelay < Util.getMillis()) {
                new ChatScreen("", false).handleChatInput(scheduledMessage.message(), enablePartyCommandsDebug);
                scheduledMessages.poll();
                lastMessageSentMillis = Util.getMillis();
            }
        });
        LOGGER.info("Initialized Party Commands!");
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
        if ((scopesInteger >> 2) % 2 == 1 )
            scopes.add("guild");
        if ((scopesInteger >> 3) % 2 == 1 )
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
        if (message.split(":").length < 2)
            return;
        String prefix = message.split(":")[0];
        String content = message.substring(prefix.length() + 1).strip();
        if (!content.startsWith("!"))
            return;
        String[] prefixSplit = prefix.split(" ");
        String senderName = prefixSplit[prefixSplit.length - 1];
        String scope = prefix.contains(">") ? prefixSplit[0].toLowerCase().strip() : "public";
        String command = content.split(" ")[0];
        String[] arguments = content.substring(command.length()).strip().split(" ");

        Debug.sendDebugMessage("Executing Party Command '%s':\n- Scope: %s\n- Sender: %s\n- Arguments: %s".formatted(
                command, scope, senderName, Arrays.toString(arguments)), enablePartyCommandsDebug);

        var partyCommand = commands.get(command);
        if (partyCommand != null)
            partyCommand.run(scope, senderName, arguments);
    }
}
