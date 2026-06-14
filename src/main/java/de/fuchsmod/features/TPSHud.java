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
                Identifier.fromNamespaceAndPath(FuchsMod.MOD_ID, "before_chat"),
                TPSHud::extract);
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        double[] tps = TPSMeasurement.INSTANCE.getTPS();
        FuchsModConfig config = FuchsModConfigManager.get();
        int x = (int) Math.round((double) config.TPSHudXPos / 100.0 * client.getWindow().getWidth());
        int y = (int) Math.round((double) config.TPSHudYPos / 100.0 * client.getWindow().getHeight());
        String text = "TPS: " + ((tps[2] >= 10.0) ?  "%.1f".formatted(tps[1]) : "???");
        graphics.text(client.font, Component.literal(text), x, y, 0xFFFFFFFF, true);
    }
}
