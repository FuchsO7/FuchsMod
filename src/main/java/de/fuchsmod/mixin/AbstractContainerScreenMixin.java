package de.fuchsmod.mixin;

import de.fuchsmod.features.general.Calculator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.fuchsmod.FuchsMod.CONFIG;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin extends Screen {
    @Unique
    private static final int DEFAULT_SIZE = 20;
    @Unique
    private static final int DEFAULT_SPACING = 4;
    @Unique
    private final EditBox expressionBox = new EditBox(this.font, Component.literal(""));

    private AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Inject(
            at = @At("HEAD"),
            method = "init"
    )
    private void fuchsmod$addInventoryCalculatorWidgets(CallbackInfo ci) {
        if (!CONFIG.showInventoryCalculator)
            return;

        LinearLayout layout = LinearLayout.horizontal().spacing(DEFAULT_SPACING);
        layout.defaultCellSetting().alignVerticallyMiddle();

        Button calculateButton = Button.builder(Component.literal("="), (button -> {
            try {
                double result = Calculator.calculateExpression(expressionBox.getValue());
                expressionBox.setValue(Calculator.roundResult(result));
            } catch (Calculator.CalculatorException e) {
                expressionBox.setValue(e.getMessage());
            }
        })).build();
        calculateButton.setSize(DEFAULT_SIZE, DEFAULT_SIZE);
        layout.addChild(calculateButton);

        expressionBox.setSize(width / 2, DEFAULT_SIZE);
        expressionBox.setMaxLength(1024);
        layout.addChild(expressionBox);

        layout.arrangeElements();
        layout.setPosition(width / 2 - layout.getWidth() / 2, height - DEFAULT_SIZE - DEFAULT_SPACING);
        layout.visitWidgets(this::addRenderableWidget);
    }
}
