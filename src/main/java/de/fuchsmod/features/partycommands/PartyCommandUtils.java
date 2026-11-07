package de.fuchsmod.features.partycommands;

import de.fuchsmod.commands.Debug;
import de.fuchsmod.events.GameEvents;
import de.fuchsmod.features.performance.PingMeasurement;
import de.fuchsmod.features.performance.TPSMeasurement;
import org.apache.commons.lang3.function.TriFunction;

import java.util.LinkedHashMap;

import static de.fuchsmod.FuchsMod.CLIENT;
import static de.fuchsmod.FuchsMod.CONFIG;

public class PartyCommandUtils {
    private static final TPSMeasurement tps = TPSMeasurement.getInstance();
    private static final PingMeasurement ping = PingMeasurement.getInstance();

    private static int getRandomInteger(int min, int max) {
        return (int) ((max - min + 1) * Math.random()) + min;
    }

    public static String getScopeChatCommand(Scope scope) {
        return switch (scope) {
            case PARTY -> "/pc ";
            case GUILD -> "/gc";
            case OFFICER -> "/oc";
            default -> "";
        };
    }

    public static final LinkedHashMap<String, TriFunction<Scope, String, String[], String>> replacementCommands = new LinkedHashMap<>();

    public static void init() {
        replacementCommands.put("None", null);
        replacementCommands.put("Get TPS", getTPS);
        replacementCommands.put("Get FPS", getFPS);
        replacementCommands.put("Get Ping", getPing);
        replacementCommands.put("Dice Roll", diceRoll);
        replacementCommands.put("Bedwars Dream", getDream);
        replacementCommands.put("Downtime Request", requestDowntime);

        GameEvents.ON_GAME_END.register(() -> {
            if (downtimeMessage != null) {
                PartyCommands.scheduleChatMessage(getScopeChatCommand(downtimeScope) + downtimeMessage);
                downtimeMessage = null;
            }
        });
    }

    /* TriFunction arguments:
    - scope: Scope
    - senderName: String
    - arguments: String[]
    */

    public static TriFunction<Scope, String, String[], String> getTPS = (_, _, _) -> "%.1f".formatted(tps.getAverageTPS());

    public static TriFunction<Scope, String, String[], String> getFPS = (_, _, _) -> "%d".formatted(CLIENT.getFps());

    public static TriFunction<Scope, String, String[], String> getPing = (_, _, _) -> "%d".formatted(ping.getAveragePing());

    public static TriFunction<Scope, String, String[], String> diceRoll = (_, _, arguments) -> {
        try {
            int a = Integer.parseInt(arguments[0]);
            int b = Integer.parseInt(arguments[1]);
            return "%d".formatted(getRandomInteger(Integer.min(a, b), Integer.max(a, b)));
        } catch (Exception e) {
            return "%d".formatted(getRandomInteger(1, 6));
        }
    };

    public static TriFunction<Scope, String, String[], String> getDream = (_, _, _) -> {
        if (!CONFIG.partyCommandsBedwarsDreams.isEmpty())
            return CONFIG.partyCommandsBedwarsDreams.get((int) (((System.currentTimeMillis() / 1000 - CONFIG.dreamRotationOffsetSeconds) % (CONFIG.partyCommandsBedwarsDreams.size() * CONFIG.dreamRotationTimeSeconds)) / CONFIG.dreamRotationTimeSeconds));
        else {
            Debug.sendDebugMessage("No Bedwars Dream Mode found.");
            return "";
        }
    };

    private static String downtimeMessage;
    private static Scope downtimeScope;

    public static TriFunction<Scope, String, String[], String> requestDowntime = (scope, senderName, arguments) -> {
        if (arguments.length == 0)
            downtimeMessage = "Downtime requested by %s".formatted(senderName);
        String reason = String.join(" ", arguments);
        downtimeMessage = "Downtime requested by %s: %s".formatted(senderName, reason);
        downtimeScope = scope;
        return downtimeMessage;
    };
}
