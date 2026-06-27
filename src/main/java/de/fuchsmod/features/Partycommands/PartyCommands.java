package de.fuchsmod.features.Partycommands;

import net.minecraft.client.gui.screens.ChatScreen;

public class PartyCommands {

    public static void init() {

    }

    public static void onChatMessage(String messageString) {

    }

    private static void sendChatMessage(String message) {
        new ChatScreen("", false).handleChatInput(message, false);
    }
}
