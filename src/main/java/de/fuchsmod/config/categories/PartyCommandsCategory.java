package de.fuchsmod.config.categories;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.controllers.PartyCommandControllerBuilder;
import de.fuchsmod.config.controllers.PartyCommandRecord;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.network.chat.Component;

public class PartyCommandsCategory {
    public static ConfigCategory create(FuchsModConfig defaults, FuchsModConfig config) {
        return ConfigCategory.createBuilder()
                .name(Component.literal("Party Commands"))
                .group(OptionGroup.createBuilder()
                        .name(Component.literal("Party Commands"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Enable Party Commands"))
                                .description(OptionDescription.of(
                                        Component.literal("Enable Party Commands.")))
                                .binding(defaults.enablePartyCommands,
                                        () -> config.enablePartyCommands,
                                        newValue -> config.enablePartyCommands = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .build())
                .group(ListOption.<PartyCommandRecord>createBuilder()
                        .name(Component.literal("Configure Commands"))
                        .binding(defaults.partyCommandsList,
                                () -> config.partyCommandsList,
                                newValue -> config.partyCommandsList = newValue)
                        .controller(PartyCommandControllerBuilder::create)
                        .initial(new PartyCommandRecord(0, "", "", "None"))
                        .insertEntriesAtEnd(true)
                        .build())
                .build();
    }
}
