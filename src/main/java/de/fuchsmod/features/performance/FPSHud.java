package de.fuchsmod.features.performance;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;

import static de.fuchsmod.FuchsMod.MOD_ID;
import static de.fuchsmod.FuchsMod.LOGGER;
import static de.fuchsmod.FuchsMod.CLIENT;
import static de.fuchsmod.FuchsMod.CONFIG;

public class FPSHud {
    private static final Formatter formatter = new Formatter(
            0,
            new double[]{60.0, 30.0, 20.0, 10.0, 0.0},
            (value, threshold) -> value >= threshold,
            10.0, 20.0, 40.0
    );

    public static void init() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "fps_hud"),
                FPSHud::extract);
        LOGGER.debug("Initialized FPS Measurement!");
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        if (!CONFIG.showFPSHud || CLIENT.getDebugOverlay().showDebugScreen())
            return;
        int x = (int) Math.round(CONFIG.FPSHudXPos / 100.0 * CLIENT.getWindow().getGuiScaledWidth());
        int y = (int) Math.round(CONFIG.FPSHudYPos / 100.0 * CLIENT.getWindow().getGuiScaledHeight());
        Component text = Component.translatable("fuchsmod.features.fps.hud",
                getCurrentFPSFormatted());
        graphics.text(CLIENT.font, text, x, y, 0xFFFFFFFF, true);
    }

    public static Component getCurrentFPSFormatted() {
        return formatter.formatValue(CLIENT.getFps(), CONFIG.useContinuousColorsForFPSHud);
    }
}
