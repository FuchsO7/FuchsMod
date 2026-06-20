package de.fuchsmod.features;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import org.joml.Vector2i;

public class TooltipScroll {
    public static final TooltipScroll INSTANCE = new TooltipScroll();
    public static final FuchsModConfig config = FuchsModConfigManager.getInstance();

    public int x = 0;
    public int y = 0;

    public TooltipScroll() {
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            resetOffset();
            if (config.enableTooltipScroll && client.player != null) {
                ScreenMouseEvents.afterMouseScroll(screen).register((screenInstance, mouseX, mouseY, horizontalAmount, verticalAmount, consumed) -> {
                    if (!consumed) {
                        if (client.hasShiftDown()) {
                            this.x += (int) verticalAmount * config.scrollFactor * config.horizontalScrollDirection;
                        } else {
                            this.y += (int) verticalAmount * config.scrollFactor * config.verticalScrollDirection;
                        }
                    }
                    return consumed;
                });
            }
        });
    }

    public static TooltipScroll getInstance() {
        return INSTANCE;
    }

    public Vector2i getOffset() {
        return new Vector2i(this.x, this.y);
    }

    public void resetOffset() {
        this.x = 0;
        this.y = 0;
    }
}
