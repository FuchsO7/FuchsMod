package de.fuchsmod.mixin.commands;

import de.fuchsmod.commands.Debug;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.regex.PatternSyntaxException;

import static de.fuchsmod.FuchsMod.CONFIG;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(
            at = @At(value = "HEAD"),
            method = "verifyCommand",
            cancellable = true
    )
    private void fuchsmod$verifyCommand(String command, CallbackInfoReturnable<ClientPacketListener.CommandCheckResult> cir) {
        if (CONFIG.confirmEveryCommand) {
            cir.setReturnValue(ClientPacketListener.CommandCheckResult.NO_ISSUES);
            cir.cancel();
            return;
        }

        for (String whitelistedCommandRegex : CONFIG.whitelistedCommandPatterns) {
            try {
                if (command.matches(whitelistedCommandRegex)) {
                    cir.setReturnValue(ClientPacketListener.CommandCheckResult.NO_ISSUES);
                    cir.cancel();
                    return;
                }
            }
            catch (PatternSyntaxException exception) {
                Debug.sendDebugMessage("Invalid Regex %s: %s".formatted(whitelistedCommandRegex, exception.getDescription()));
            }
        }
    }
}
