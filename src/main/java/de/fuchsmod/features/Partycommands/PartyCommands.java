package de.fuchsmod.features.Partycommands;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.HashMap;

public class PartyCommands {
    private static final FuchsModConfig config = FuchsModConfigManager.getInstance();
    protected static final HashMap<String, PartyCommand> commands = new HashMap<>();

    public static void init() {

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

        Minecraft client = Minecraft.getInstance();
        if (client.player != null && !message.contains("TEST"))
            client.player.sendSystemMessage(Component.literal("TEST Sender: %s, Scope: %s, Content: %s, Command: %s, Args: %s".formatted(senderName, scope, content, command, Arrays.toString(arguments))));
    }
}
