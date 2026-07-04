package de.fuchsmod.config.controllers;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.AbstractControllerBuilderImpl;

public class PartyCommandControllerBuilder extends AbstractControllerBuilderImpl<PartyCommandRecord> {
    protected PartyCommandControllerBuilder(Option<PartyCommandRecord> option) {
        super(option);
    }

    public static PartyCommandControllerBuilder create(Option<PartyCommandRecord> opt) {
        return new PartyCommandControllerBuilder(opt);
    }

    @Override
    public Controller<PartyCommandRecord> build() {
        return PartyCommandController.createInternal(option);
    }
}
