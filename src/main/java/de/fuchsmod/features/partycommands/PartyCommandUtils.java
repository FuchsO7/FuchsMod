package de.fuchsmod.features.partycommands;

import de.fuchsmod.events.GameEvents;
import de.fuchsmod.features.general.PingMeasurement;
import de.fuchsmod.features.general.TPSMeasurement;
import org.apache.commons.lang3.function.TriFunction;

import java.util.LinkedHashMap;

import static de.fuchsmod.FuchsMod.CLIENT;

public class PartyCommandUtils {
    private static final TPSMeasurement tps = TPSMeasurement.getInstance();
    private static final PingMeasurement ping = PingMeasurement.getInstance();

    private static int getRandomInteger(int min, int max) {
        return (int) ((max - min + 1) * Math.random()) + min;
    }

    public static String getScopeChatCommand(String scope) {
        return switch (scope) {
            case "party" -> "/pc ";
            case "guild" -> "/gc";
            case "officer" -> "/oc";
            default -> "";
        };
    }

    public static final LinkedHashMap<String, TriFunction<String, String, String[], String>> replacementCommands = new LinkedHashMap<>();

    public static void init() {
        replacementCommands.put("None", null);
        replacementCommands.put("Get TPS", getTPS);
        replacementCommands.put("Get FPS", getFPS);
        replacementCommands.put("Get Ping", getPing);
        replacementCommands.put("Dice Roll", diceRoll);
        replacementCommands.put("Bedwars Dream", getDream);
        replacementCommands.put("Downtime Request", requestDowntime);

        GameEvents.GAME_ENDED.register(() -> {
            if (downtimeMessage != null) {
                PartyCommands.sendChatMessage(getScopeChatCommand(downtimeScope) + downtimeMessage);
                downtimeMessage = null;
            }
        });
    }

    /* TriFunction arguments:
    - scope: String
    - senderName: String
    - arguments: String[]
    */

    public static TriFunction<String, String, String[], String> getTPS = (_, _, _) -> "%.1f".formatted(tps.getAverageTPS());

    public static TriFunction<String, String, String[], String> getFPS = (_, _, _) -> "%d".formatted(CLIENT.getFps());

    public static TriFunction<String, String, String[], String> getPing = (_, _, _) -> "%d".formatted(ping.getAveragePing());

    public static TriFunction<String, String, String[], String> diceRoll = (_, _, arguments) -> {
        try {
            int a = Integer.parseInt(arguments[0]);
            int b = Integer.parseInt(arguments[1]);
            return "%d".formatted(getRandomInteger(Integer.min(a, b), Integer.max(a, b)));
        } catch (Exception e) {
            return "%d".formatted(getRandomInteger(1, 6));
        }
    };

    private static final String[] DREAMS = {"swap","oneblock","rush","ultimate","castles","voidless","armed","lucky"};
    private static final long DREAM_ROTATION_TIME_SECONDS = 82800;
    private static final long WEEK_IN_SECONDS = 604800;

    public static TriFunction<String, String, String[], String> getDream = (_, _, _) ->
            DREAMS[(int) (((System.currentTimeMillis() - DREAM_ROTATION_TIME_SECONDS) % (DREAMS.length * WEEK_IN_SECONDS)) / WEEK_IN_SECONDS)];

    private static String downtimeMessage;
    private static String downtimeScope;

    public static TriFunction<String, String, String[], String> requestDowntime = (scope, senderName, arguments) -> {
        if (arguments.length == 0)
            downtimeMessage = "Downtime requested by %s".formatted(senderName);
        String reason = String.join(" ", arguments);
        downtimeMessage = "Downtime requested by %s: %s".formatted(senderName, reason);
        downtimeScope = scope;
        return downtimeMessage;
    };
}
