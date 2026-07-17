package de.fuchsmod.mixin;

import de.fuchsmod.events.ClientPacketEvents;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {
    @Inject(
            at = @At("HEAD"),
            method = "send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;Z)V"
    )
    private void fuchsmod$onPacketSend(Packet<?> packet, @Nullable ChannelFutureListener listener, boolean flush, CallbackInfo ci) {
        if (packet instanceof ServerboundResourcePackPacket resourcePackPacket) {
            ClientPacketEvents.RESOURCE_PACK_ACTION_PACKET.invoker().onResourcePackActionPacket(resourcePackPacket);
        }
    }
}
