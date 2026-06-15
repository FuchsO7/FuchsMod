package de.fuchsmod.mixin;

import de.fuchsmod.features.PingMeasurement;
import de.fuchsmod.features.TPSMeasurement;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	@Inject(
			at = @At("RETURN"),
			method = "handleSetTime")
	private void handleSetTime(ClientboundSetTimePacket packet, CallbackInfo info) {
		TPSMeasurement.INSTANCE.onSetTimePacket(packet);
	}

	@Inject(
			at = @At("RETURN"),
			method = "handlePongResponse")
	private void handlePong(ClientboundPongResponsePacket packet, CallbackInfo info) {
		PingMeasurement.INSTANCE.onPongResponsePacket(packet);
	}
}

