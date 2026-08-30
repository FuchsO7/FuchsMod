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

import static de.fuchsmod.FuchsMod.CONFIG;

public class CalculatorScreen extends Screen {
    private final Screen parent;

    private static final int DEFAULT_SPACING = 4;
    private static final int DEFAULT_SIZE = 20;

    private boolean showFunctions = CONFIG.showFunctionsOnCalculatorScreenOpen;

    private final EditBox expressionBox = new EditBox(font, Component.literal(""));;

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
        expressionBox.setMaxLength(1024);
        expressionBox.setCanLoseFocus(false);
        expressionBox.setFocused(true);
        content.addChild(expressionBox);

        LinearLayout numpadLayout = content.addChild(LinearLayout.horizontal().spacing(DEFAULT_SPACING));
        numpadLayout.defaultCellSetting().alignVerticallyMiddle();

        GridLayout numberGridLayout = numpadLayout.addChild(new GridLayout().spacing(DEFAULT_SPACING));
        GridLayout.RowHelper helper = numberGridLayout.createRowHelper(showFunctions ? 8 : 5);

        String[] buttonLabels = showFunctions ? new String[]{
                "C", "D", "<", ">", "sin", "asin", "7", "8", "9", "+", "cos", "acos", "4", "5", "6", "-", "tan", "atan",
                "1", "2", "3", "*", "(", ")", "sign", "=", "0", ".", "/", "%", "^", "abs", "ln", "lg", "sqrt", "round", "log", ","
        } : new String[]{
                "C", "D", "<", ">", ",", "7", "8", "9", "+", "(", "4", "5", "6", "-", ")", "1", "2", "3", "*", "^", "=", "0", ".", "/", "%"
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
            } else if (buttonLabel.equals("C")) {
                onPress = (_) -> expressionBox.setValue("");
            } else if (buttonLabel.equals("D")) {
                onPress = (_) -> removeCharFromExpressionBox(expressionBox.getCursorPosition() - 1);
            } else if (buttonLabel.equals("<")) {
                onPress = (_) -> expressionBox.moveCursor(-1, false);
            } else if (buttonLabel.equals(">")) {
                onPress = (_) -> expressionBox.moveCursor(1, false);
            } else if (buttonLabel.length() == 1) {
                onPress = (button) -> addTextToExpressionBox(button.getMessage().getString());
            } else {
                onPress = (button) -> addTextToExpressionBox(button.getMessage().getString() + "(");
                buttonWidthScaling = 2;
            }

            Button button = Button.builder(Component.literal(buttonLabel), onPress::accept).build();
            button.setSize(buttonWidthScaling * DEFAULT_SIZE + (buttonWidthScaling - 1) * DEFAULT_SPACING, DEFAULT_SIZE);
            helper.addChild(button, buttonWidthScaling);
        }

        Button hideFunctionsButton = Button.builder(Component.literal(showFunctions ? "+/-" : "f(x)"), (button) -> {
            showFunctions = !showFunctions;
            this.rebuildWidgets();
        }).build();
        hideFunctionsButton.setSize(2 * DEFAULT_SIZE, DEFAULT_SIZE);
        numpadLayout.addChild(hideFunctionsButton);

        layout.arrangeElements();
        layout.visitWidgets(this::addRenderableWidget);
    }

    private void addTextToExpressionBox(String text) {
        int cursorPos = expressionBox.getCursorPosition();
        expressionBox.setValue(
                new StringBuilder(expressionBox.getValue()).insert(expressionBox.getCursorPosition(), text).toString()
        );
        expressionBox.moveCursorTo(cursorPos + text.length(), false);
    }

    private void removeCharFromExpressionBox(int position) {
        if (!expressionBox.getValue().isEmpty() && expressionBox.getCursorPosition() != 0) {
            int cursorPos = expressionBox.getCursorPosition();
            expressionBox.setValue(
                    new StringBuilder(expressionBox.getValue()).deleteCharAt(position).toString()
            );
            expressionBox.moveCursorTo(cursorPos -1, false);
        }
    }
}
