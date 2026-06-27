package de.fuchsmod.features;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import org.joml.Vector2i;

public class TooltipScroll {
    private static final TooltipScroll INSTANCE = new TooltipScroll();
    private static final FuchsModConfig config = FuchsModConfigManager.getInstance();

    private int x = 0;
    private int y = 0;

    public static void init() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            TooltipScroll.getInstance().resetOffset();
            if (!config.enableTooltipScroll || client.player == null)
                return;
            ScreenMouseEvents.afterMouseScroll(screen).register((screenInstance, mouseX, mouseY, horizontalAmount, verticalAmount, consumed) -> {
                if (!consumed) {
                    getInstance().moveOffset((int) verticalAmount, client.hasShiftDown());
                }
                return consumed;
            });
        });
    }

    public static TooltipScroll getInstance() {
        return INSTANCE;
    }

    public void moveOffset(int distance, boolean moveVertical) {
        if (moveVertical) {
            this.x += distance * config.scrollFactor * config.horizontalScrollDirection;
        } else {
            this.y += distance * config.scrollFactor * config.verticalScrollDirection;
        }
    }

    public Vector2i getOffset() {
        return new Vector2i(this.x, this.y);
    }

    public void resetOffset() {
        this.x = 0;
        this.y = 0;
    }
}
