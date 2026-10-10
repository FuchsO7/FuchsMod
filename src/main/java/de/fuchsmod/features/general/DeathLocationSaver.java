package de.fuchsmod.features.general;

import de.fuchsmod.events.GameEvents;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import static de.fuchsmod.FuchsMod.FUCHSMOD_CHAT_MESSAGE_PREFIX;
import static de.fuchsmod.FuchsMod.LOGGER;
import static de.fuchsmod.FuchsMod.CLIENT;
import static de.fuchsmod.FuchsMod.CONFIG;

public class DeathLocationSaver {

    public static void init () {
        GameEvents.NEW_DEATH_LOCATION.register((globalPos) -> {
            LOGGER.info("Saved Death Location %s".formatted(getLastDeathLocationString(globalPos)));
            if (CONFIG.sendLastDeathLocationMessage && CLIENT.player != null)
                CLIENT.player.sendSystemMessage(getLastDeathLocationFormatted(globalPos));
        });
        LOGGER.debug("initialized Death Location Saver!");
    }

    public static Component getLastDeathLocationFormatted(GlobalPos globalPos) {
        return FUCHSMOD_CHAT_MESSAGE_PREFIX.get()
                .append(Component.translatable("fuchsmod.features.last_death_position.message"))
                .withStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent.CopyToClipboard(getLastDeathLocationString(globalPos)))
                        .withHoverEvent(new HoverEvent.ShowText(Component.literal(getLastDeathLocationString(globalPos)))));
    }

    public static String getLastDeathLocationString(GlobalPos globalPos) {
        return String.format("x: %d, y: %d, z: %d, dimension: %s",
                globalPos.pos().getX(), globalPos.pos().getY(), globalPos.pos().getZ(),
                globalPos.dimension().identifier().getPath());
    }
}
