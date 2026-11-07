package de.fuchsmod.mixin.events;

import de.fuchsmod.events.ClientPacketEvents;
import de.fuchsmod.events.GameEvents;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static de.fuchsmod.FuchsMod.CLIENT;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

	@Inject(
			at = @At("RETURN"),
			method = "handleSetTime"
	)
	private void fuchsmod$handleSetTime(ClientboundSetTimePacket packet, CallbackInfo info) {
		ClientPacketEvents.ON_SET_TIME_PACKET.invoker().onSetTimePacket(packet);
	}

	@Inject(
			at = @At("RETURN"),
			method = "handlePongResponse"
	)
	private void fuchsmod$handlePong(ClientboundPongResponsePacket packet, CallbackInfo info) {
		ClientPacketEvents.ON_PONG_RESPONSE_PACKET.invoker().onPongResponsePacket(packet);
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
		GameEvents.ON_NEW_DEATH_LOCATION.invoker().onNewDeathLocation(globalPos.get());
	}
}

