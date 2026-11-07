package de.fuchsmod.events;

import de.fuchsmod.commands.Debug;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.GlobalPos;

import java.util.regex.PatternSyntaxException;

import static de.fuchsmod.FuchsMod.CONFIG;
import static de.fuchsmod.FuchsMod.LOGGER;

public class GameEvents {

    public static void register() {
        ChatEvents.ON_MESSAGE.register(message -> {
            if (!CONFIG.enableGameEndTrigger)
                return;
            String messageString = message.content().getString().replaceAll("§.", "");
            for (String regex : CONFIG.gameEndTriggerPatterns) {
                try {
                    if (messageString.matches(regex))
                        ON_GAME_END.invoker().onGameEnd();
                } catch (PatternSyntaxException exception) {
                    if (!messageString.contains("Invalid Regex"))
                        Debug.sendDebugMessage("Invalid Regex %s: %s".formatted(regex, exception.getDescription()));
                }
            }
        });
        LOGGER.debug("Registered Game End Event Triggers!");
    }

    public static final Event<GameEvents.NewDeathLocation> ON_NEW_DEATH_LOCATION = EventFactory.createArrayBacked(NewDeathLocation.class, callbacks -> position -> {
        for (GameEvents.NewDeathLocation event : callbacks) {
            event.onNewDeathLocation(position);
        }
    });

    public static final Event<GameEvents.GameEnded> ON_GAME_END = EventFactory.createArrayBacked(GameEnded.class, callbacks -> () -> {
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
