package de.fuchsmod.features.partycommands;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import de.fuchsmod.config.controllers.PartyCommandRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PartyCommands {
    private static final FuchsModConfig config = FuchsModConfigManager.getInstance();
    protected static final HashMap<String, PartyCommand> commands = new HashMap<>();

    public static void init() {
        loadCommands();
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
