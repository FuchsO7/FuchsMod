package de.fuchsmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import de.fuchsmod.config.FuchsModConfigManager;
import de.fuchsmod.features.TooltipScroll;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DefaultTooltipPositioner.class)
public class DefaultTooltipPositionerMixin {
    @ModifyReturnValue(
            at = @At("RETURN"),
            method = "positionTooltip(IIIIII)Lorg/joml/Vector2ic;"
    )
    private Vector2ic moveTooltip(Vector2ic original) {
        if (FuchsModConfigManager.getInstance().enableTooltipScroll) {
            return new Vector2i(original.x(), original.y()).add(TooltipScroll.getInstance().getOffset());
        } else {
            return original;
        }
    }
}