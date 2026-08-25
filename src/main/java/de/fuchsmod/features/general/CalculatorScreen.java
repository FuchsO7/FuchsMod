package de.fuchsmod.features.general;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class CalculatorScreen extends Screen {
    private final Screen parent;

    private static final int DEFAULT_SPACING = 4;
    private static final int DEFAULT_SIZE = 20;

    private boolean showFunctions = true;

    private EditBox expressionBox = new EditBox(font, Component.literal(""));;

    public CalculatorScreen(Screen parent) {
        super(Component.literal("Calculator"));
        this.parent = parent;
    }

    @Override
    public void onClose() {
        minecraft.gui.setScreen(this.parent);
    }

    @Override
    protected void init() {
        HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
        layout.addTitleHeader(getTitle(), font);
        LinearLayout footerLayout = layout.addToFooter(LinearLayout.horizontal().spacing(DEFAULT_SPACING));
        footerLayout.addChild(Button.builder(CommonComponents.GUI_DONE, _ -> onClose()).build());
        LinearLayout content = layout.addToContents(LinearLayout.vertical().spacing(DEFAULT_SPACING));
        content.defaultCellSetting().alignHorizontallyCenter();
        expressionBox.setSize(width / 2, 20);
        expressionBox.setMaxLength(255);
        content.addChild(expressionBox);

        LinearLayout numpadLayout = content.addChild(LinearLayout.horizontal().spacing(DEFAULT_SPACING));
        numpadLayout.defaultCellSetting().alignVerticallyMiddle();

        GridLayout numberGridLayout = numpadLayout.addChild(new GridLayout().spacing(DEFAULT_SPACING));
        GridLayout.RowHelper helper = numberGridLayout.createRowHelper(showFunctions ? 8 : 5);

        String[] buttonLabels = showFunctions ? new String[]{
                "Clear", "Delete", "sin", "asin", "7", "8", "9", "+", "cos", "acos", "4", "5", "6", "-", "tan", "atan",
                "1", "2", "3", "*", "(", ")", "sign", "=", "0", ".", "/", "%", "^", "abs", "ln", "lg", "sqrt", "round", "log", ","
        } : new String[]{
                "Clear", "Delete", ",", "7", "8", "9", "+", "(", "4", "5", "6", "-", ")", "1", "2", "3", "*", "^", "=", "0", ".", "/", "%"
        };

        for (String buttonLabel : buttonLabels) {
            Consumer<Button> onPress;
            int buttonWidthScaling = 1;
            if (buttonLabel.equals("=")) {
                onPress = (_) -> {
                    try {
                        double result = Calculator.calculateExpression(expressionBox.getValue());
                        expressionBox.setValue(Calculator.roundResult(result));
                    } catch (Calculator.CalculatorException e) {
                        expressionBox.setValue(e.getMessage());
                    }
                };
            } else if (buttonLabel.equals("Clear")) {
                onPress = (_) -> expressionBox.setValue("");
                buttonWidthScaling = 2;
            } else if (buttonLabel.equals("Delete")) {
                onPress = (_) -> {
                    if (!expressionBox.getValue().isEmpty())
                        expressionBox.setValue(expressionBox.getValue().substring(0, expressionBox.getValue().length() - 1));
                };
                buttonWidthScaling = 2;
            } else if (buttonLabel.length() == 1) {
                onPress = (button) -> expressionBox.setValue(expressionBox.getValue() + button.getMessage().getString());
            } else {
                onPress = (button) -> expressionBox.setValue(expressionBox.getValue() + button.getMessage().getString() + "(");
                buttonWidthScaling = 2;
            }

            Button button = Button.builder(Component.literal(buttonLabel), onPress::accept).build();
            button.setSize(buttonWidthScaling * DEFAULT_SIZE + (buttonWidthScaling - 1) * DEFAULT_SPACING, DEFAULT_SIZE);
            helper.addChild(button, buttonWidthScaling);
        }

        Button hideFunctionsButton = Button.builder(Component.literal(showFunctions ? "+-" : "f(x)"), (button) -> {
            showFunctions = !showFunctions;
            this.rebuildWidgets();
        }).build();
        hideFunctionsButton.setSize(2 * DEFAULT_SIZE, DEFAULT_SIZE);
        numpadLayout.addChild(hideFunctionsButton);

        layout.arrangeElements();
        layout.visitWidgets(this::addRenderableWidget);
    }
}
