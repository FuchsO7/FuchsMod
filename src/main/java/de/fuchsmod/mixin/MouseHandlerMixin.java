package de.fuchsmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.fuchsmod.features.general.Zoom;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @ModifyExpressionValue(
            at = @At(value = "INVOKE", target = "Ljava/lang/Double;doubleValue()D"),
            method = "turnPlayer"
    )
    private double fuchsmod$modifyMouseSensitivity(double sensitivity) {
        return sensitivity * Zoom.getZoomModifier();
    }
}
