package de.fuchsmod.mixin;

import de.fuchsmod.events.ClientPacketEvents;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonPacketListenerImpl.class)
public class ClientCommonPacketListenerImplMixin{
    @Inject(
            at = @At("RETURN"),
            method = "handlePing"
    )
    private void fuchsmod$handlePing(ClientboundPingPacket packet, CallbackInfo info) {
        ClientPacketEvents.PING_PACKET.invoker().onPingPacket(packet);
    }

    @Inject(
            at = @At("HEAD"),
            method = "handleResourcePackPush"
    )
    private void fuchsmod$onResourcePackPush(ClientboundResourcePackPushPacket packet, CallbackInfo ci) {
        ClientPacketEvents.RESOURCE_PACK_PUSH_PACKET.invoker().onResourcePackPushPacket(packet);
    }

    @Inject(
            at = @At("HEAD"),
            method = "handleResourcePackPop"
    )
    private void fuchsmod$onResourcePackPop(ClientboundResourcePackPopPacket packet, CallbackInfo ci) {
        ClientPacketEvents.RESOURCE_PACK_POP_PACKET.invoker().onResourcePackPopPacket(packet);
    }
}