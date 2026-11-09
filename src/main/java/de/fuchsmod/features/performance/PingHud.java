package de.fuchsmod.features.performance;

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

public class PingHud {

    public static void init() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "ping_hud"),
                PingHud::extract);
        LOGGER.debug("Initialized Ping Measurement!");
    }

    // TODO: Do something about the formatter methods
    private static TextColor getDiscretePingColor(long ping) {
        if (ping < 50L) {
            return TextColor.DARK_GREEN;
        } else if (ping < 150L) {
            return TextColor.GREEN;
        } else if (ping < 250L) {
            return TextColor.YELLOW;
        } else if (ping < 400L) {
            return TextColor.RED;
        } else {
            return TextColor.DARK_RED;
        }
    }

    private static int getContinuousPingColor(long ping) {
        final int BREAKPOINT = 150;
        final int DARK_GREEN = 0xFF00AA00;
        final int YELLOW = 0xFFFFFF55;
        final int DARK_RED = 0xFFAA0000;
        if (ping <= BREAKPOINT) {
            return ARGB.linearLerp((float) ping / BREAKPOINT, DARK_GREEN, YELLOW);
        } else {
            return ARGB.linearLerp(Float.min((float) (ping - BREAKPOINT) / 250, 1f), YELLOW, DARK_RED);
        }
    }

    private static int getPingColor(long ping) {
        return CONFIG.useContinuousColorsForPingHud ? getContinuousPingColor(ping) : getDiscretePingColor(ping).getValue();
    }

    public static Component getCurrentPingFormatted() {
        long ping = PingMeasurement.getPing();
        return Component.literal("%d".formatted(ping)).withColor(getPingColor(ping));
    }

    public static Component getAveragePingFormatted() {
        long averagePing = PingMeasurement.getAveragePing();
        if (!PingMeasurement.getInstance().hasEnoughSamplesForSignificantAverage()) {
            return Component.literal("???").withColor(TextColor.WHITE);
        } else {
            return Component.literal("%d".formatted(averagePing)).withColor(getPingColor(averagePing));
        }
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        if (CLIENT.level == null || !CONFIG.showPingHud || CLIENT.getDebugOverlay().showDebugScreen())
            return;
        int x = (int) Math.round(CONFIG.PingHudXPos / 100.0 * CLIENT.getWindow().getGuiScaledWidth());
        int y = (int) Math.round(CONFIG.PingHudYPos / 100.0 * CLIENT.getWindow().getGuiScaledHeight());
        Component text = Component.translatable("fuchsmod.features.ping.hud", getAveragePingFormatted());
        graphics.text(CLIENT.font, text, x, y, 0xFFFFFFFF, true);
    }
}
