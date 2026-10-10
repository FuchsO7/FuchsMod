package de.fuchsmod.config.categories;

import de.fuchsmod.config.FuchsModConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.network.chat.Component;

public class CommandWhitelistCategory {
    public static ConfigCategory create(FuchsModConfig defaults, FuchsModConfig config) {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("fuchsmod.config.command_whitelist"))
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("fuchsmod.config.command_whitelist.allow_everything"))
                        .description(OptionDescription.of(
                                Component.translatable("fuchsmod.config.command_whitelist.allow_everything.description")))
                        .binding(defaults.confirmEveryCommand,
                                () -> config.confirmEveryCommand,
                                newValue -> config.confirmEveryCommand = newValue)
                        .controller(opt -> BooleanControllerBuilder.create(opt)
                                .coloured(true))
                        .build())
                .group(ListOption.<String>createBuilder()
                        .name(Component.translatable("fuchsmod.config.command_whitelist.command_regexes"))
                        .description(OptionDescription.of(
                                Component.translatable("fuchsmod.config.command_whitelist.command_regexes.description")))
                        .binding(defaults.whitelistedCommandPatterns,
                                () -> config.whitelistedCommandPatterns,
                                newValue -> config.whitelistedCommandPatterns = newValue)
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .insertEntriesAtEnd(true)
                        .build())
                .build();
    }
}
