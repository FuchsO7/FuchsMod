package de.fuchsmod.compatibility;

import de.fuchsmod.events.GameEvents;
import de.hysky.skyblocker.events.DungeonEvents;

import static de.fuchsmod.FuchsMod.LOGGER;

public class Skyblocker {

    public static void register() {
        DungeonEvents.DUNGEON_ENDED.register(() -> {
            GameEvents.GAME_ENDED.invoker().onGameEnd();
        });
        LOGGER.debug("Registered Skyblocker Dungeon Ended Event!");
    }
}