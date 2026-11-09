package de.fuchsmod.features.performance;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import java.lang.Math;

import static de.fuchsmod.FuchsMod.MOD_ID;
import static de.fuchsmod.FuchsMod.LOGGER;
import static de.fuchsmod.FuchsMod.CLIENT;
import static de.fuchsmod.FuchsMod.CONFIG;

public class TPSHud {

    public static void init() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "tps_hud"),
                TPSHud::extract);
        LOGGER.debug("Initialized TPS Measurement!");
    }

    // TODO: Do something about the formatter methods
    private static TextColor getDiscreteTPSColor(double tps) {
        if (tps > 19.0) {
            return TextColor.DARK_GREEN;
        } else if (tps > 17.5) {
            return TextColor.GREEN;
        } else if (tps > 15.0) {
            return TextColor.YELLOW;
        } else if (tps > 10.0) {
            return TextColor.RED;
        } else {
            return TextColor.DARK_RED;
        }
    }

    private static int getContinuousTPSColor(double tps) {
        final double BREAKPOINT = 17.5;
        final int DARK_GREEN = 0xFF00AA00;
        final int YELLOW = 0xFFFFFF55;
        final int DARK_RED = 0xFFAA0000;
        if (tps <= BREAKPOINT) {
            return ARGB.linearLerp((float) Double.max(tps - 10, 0) / 7.5f, DARK_RED, YELLOW);
        } else {
            return ARGB.linearLerp(Float.min((float) (tps - BREAKPOINT) / 2.5f, 1f), YELLOW, DARK_GREEN);
        }
    }

    private static int getTPSColor(double tps) {
        return CONFIG.useContinuousColorsForTPSHud ? getContinuousTPSColor(tps) : getDiscreteTPSColor(tps).getValue();
    }

    public static Component getCurrentTPSFormatted() {
        double tps = TPSMeasurement.getTPS();
        return Component.literal("%.1f".formatted(tps)).withColor(getTPSColor(tps));
    }

    public static Component getAverageTPSFormatted() {
        double averageTPS = TPSMeasurement.getAverageTPS();
        if (!TPSMeasurement.getInstance().hasEnoughSamplesForSignificantAverage()) {
            return Component.literal("???").withColor(TextColor.WHITE);
        } else {
            return Component.literal("%.1f".formatted(averageTPS)).withColor(getTPSColor(averageTPS));
        }
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        if (CLIENT.level == null || !CONFIG.showTPSHud || CLIENT.getDebugOverlay().showDebugScreen())
            return;
        int x = (int) Math.round(CONFIG.TPSHudXPos / 100.0 * CLIENT.getWindow().getGuiScaledWidth());
        int y = (int) Math.round(CONFIG.TPSHudYPos / 100.0 * CLIENT.getWindow().getGuiScaledHeight());
        Component text = Component.translatable("fuchsmod.features.tps.hud", getAverageTPSFormatted());
        graphics.text(CLIENT.font, text, x, y, 0xFFFFFFFF, true);
    }
}
