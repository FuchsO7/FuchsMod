package de.fuchsmod.mixin;

import de.fuchsmod.events.ClientPacketEvents;
import de.fuchsmod.events.GameEvents;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static de.fuchsmod.FuchsMod.CLIENT;
import static de.fuchsmod.FuchsMod.CONFIG;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

	@Inject(
			at = @At("RETURN"),
			method = "handleSetTime"
	)
	private void fuchsmod$handleSetTime(ClientboundSetTimePacket packet, CallbackInfo info) {
		ClientPacketEvents.SET_TIME_PACKET.invoker().onSetTimePacket(packet);
	}

	@Inject(
			at = @At("RETURN"),
			method = "handlePongResponse"
	)
	private void fuchsmod$handlePong(ClientboundPongResponsePacket packet, CallbackInfo info) {
		ClientPacketEvents.PONG_RESPONSE_PACKET.invoker().onPongResponsePacket(packet);
	}

	@Redirect(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;showNetworkCharts()Z"),
			method = "tick()V"
	)
	private boolean fuchsmod$shouldSendPingRequest(DebugScreenOverlay instance) {
		if (CONFIG.alwaysSendPingRequest) {
			return true;
		} else {
			return instance.showNetworkCharts();
		}
	}

	@Inject(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V", shift = At.Shift.AFTER),
			method = "handleRespawn"
	)
	private void fuchsmod$saveDeathPosition(ClientboundRespawnPacket packet, CallbackInfo ci) {
		Optional<GlobalPos> globalPos = packet.commonPlayerSpawnInfo().lastDeathLocation();
		if (globalPos.isEmpty())
			return;
		if (globalPos.equals(CLIENT.player.getLastDeathLocation()))
			return;
		GameEvents.NEW_DEATH_LOCATION.invoker().onNewDeathLocation(globalPos.get());
	}
}

