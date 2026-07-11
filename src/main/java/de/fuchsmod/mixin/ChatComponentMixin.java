package de.fuchsmod.mixin;

import de.fuchsmod.events.ChatEvents;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @Inject(
            at = @At("RETURN"),
            method = "logChatMessage"
    )
    private void fuchsmod$onChatMessage(GuiMessage message, CallbackInfo ci) {
        ChatEvents.MESSAGE.invoker().onMessage(message);
    }
}