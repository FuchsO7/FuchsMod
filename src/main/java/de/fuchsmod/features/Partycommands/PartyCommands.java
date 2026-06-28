package de.fuchsmod.features.Partycommands;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PartyCommands {
    private static final FuchsModConfig config = FuchsModConfigManager.getInstance();
    protected static final HashMap<String, PartyCommand> commands = new HashMap<>();

    private static final String[] basicPartyChatCommands = new String[] {
            "!warp:/party warp",
            "!allinv:/party settings allinvite true",
            "!ptme:/party transfer {player}",
            "!f1:/joininstance CATACOMBS_FLOOR_ONE",
            "!f2:/joininstance CATACOMBS_FLOOR_TWO",
            "!f3:/joininstance CATACOMBS_FLOOR_THREE",
            "!f4:/joininstance CATACOMBS_FLOOR_FOUR",
            "!f5:/joininstance CATACOMBS_FLOOR_FIVE",
            "!f6:/joininstance CATACOMBS_FLOOR_SIX",
            "!f7:/joininstance CATACOMBS_FLOOR_SEVEN",
            "!m1:/joininstance MASTER_CATACOMBS_FLOOR_ONE",
            "!m2:/joininstance MASTER_CATACOMBS_FLOOR_TWO",
            "!m3:/joininstance MASTER_CATACOMBS_FLOOR_THREE",
            "!m4:/joininstance MASTER_CATACOMBS_FLOOR_FOUR",
            "!m5:/joininstance MASTER_CATACOMBS_FLOOR_FIVE",
            "!m6:/joininstance MASTER_CATACOMBS_FLOOR_SIX",
            "!m7:/joininstance MASTER_CATACOMBS_FLOOR_SEVEN",
            "!t1:/joininstance KUUDRA_NORMAL",
            "!t2:/joininstance KUUDRA_HOT",
            "!t3:/joininstance KUUDRA_BURNING",
            "!t4:/joininstance KUUDRA_FIERY",
            "!t5:/joininstance KUUDRA_INFERNAL",
            "!b8x2:/play bedwars_eight_two",
            "!b4x4:/play bedwars_four_four",
            "!b4x3:/play bedwars_four_three",
            "!b2x4:/play bedwars_two_four",
            "!b4v4:/play bedwars_two_four",
            "!m:/play murder_classic",
            "!mc:/play murder_classic",
            "!md:/play murder_double_up",
            "!mi:/play murder_infection",
            "!ma:/play murder_assassins",

            //Commands von Schnittlauch
            "!invite:/party invite {args[0]}",
            "!kick:/party kick {args[0]}",
            "!kickoffline:/party kickoffline",
            "!ifuchs:/party invite Fuchs07",
            "!istein:/party invite kyloren223",
            "!ieman:/party invite Schnitzel747",
            "!imensch:/party invite Jeremy_2002",
            "!icat:/party invite h1guro",
            "!promote:/party promote {args[0]}",
            "!demote:/party demote {args[0]}",
            "!pie:/pchat 3.1415926535 wait a minute this is not a pie this is pi",
            "!e:/pchat C:HBICIBICIEFJAEFCDFDGACIHEHBDFC  Hmm i feel something is off about this number"
    };

    public static void init() {
        commands.clear();
        for(String command : basicPartyChatCommands) {
            String partyMessage = command.split(":")[0];
            String commandToExecute = command.substring(partyMessage.length()+1);
            final List<String> scopes = new ArrayList<>();
            scopes.add("party");
            commands.put(partyMessage, new PartyCommand(scopes, commandToExecute));
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
