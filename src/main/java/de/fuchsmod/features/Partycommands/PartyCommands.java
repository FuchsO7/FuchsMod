package de.fuchsmod.features.Partycommands;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;

public class PartyCommands {
    private static final FuchsModConfig config = FuchsModConfigManager.getInstance();

    public static void init() {

    }

    public static void onChatMessage(String message) {
        if (!config.enablePartyCommands)
            return;
        if (message.split(":").length < 2)
            return;
        String prefix = message.split(":")[0];
        String content = message.substring(prefix.length() + 1).trim();
        if (!content.startsWith("!"))
            return;
        String[] prefixSplit = prefix.split(" ");
        String senderName = prefixSplit[prefixSplit.length - 1];
        String scope = prefix.contains(">") ? prefixSplit[0].toLowerCase().trim() : "public";

        Minecraft client = Minecraft.getInstance();
        if (client.player != null && !message.contains("TEST"))
            client.player.sendSystemMessage(Component.literal("TEST Sender: %s, Scope: %s, Content: %s".formatted(senderName, scope, content)));
    }

    private static void sendChatMessage(String message) {
        new ChatScreen("", false).handleChatInput(message, false);
    }
}
