package de.fuchsmod.config.controllers;

import de.fuchsmod.commands.Debug;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.string.IStringController;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public record FormattableStringController(Option<String> option, ValueFormatter<String> formatter, ValueFormatter<String> invalidFormatter, Function<String, Boolean> validator) implements IStringController<String> {

    @Override
    public Option<String> option() {
        return option;
    }

    @Override
    public String getString() {
        return option().pendingValue();
    }

    @Override
    public void setFromString(String value) {
        option().requestSet(value);
    }

    @Override
    public Component formatValue() {
        String value = getString();
        if (validateValue(value))
            return formatter.format(value);
        else
            return invalidFormatter.format(value);
    }

    public boolean validateValue(String value) {
        return validator.apply(value);
    }
}