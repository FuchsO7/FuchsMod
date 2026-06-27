package de.fuchsmod.mixin;

import de.fuchsmod.config.FuchsModConfigManager;
import de.fuchsmod.features.PingMeasurement;
import de.fuchsmod.features.TPSMeasurement;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	@Inject(
			at = @At("RETURN"),
			method = "handleSetTime"
	)
	private void handleSetTime(ClientboundSetTimePacket packet, CallbackInfo info) {
		TPSMeasurement.getInstance().onSetTimePacket(packet);
	}

	@Inject(
			at = @At("RETURN"),
			method = "handlePongResponse"
	)
	private void handlePong(ClientboundPongResponsePacket packet, CallbackInfo info) {
		PingMeasurement.getInstance().onPongResponsePacket(packet);
	}

	@Redirect(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;showNetworkCharts()Z"),
			method = "tick()V"
	)
	private boolean shouldSendPingRequest(DebugScreenOverlay instance) {
		if (FuchsModConfigManager.getInstance().alwaysSendPingRequest) {
			return true;
		} else {
			return instance.showNetworkCharts();
		}
	}
}

