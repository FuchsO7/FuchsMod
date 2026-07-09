package de.fuchsmod.features.general;

import de.fuchsmod.FuchsMod;
import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import static de.fuchsmod.FuchsMod.LOGGER;

public class FPSHud {
    private static final Minecraft client = Minecraft.getInstance();
    private static final FuchsModConfig config = FuchsModConfigManager.getInstance();

    public static void init() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(FuchsMod.MOD_ID, "fps_hud"),
                FPSHud::extract);
        LOGGER.info("Initialized FPS Measurement!");
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
        final int darK_green = 0xFF00AA00;
        final int yellow = 0xFFFFFF55;
        final int dark_red = 0xFFAA0000;
        if (fps <= BREAKPOINT) {
            return ARGB.linearLerp((float) fps / BREAKPOINT, dark_red, yellow);
        } else {
            return ARGB.linearLerp(Float.min((float) (fps - BREAKPOINT) / 40, 1f), yellow, darK_green);
        }
    }

    private static int getFPSColor(int fps) {
        return config.useContinuousColorsForFPSHud ? getContinuousFPSColor(fps) : getDiscreteFPSColor(fps).getValue();
    }

    public static Component getCurrentFPSFormatted() {
        return Component.literal("%d".formatted(client.getFps())).withColor(getFPSColor(client.getFps()));
    }
}
