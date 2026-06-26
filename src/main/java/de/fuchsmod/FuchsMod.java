package de.fuchsmod;

import de.fuchsmod.commands.Commands;
import de.fuchsmod.config.FuchsModConfigManager;
import de.fuchsmod.features.*;
import net.fabricmc.api.ClientModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FuchsMod implements ClientModInitializer {
	public static final String MOD_ID = "fuchs-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Fuchs Mod!");
		FuchsModConfigManager.init();
		TPSHud.init();
		FPSHud.init();
		PingHud.init();
		TooltipScroll.init();
		Commands.init();
	}
}