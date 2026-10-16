package de.fuchsmod.mixin;

import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static de.fuchsmod.FuchsMod.CONFIG;

@Mixin(LightmapRenderStateExtractor.class)
public class LightmapRenderStateExtractorMixin {

    @ModifyVariable(
            at = @At("STORE"),
            method = "extract",
            name = "brightnessOption"
    )
    private float fuchsmod$setCustomBrightness(float brightnessOption) {
        if (CONFIG.enableCustomGamma)
            return CONFIG.customGamma;
        else
            return brightnessOption;
    }
}
