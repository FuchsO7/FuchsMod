package de.fuchsmod.features.general;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import static de.fuchsmod.FuchsMod.LOGGER;
import static de.fuchsmod.FuchsMod.CLIENT;
import static de.fuchsmod.FuchsMod.CONFIG;

public class Fullbright {

    public static void init() {
        ClientPlayConnectionEvents.JOIN.register((packetListener, packetSender, client) -> {
            setGamma();
        });
        LOGGER.debug("Initialized Fullbright!");
    }

    public static void setGamma() {
        if (CONFIG.enableCustomGamma) {
            CLIENT.options.gamma().value = CONFIG.customGamma;
        }
    }
}

