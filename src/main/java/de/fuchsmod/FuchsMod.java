package de.fuchsmod;

import de.fuchsmod.commands.Commands;
import de.fuchsmod.config.FuchsModConfigManager;
import de.fuchsmod.features.*;
import de.fuchsmod.features.partycommands.PartyCommandUtils;
import de.fuchsmod.features.partycommands.PartyCommands;
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
		LOGGER.info("Initialized Config Manager!");
		TPSHud.init();
		LOGGER.info("Initialized TPS Measurement!");
		FPSHud.init();
		LOGGER.info("Initialized FPS Measurement!");
		PingHud.init();
		LOGGER.info("Initialized Ping Measurement!");
		TooltipScroll.init();
		LOGGER.info("Initialized Tooltip Scrolling!");
		PartyCommandUtils.init();
		PartyCommands.init();
		LOGGER.info("Initialized Party Commands!");
		Commands.init();
		LOGGER.info("Initialized Fuchs Mod Commands!");
		LOGGER.info("Initializing Fuchs Mod completed!");
	}
}