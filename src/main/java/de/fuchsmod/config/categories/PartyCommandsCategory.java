package de.fuchsmod.config.categories;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.controllers.PartyCommandControllerBuilder;
import de.fuchsmod.config.controllers.PartyCommandRecord;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.LongSliderControllerBuilder;
import net.minecraft.network.chat.Component;

public class PartyCommandsCategory {
    public static ConfigCategory create(FuchsModConfig defaults, FuchsModConfig config) {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("fuchsmod.config.partycommands"))
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("fuchsmod.config.partycommands"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.partycommands.enable"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.partycommands.enable.description")))
                                .binding(defaults.enablePartyCommands,
                                        () -> config.enablePartyCommands,
                                        newValue -> config.enablePartyCommands = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Long>createBuilder()
                                .name(Component.translatable("fuchsmod.config.partycommands.command_delay"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.partycommands.command_delay.description")))
                                .binding(defaults.commandDelay,
                                        () -> config.commandDelay,
                                        newValue -> config.commandDelay = newValue)
                                .controller(opt -> LongSliderControllerBuilder.create(opt)
                                        .range(0L, 3000L)
                                        .step(50L)
                                        .formatValue(value -> Component.literal("%d ms".formatted(value))))
                                .build())
                        .build())
                .group(ListOption.<PartyCommandRecord>createBuilder()
                        .name(Component.translatable("fuchsmod.config.partycommands.configure_commands"))
                        .description(OptionDescription.of(
                                Component.translatable("fuchsmod.config.partycommands.configure_commands.description")))
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
