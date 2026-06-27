package de.fuchsmod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @Unique
    private static final Minecraft client = Minecraft.getInstance();

    @Inject(
            at = @At("RETURN"),
            method = "logChatMessage"
    )
    private void logChatMessage(GuiMessage message, CallbackInfo ci) {
        if (client.player != null && !message.content().getString().contains("TEST")) {
            String messageString = message.content().getString()
                    .replaceAll("\r", "\\\\r")
                    .replaceAll("\n", "\\\\n")
                    .replaceAll("§.", "");
        }
    }
}