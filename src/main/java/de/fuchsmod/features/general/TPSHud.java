package de.fuchsmod.features.general;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

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

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        if (!CONFIG.showTPSHud || CLIENT.getDebugOverlay().showDebugScreen())
            return;
        int x = (int) Math.round(CONFIG.TPSHudXPos / 100.0 * CLIENT.getWindow().getGuiScaledWidth());
        int y = (int) Math.round(CONFIG.TPSHudYPos / 100.0 * CLIENT.getWindow().getGuiScaledHeight());
        Component text = Component.translatable("fuchsmod.features.tps.hud",
                TPSMeasurement.getInstance().getAverageTPSFormatted());
        graphics.text(CLIENT.font, text, x, y, 0xFFFFFFFF, true);
    }
}
