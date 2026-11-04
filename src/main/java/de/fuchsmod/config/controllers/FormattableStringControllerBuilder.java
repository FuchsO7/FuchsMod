package de.fuchsmod.config.controllers;

import de.fuchsmod.commands.Debug;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionEventListener;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;

import java.util.function.Function;

public interface FormattableStringControllerBuilder extends StringControllerBuilder {
    FormattableStringControllerBuilder formatValue(ValueFormatter<String> formatter);

    FormattableStringControllerBuilder formatInvalidValue(ValueFormatter<String> formatter);

    FormattableStringControllerBuilder validateValue(Function<String, Boolean> validator);

    static FormattableStringControllerBuilder create(Option<String> option) {
        option.addEventListener((opt, event) -> {
            if (event == OptionEventListener.Event.INITIAL)
                Debug.sendDebugMessage("WAA");
        });
        return new FormattableStringControllerBuilderImpl(option);
    }
}