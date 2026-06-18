package de.fuchsmod.mixin;

import de.fuchsmod.features.TPSMeasurement;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonPacketListenerImpl.class)
public class ClientCommonPacketListenerImplMixin {
    @Inject(
            at = @At("RETURN"),
            method = "handlePing")
    private void handlePing(ClientboundPingPacket packet, CallbackInfo info) {
        TPSMeasurement.getInstance().onPingPacket(packet);
    }
}