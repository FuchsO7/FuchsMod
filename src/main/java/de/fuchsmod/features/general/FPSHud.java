package de.fuchsmod.features.general;

import de.fuchsmod.FuchsMod;
import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class FPSHud {
    private static final Minecraft client = Minecraft.getInstance();
    private static final FuchsModConfig config = FuchsModConfigManager.getInstance();

    public static void init() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(FuchsMod.MOD_ID, "fps_hud"),
                FPSHud::extract);
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        if (!config.showFPSHud || client.getDebugOverlay().showDebugScreen())
            return;
        int x = (int) Math.round(config.FPSHudXPos / 100.0 * client.getWindow().getGuiScaledWidth());
        int y = (int) Math.round(config.FPSHudYPos / 100.0 * client.getWindow().getGuiScaledHeight());
        Component text = Component.literal("FPS: ")
                .append(getCurrentFPSFormatted());
        graphics.text(client.font, text, x, y, 0xFFFFFFFF, true);
    }

    private static ChatFormatting getFPSColor(int fps) {
        if (fps >= 60) {
            return ChatFormatting.DARK_GREEN;
        } else if (fps >= 30) {
            return ChatFormatting.GREEN;
        } else if (fps >= 20) {
            return ChatFormatting.YELLOW;
        } else if (fps >= 10) {
            return ChatFormatting.RED;
        } else {
            return ChatFormatting.DARK_RED;
        }
    }

    public static Component getCurrentFPSFormatted() {
        return Component.literal("%d".formatted(client.getFps())).withStyle(getFPSColor(client.getFps()));
    }
}
