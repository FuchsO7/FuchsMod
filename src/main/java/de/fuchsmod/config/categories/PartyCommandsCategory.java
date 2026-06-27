package de.fuchsmod.config.categories;

import de.fuchsmod.config.FuchsModConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
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
                .build();
    }
}
