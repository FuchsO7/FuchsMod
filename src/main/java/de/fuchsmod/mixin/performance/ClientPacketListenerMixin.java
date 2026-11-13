package de.fuchsmod.mixin.performance;

import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static de.fuchsmod.FuchsMod.CONFIG;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

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
}
