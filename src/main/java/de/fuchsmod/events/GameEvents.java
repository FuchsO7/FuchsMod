package de.fuchsmod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.GlobalPos;

public class GameEvents {
    public static final Event<GameEvents.NewDeathLocation> NEW_DEATH_LOCATION = EventFactory.createArrayBacked(NewDeathLocation.class, callbacks -> position -> {
        for (GameEvents.NewDeathLocation event : callbacks) {
            event.onNewDeathLocation(position);
        }
    });

    public static final Event<GameEvents.GameEnded> GAME_ENDED = EventFactory.createArrayBacked(GameEnded.class, callbacks -> () -> {
        for (GameEvents.GameEnded event : callbacks) {
            event.onGameEnd();
        }
    });

    @FunctionalInterface
    public interface NewDeathLocation {
        void onNewDeathLocation(GlobalPos position);
    }

    @FunctionalInterface
    public interface GameEnded {
        void onGameEnd();
    }
}
