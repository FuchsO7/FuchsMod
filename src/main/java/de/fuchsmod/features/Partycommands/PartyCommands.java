package de.fuchsmod.features.Partycommands;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import net.minecraft.client.gui.screens.ChatScreen;

public class PartyCommands {
    private static final FuchsModConfig config = FuchsModConfigManager.getInstance();

    public static void init() {

    }

    public static void onChatMessage(String messageString) {
        if (config.enablePartyCommands) {
            String messagePrefix = messageString.split(":")[0];
            String[] prefixSplit = messagePrefix.split(" ");
            String senderName = prefixSplit[prefixSplit.length - 1];
            String scope = messagePrefix.contains(" >") ? prefixSplit[0].toLowerCase() : "public";
        }
    }

    private static void sendChatMessage(String message) {
        new ChatScreen("", false).handleChatInput(message, false);
    }
}
