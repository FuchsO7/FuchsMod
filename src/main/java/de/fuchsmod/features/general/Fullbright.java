package de.fuchsmod.features.general;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;

import static de.fuchsmod.FuchsMod.LOGGER;

public class Fullbright {
    private static final FuchsModConfig config = FuchsModConfigManager.getInstance();
    private static final Minecraft client = Minecraft.getInstance();

    public static void init() {
        ClientPlayConnectionEvents.JOIN.register((packetListener, packetSender, client) -> {
            setGamma();
        });
        LOGGER.info("Initialized Fullbright!");
    }

    public static void setGamma() {
        if (config.enableCustomGamma) {
            client.options.gamma().value = config.customGamma;
        }
    }
}

