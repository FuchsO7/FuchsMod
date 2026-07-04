package de.fuchsmod.features.partycommands;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import net.minecraft.client.gui.screens.ChatScreen;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;

public class PartyCommand {
    private static final FuchsModConfig config = FuchsModConfigManager.getInstance();
    private final List<String> scopes;
    private final String message;
    private final TriFunction<String, String, String[], String> replacementFunction;

    public PartyCommand (List<String> scopes, String message) {
        this(scopes, message, null);
    }

    public PartyCommand (List<String> scopes, String message, TriFunction<String, String, String[], String> replacementFunction) {
        this.scopes = scopes;
        this.message = message;
        this.replacementFunction = replacementFunction;
    }

    public void run(String scope, String senderName, String[] arguments) {
        if (!scopes.contains(scope))
            return;
        String messageToSend = message.replace("{player}", senderName);
        for(int i = 0; i < arguments.length; i++)
            messageToSend = messageToSend.replace("{args["+i+"]}", arguments[i]);
        if (replacementFunction != null)
            messageToSend = messageToSend.replace("{function}", replacementFunction.apply(scope, senderName, arguments));
        sendChatMessage(messageToSend);
    }

    private static void sendChatMessage(String message) {
        new ChatScreen("", false).handleChatInput(message, false);
    }
}
