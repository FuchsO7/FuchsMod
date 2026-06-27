package de.fuchsmod.mixin;

import de.fuchsmod.features.Partycommands.PartyCommands;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @Inject(
            at = @At("RETURN"),
            method = "logChatMessage"
    )
    private void onChatMessage(GuiMessage message, CallbackInfo ci) {
        String messageString = message.content().getString()
                .replaceAll("\r", "\\\\r")
                .replaceAll("\n", "\\\\n")
                .replaceAll("§.", "");
        PartyCommands.onChatMessage(messageString);
    }
}