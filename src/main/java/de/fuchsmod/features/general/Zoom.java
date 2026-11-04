package de.fuchsmod.features.general;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.event.client.player.ClientHotbarScrollEvents;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import static de.fuchsmod.FuchsMod.KEYMAPPING_CATEGORY;
import static de.fuchsmod.FuchsMod.LOGGER;
import static de.fuchsmod.FuchsMod.CLIENT;
import static de.fuchsmod.FuchsMod.CONFIG;

public class Zoom {
    private static double dynamicZoomFactor = 1.0;
    private static float zoomModifier = 1.0f;
    private static KeyMapping zoomKey;

    public static void init() {
        zoomKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.fuchsmod.zoom",
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_C,
                        KEYMAPPING_CATEGORY
                ));

        ClientTickEvents.END_CLIENT_TICK.register((clientLevel) -> {
            if (zoomKey.isDown())
                setZoom((float) (dynamicZoomFactor * dynamicZoomFactor) * CONFIG.immediateZoomFactor);
            else
                resetZoom();
        });

        ClientHotbarScrollEvents.ALLOW.register((inventory, currentSlot, newSlot, xOffset, yOffset) -> {
            if (!zoomKey.isDown() || !CONFIG.enableZoom)
                return true;
            updateDynamicZoomFactor(yOffset);
            return false;
        });
        LOGGER.debug("Initialized Zoom!");
    }

    public static void updateDynamicZoomFactor(double direction) {
        dynamicZoomFactor += CONFIG.dynamicZoomFactor * direction;
        if (dynamicZoomFactor < 1.0)
            dynamicZoomFactor = 1.0;
    }

    public static void setZoom(float factor) {
        if (CONFIG.smoothCameraOnZoom)
            CLIENT.options.smoothCamera = factor > 1.0f;
        zoomModifier = 1.0f / factor;
    }

    public static void resetZoom() {
        if (CONFIG.smoothCameraOnZoom)
            CLIENT.options.smoothCamera = false;
        dynamicZoomFactor = 1.0;
        zoomModifier = 1.0f;
    }

    public static float getZoomModifier() {
        return zoomModifier;
    }
}
