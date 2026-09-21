package de.fuchsmod.mixin;

import de.fuchsmod.events.GameEvents;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Shadow
    private Optional<GlobalPos> lastDeathLocation;

    @Inject(
            at = @At("HEAD"),
            method = "setLastDeathLocation"
    )
    private void fuchsmod$saveDeathPosition(Optional<GlobalPos> pos, CallbackInfo ci) {
        if (pos.isEmpty() || lastDeathLocation.isEmpty())
            return;
        if (lastDeathLocation.equals(pos))
            return;

        GameEvents.NEW_DEATH_LOCATION.invoker().onNewDeathLocation(pos.get());
    }
}
