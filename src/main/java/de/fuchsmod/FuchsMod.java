package de.fuchsmod;

import de.fuchsmod.commands.Commands;
import de.fuchsmod.config.FuchsModConfigManager;
import de.fuchsmod.features.PingHud;
import de.fuchsmod.features.PingMeasurement;
import de.fuchsmod.features.TPSHud;
import de.fuchsmod.features.TPSMeasurement;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FuchsMod implements ClientModInitializer {
	public static final String MOD_ID = "fuchs-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Fuchs Mod!");
		FuchsModConfigManager.init();
		Commands.init();
		TPSHud.init();
		PingHud.init();
	}
}