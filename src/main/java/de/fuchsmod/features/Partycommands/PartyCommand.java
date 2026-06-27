package de.fuchsmod.features.Partycommands;

import net.minecraft.client.gui.screens.ChatScreen;

public class PartyCommand {

    public void run(String scope, String senderName, String[] arguments) {

    }

    private static void sendChatMessage(String message) {
        new ChatScreen("", false).handleChatInput(message, false);
    }
}
