package de.fuchsmod.config.controllers;

import com.mojang.blaze3d.platform.InputConstants;
import de.fuchsmod.config.screens.PartyCommandEditorScreen;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class PartyCommandController implements Controller<PartyCommandRecord> {
    private final Option<PartyCommandRecord> option;

    public PartyCommandController(Option<PartyCommandRecord> option) {
        this.option = option;
    }

    @Override
    public Option<PartyCommandRecord> option() {
        return this.option;
    }

    @Override
    public Component formatValue() {
        return Component.literal(option.pendingValue().trigger());
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new PartyCommandControllerElement(this, screen, widgetDimension);
    }

    public static PartyCommandController createInternal(Option<PartyCommandRecord> option) {
        return new PartyCommandController(option);
    }

    public static class PartyCommandControllerElement extends ControllerWidget<PartyCommandController> {
        public PartyCommandControllerElement(PartyCommandController control, YACLScreen screen, Dimension<Integer> dim) {
            super(control, screen, dim);
        }

        @Override
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
        }
        @Override
        public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
            if (!isMouseOver(event.x(), event.y()) || !isAvailable())
                return false;
            openEditorScreen();
            return true;
        }

        @Override
        public boolean keyPressed(@NonNull KeyEvent event) {
            if (!isFocused()) {
                return false;
            }

            if (event.key() == InputConstants.KEY_RETURN || event.key() == InputConstants.KEY_SPACE || event.key() == InputConstants.KEY_NUMPADENTER) {
                openEditorScreen();
                return true;
            }

            return false;
        }

        public void openEditorScreen() {
            client.gui.setScreen(new PartyCommandEditorScreen(client.gui.screen(), control.option()));
            playDownSound();
        }
    }
}
