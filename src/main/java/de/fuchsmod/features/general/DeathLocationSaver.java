package de.fuchsmod.features.general;

import de.fuchsmod.events.GameEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import static de.fuchsmod.FuchsMod.FUCHSMOD_CHAT_MESSAGE_PREFIX;
import static de.fuchsmod.FuchsMod.LOGGER;
import static de.fuchsmod.FuchsMod.CLIENT;
import static de.fuchsmod.FuchsMod.CONFIG;

public class DeathLocationSaver {
    public static String dimension;
    public static BlockPos blockPos;

    public static void init () {
        GameEvents.NEW_DEATH_LOCATION.register((position) -> {
            dimension = position.dimension().identifier().getPath();
            blockPos = position.pos();
            LOGGER.info("Saved Death Location %s".formatted(getLastDeathLocationString()));
            if (CONFIG.sendLastDeathLocationMessage && CLIENT.player != null)
                CLIENT.player.sendSystemMessage(getLastDeathLocationFormatted());
        });
    }

    public static Component getLastDeathLocationFormatted() {
        return FUCHSMOD_CHAT_MESSAGE_PREFIX.get()
                .append(Component.translatable("fuchsmod.features.last_death_position.message"))
                .withStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent.CopyToClipboard(getLastDeathLocationString()))
                        .withHoverEvent(new HoverEvent.ShowText(Component.literal(getLastDeathLocationString()))));
    }

    public static String getLastDeathLocationString() {
        return String.format("x: %d, y: %d, z: %d, dimension: %s",
                blockPos.getX(), blockPos.getY(), blockPos.getZ(), dimension);
    }
}
