package de.fuchsmod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	@Unique
	private static final Minecraft client = Minecraft.getInstance();

	@Inject(
			at = @At("RETURN"),
			method = "handleSetTime")
	private void handleSetTime(ClientboundSetTimePacket packet, CallbackInfo info) {
		if (client.player != null) {
			long gameTime = packet.gameTime();
		}
	}

	@Inject(
			at = @At("RETURN"),
			method = "handlePongResponse")
	private void handlePong(ClientboundPongResponsePacket packet, CallbackInfo info) {
		if (client.player != null) {
			long time = packet.time();
			long ping = Util.getMillis() - time;
		}
	}
}

