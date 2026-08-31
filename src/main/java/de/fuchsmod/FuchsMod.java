package de.fuchsmod;

import de.fuchsmod.commands.Commands;
import de.fuchsmod.commands.Debug;
import de.fuchsmod.compatibility.Skyblocker;
import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import de.fuchsmod.features.general.*;
import de.fuchsmod.features.partycommands.PartyCommandUtils;
import de.fuchsmod.features.partycommands.PartyCommands;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class FuchsMod implements ClientModInitializer {
	public static final String MOD_ID = "fuchs-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Minecraft CLIENT = Minecraft.getInstance();
	public static final FuchsModConfig CONFIG = FuchsModConfigManager.initInstance();
	public static final KeyMapping.Category KEYMAPPING_CATEGORY = KeyMapping.Category.register(
			Identifier.fromNamespaceAndPath(FuchsMod.MOD_ID, "fuchsmod_category")
	);
	public static final Supplier<MutableComponent> FUCHSMOD_CHAT_MESSAGE_PREFIX = () -> Component.empty()
			.append(Component.literal("[FuchsMod]: ").withStyle(ChatFormatting.GOLD));
	public static final Supplier<MutableComponent> FUCHSMOD_DEBUG_CHAT_MESSAGE_PREFIX = () -> Component.empty()
			.append(Component.literal("[FuchsMod Debug]: ").withStyle(ChatFormatting.GOLD));

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Fuchs Mod!");
		TPSHud.init();
		FPSHud.init();
		PingHud.init();
		TooltipScroll.init();
		Fullbright.init();
		Zoom.init();
		PartyCommandUtils.init();
		PartyCommands.init();
		ResourcePackIgnore.init();
		DeathLocationSaver.init();
		Debug.init();
		Commands.init();
		if (FabricLoader.getInstance().isModLoaded("skyblocker"))
			Skyblocker.register();
		LOGGER.debug("Initializing Fuchs Mod completed!");
	}
}