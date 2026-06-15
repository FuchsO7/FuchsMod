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

import java.lang.Math;

public class TPSHud {
    public static final Minecraft client = Minecraft.getInstance();

    public static void init() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(FuchsMod.MOD_ID, "tps_hud"),
                TPSHud::extract);
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        FuchsModConfig config = FuchsModConfigManager.get();
        if (config.showTPSHud) {
            int x = (int) Math.round(config.TPSHudXPos / 100.0 * client.getWindow().getGuiScaledWidth());
            int y = (int) Math.round(config.TPSHudYPos / 100.0 * client.getWindow().getGuiScaledHeight());
            Component text = Component.literal("TPS: ")
                    .append(TPSMeasurement.INSTANCE.getAverageTPSFormatted());
            graphics.text(client.font, text, x, y, 0xFFFFFFFF, true);
        }
    }
}
