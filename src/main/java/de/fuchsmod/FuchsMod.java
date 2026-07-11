package de.fuchsmod;

import de.fuchsmod.commands.Commands;
import de.fuchsmod.commands.Debug;
import de.fuchsmod.config.FuchsModConfigManager;
import de.fuchsmod.features.general.*;
import de.fuchsmod.features.partycommands.PartyCommandUtils;
import de.fuchsmod.features.partycommands.PartyCommands;
import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FuchsMod implements ClientModInitializer {
	public static final String MOD_ID = "fuchs-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static KeyMapping.Category KEYMAPPING_CATEGORY = KeyMapping.Category.register(
			Identifier.fromNamespaceAndPath(FuchsMod.MOD_ID, "fuchsmod_category")
	);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Fuchs Mod!");
		FuchsModConfigManager.init();
		TPSHud.init();
		FPSHud.init();
		PingHud.init();
		TooltipScroll.init();
		Fullbright.init();
		Zoom.init();
		PartyCommandUtils.init();
		PartyCommands.init();
		Debug.init();
		Commands.init();
		LOGGER.info("Initializing Fuchs Mod completed!");
	}
}