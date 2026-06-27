package de.fuchsmod.features;

import de.fuchsmod.FuchsMod;
import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class PingHud {
    private static final Minecraft client = Minecraft.getInstance();

    public static void init() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(FuchsMod.MOD_ID, "ping_hud"),
                PingHud::extract);
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        FuchsModConfig config = FuchsModConfigManager.getInstance();
        if (config.showPingHud && !client.getDebugOverlay().showDebugScreen()) {
            int x = (int) Math.round(config.PingHudXPos / 100.0 * client.getWindow().getGuiScaledWidth());
            int y = (int) Math.round(config.PingHudYPos / 100.0 * client.getWindow().getGuiScaledHeight());
            Component text = Component.literal("Ping: ")
                    .append(PingMeasurement.getInstance().getAveragePingFormatted())
                    .append(" ms");
            graphics.text(client.font, text, x, y, 0xFFFFFFFF, true);
        }
    }
}
