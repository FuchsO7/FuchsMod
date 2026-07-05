package de.fuchsmod.features.partycommands;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import de.fuchsmod.config.controllers.PartyCommandRecord;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PartyCommands {
    private static final FuchsModConfig config = FuchsModConfigManager.getInstance();
    protected static final HashMap<String, PartyCommand> commands = new HashMap<>();
    private static final List<ScheduledMessage> scheduledMessages = new LinkedList<>();

    private record ScheduledMessage(long time, String message) {
    }

    public static void init() {
        loadCommands();
        ClientTickEvents.END_LEVEL_TICK.register((clientLevel) -> {
            for (ScheduledMessage scheduledMessage : scheduledMessages) {
                if (scheduledMessage.time < Util.getMillis()) {
                    new ChatScreen("", false).handleChatInput(scheduledMessage.message(), false);
                    scheduledMessages.remove(scheduledMessage);
                }
            }
        });
    }

    public static void sendChatMessage(String message) {
        scheduledMessages.add(new ScheduledMessage(Util.getMillis() + (long) config.commandDelay, message));
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
        for(PartyCommandRecord command : config.partyCommandsList) {
            commands.put(command.trigger(), new PartyCommand(
                    getScopes(command.scopes()),
                    command.command(),
                    PartyCommandUtils.replacementCommands.get(command.replacementFunction())));
        }
    }

    public static void onChatMessage(String message) {
        if (!config.enablePartyCommands)
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

        var partyCommand = commands.get(command);
        if (partyCommand != null)
            partyCommand.run(scope, senderName, arguments);
    }
}
