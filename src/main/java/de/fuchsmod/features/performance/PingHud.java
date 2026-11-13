package de.fuchsmod.features.performance;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;

import static de.fuchsmod.FuchsMod.MOD_ID;
import static de.fuchsmod.FuchsMod.LOGGER;
import static de.fuchsmod.FuchsMod.CLIENT;
import static de.fuchsmod.FuchsMod.CONFIG;

public class PingHud {
    private static final Formatter formatter = new Formatter(
            0,
            new double[]{50.0, 150.0, 250.0, 400.0, Double.POSITIVE_INFINITY},
            (value, threshold) -> value <= threshold,
            400.0, 150.0, 50.0
    );

    public static void init() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "ping_hud"),
                PingHud::extract);
        LOGGER.debug("Initialized Ping Measurement!");
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        if (CLIENT.level == null || !CONFIG.showPingHud || CLIENT.getDebugOverlay().showDebugScreen())
            return;
        int x = (int) Math.round(CONFIG.PingHudXPos / 100.0 * CLIENT.getWindow().getGuiScaledWidth());
        int y = (int) Math.round(CONFIG.PingHudYPos / 100.0 * CLIENT.getWindow().getGuiScaledHeight());
        Component text = Component.translatable("fuchsmod.features.ping.hud", getAveragePingFormatted());
        graphics.text(CLIENT.font, text, x, y, 0xFFFFFFFF, true);
    }

    public static Component getCurrentPingFormatted() {
        return formatter.formatValue(PingMeasurement.getPing(), CONFIG.useContinuousColorsForPingHud);
    }

    public static Component getAveragePingFormatted() {
        if (!PingMeasurement.getInstance().hasEnoughSamplesForSignificantAverage()) {
            return Component.literal("???").withColor(TextColor.fromLegacyFormat(ChatFormatting.WHITE).getValue());
        } else {
            return formatter.formatValue(PingMeasurement.getAveragePing(), CONFIG.useContinuousColorsForPingHud);
        }
    }
}
