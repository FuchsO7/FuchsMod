package de.fuchsmod.features.general;

import com.mojang.blaze3d.platform.InputConstants;
import de.fuchsmod.FuchsMod;
import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.event.client.player.ClientHotbarScrollEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import static de.fuchsmod.FuchsMod.LOGGER;

public class Zoom {
    private static final FuchsModConfig config = FuchsModConfigManager.getInstance();
    private static final Minecraft client = Minecraft.getInstance();
    private static double scrolls = 1;
    public static float fovModifier = 1.0f;
    private static KeyMapping zoomKey;

    public static void init() {
        zoomKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "Zoom",
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_C,
                        FuchsMod.KEYMAPPING_CATEGORY
                ));

        ClientTickEvents.END_LEVEL_TICK.register((clientLevel) -> {
            if (!client.hasControlDown())
                resetZoom();
            if (zoomKey.isDown())
                setZoom(config.immediateZoomFactor);
        });

        ClientHotbarScrollEvents.ALLOW.register((inventory, currentSlot, newSlot, xOffset, yOffset) -> {
            if (!client.hasControlDown() || zoomKey.isDown() || !config.enableZoom)
                return true;
            onMouseScroll(yOffset);
            return false;
        });
        LOGGER.info("Initialized Zoom!");
    }

    public static void onMouseScroll(double direction) {
        scrolls += config.zoomFactor * direction;
        if (scrolls < 1.0)
            scrolls = 1.0;
        else setZoom((float) (scrolls * scrolls));
    }

    public static void setZoom(float factor) {
        fovModifier = 1.0f / factor;
    }

    public static void resetZoom() {
        scrolls = 1.0;
        fovModifier = 1.0f;
    }
}
