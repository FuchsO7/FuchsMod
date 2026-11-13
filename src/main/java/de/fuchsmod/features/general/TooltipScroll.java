package de.fuchsmod.features.general;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import org.joml.Vector2i;

import static de.fuchsmod.FuchsMod.LOGGER;
import static de.fuchsmod.FuchsMod.CONFIG;

public class TooltipScroll {
    private static final Vector2i tooltipOffset = new Vector2i(0, 0);

    public static void init() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            resetOffset();
            if (!CONFIG.enableTooltipScroll || client.player == null)
                return;
            ScreenMouseEvents.afterMouseScroll(screen).register((screenInstance, mouseX, mouseY, horizontalAmount, verticalAmount, consumed) -> {
                if (!consumed) {
                    moveOffset((int) verticalAmount, client.hasShiftDown());
                }
                return consumed;
            });
        });
        LOGGER.debug("Initialized Tooltip Scrolling!");
    }

    public static void moveOffset(int distance, boolean moveVertical) {
        if (moveVertical) {
            tooltipOffset.add(distance * CONFIG.scrollFactor * CONFIG.horizontalScrollDirection, 0);
        } else {
            tooltipOffset.add(0, distance * CONFIG.scrollFactor * CONFIG.verticalScrollDirection);
        }
    }

    public static void resetOffset() {
        tooltipOffset.set(0, 0);
    }

    public static Vector2i getTooltipOffset() {
        return tooltipOffset;
    }
}
