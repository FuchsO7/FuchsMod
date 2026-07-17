package de.fuchsmod.mixin;

import de.fuchsmod.features.general.ResourcePackIgnore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmScreen;

import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConfirmScreen.class)
public class ConfirmScreenMixin {
    @Unique
    private static final Minecraft client = Minecraft.getInstance();
    @Unique
    protected Component ignoreButtonComponent = Component.literal("Ignore");
    @Unique
    protected Button ignoreButton;

    @Inject(
            at = @At("RETURN"),
            method = "addButtons"
    )
    protected void fuchsmod$addIgnoreButton(LinearLayout buttonLayout, CallbackInfo ci) {
        if ((Object) this instanceof ClientCommonPacketListenerImpl.PackConfirmScreen packConfirmScreen) {
            this.ignoreButton = buttonLayout.addChild(Button.builder(this.ignoreButtonComponent, button -> {
                client.gui.setScreen(packConfirmScreen.parentScreen);
                ResourcePackIgnore.imitateResourcePackDownload();
            }).build());
        }
    }
}
