package de.fuchsmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.fuchsmod.features.general.Zoom;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public class CameraMixin {
    @ModifyReturnValue(
            at = @At("RETURN"),
            method = "calculateFov"
    )
    private float fuchsmod$calculateNewFov(float fov) {
        return fov * Zoom.fovModifier;
    }
}
