package de.fuchsmod.features.general;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import static de.fuchsmod.FuchsMod.MOD_ID;
import static de.fuchsmod.FuchsMod.LOGGER;
import static de.fuchsmod.FuchsMod.CLIENT;
import static de.fuchsmod.FuchsMod.CONFIG;

public class FPSHud {

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

    private static TextColor getDiscreteFPSColor(int fps) {
        if (fps >= 60) {
            return TextColor.DARK_GREEN;
        } else if (fps >= 30) {
            return TextColor.GREEN;
        } else if (fps >= 20) {
            return TextColor.YELLOW;
        } else if (fps >= 10) {
            return TextColor.RED;
        } else {
            return TextColor.DARK_RED;
        }
    }

    private static int getContinuousFPSColor(int fps) {
        final int BREAKPOINT = 20;
        final int DARK_GREEN = 0xFF00AA00;
        final int YELLOW = 0xFFFFFF55;
        final int DARK_RED = 0xFFAA0000;
        if (fps <= BREAKPOINT) {
            return ARGB.linearLerp((float) fps / BREAKPOINT, DARK_RED, YELLOW);
        } else {
            return ARGB.linearLerp(Float.min((float) (fps - BREAKPOINT) / 40, 1f), YELLOW, DARK_GREEN);
        }
    }

    private static int getFPSColor(int fps) {
        return CONFIG.useContinuousColorsForFPSHud ? getContinuousFPSColor(fps) : getDiscreteFPSColor(fps).getValue();
    }

    public static Component getCurrentFPSFormatted() {
        return Component.literal("%d".formatted(CLIENT.getFps())).withColor(getFPSColor(CLIENT.getFps()));
    }
}
