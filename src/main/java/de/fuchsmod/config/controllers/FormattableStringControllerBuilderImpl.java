package de.fuchsmod.config.controllers;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.impl.controller.AbstractControllerBuilderImpl;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class FormattableStringControllerBuilderImpl extends AbstractControllerBuilderImpl<String> implements FormattableStringControllerBuilder {
    private ValueFormatter<String> formatter = Component::literal;
    private ValueFormatter<String> invalidFormatter = value -> formatter.format(value).copy().withStyle(ChatFormatting.RED);
    private Function<String, Boolean> validator = _ -> true;

    public FormattableStringControllerBuilderImpl(Option<String> option) {
        super(option);
    }

    public static FormattableStringControllerBuilderImpl create(Option<String> opt) {
        return new FormattableStringControllerBuilderImpl(opt);
    }

    @Override
    public FormattableStringControllerBuilderImpl formatValue(ValueFormatter<String> formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public FormattableStringControllerBuilder formatInvalidValue(ValueFormatter<String> invalidFormatter) {
        this.invalidFormatter = invalidFormatter;
        return this;
    }

    @Override
    public FormattableStringControllerBuilder validateValue(Function<String, Boolean> validator) {
        this.validator = validator;
        return this;
    }

    @Override
    public Controller<String> build() {
        return new FormattableStringController(option, formatter, invalidFormatter, validator);
    }
}