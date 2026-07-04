package de.fuchsmod.features.partycommands;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import de.fuchsmod.features.PingMeasurement;
import de.fuchsmod.features.TPSMeasurement;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.function.TriFunction;

import java.util.LinkedHashMap;

public class PartyCommandUtils {
    private static final FuchsModConfig config = FuchsModConfigManager.getInstance();
    private static final Minecraft client = Minecraft.getInstance();
    private static final TPSMeasurement tps = TPSMeasurement.getInstance();
    private static final PingMeasurement ping = PingMeasurement.getInstance();

    private static int getRandomInteger(int min, int max) {
        return (int) ((max - min + 1) * Math.random()) + min;
    }

    public String getScopeChatCommand(String scope) {
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
    }

    /* TriFunction arguments:
    - scope: String
    - senderName: String
    - arguments: String[]
    */

    public static TriFunction<String, String, String[], String> getTPS = (_, _, _) -> "%.1f".formatted(tps.getAverageTPS());

    public static TriFunction<String, String, String[], String> getFPS = (_, _, _) -> "%d".formatted(client.getFps());

    public static TriFunction<String, String, String[], String> getPing = (_, _, _) -> "%d".formatted(ping.getAveragePing());

    public static TriFunction<String, String, String[], String> diceRoll = (_, _, arguments) -> {
        try {
            int a = Integer.parseInt(arguments[0]);
            int b = Integer.parseInt(arguments[1]);
            return "" + getRandomInteger(Integer.min(a, b), Integer.max(a, b));
        } catch (Exception e) {
            return "" + getRandomInteger(1, 6);
        }
    };

    private static final String[] DREAMS = {"swap","oneblock","rush","ultimate","castles","voidless","armed","lucky"};
    private static final long DREAM_ROTATION_TIME_SECONDS = 82800;
    private static final long WEEK_IN_SECONDS = 604800;

    public static TriFunction<String, String, String[], String> getDream = (_, _, _) ->
            DREAMS[(int) (((System.currentTimeMillis() - DREAM_ROTATION_TIME_SECONDS) % (DREAMS.length * WEEK_IN_SECONDS)) / WEEK_IN_SECONDS)];
}
