package de.fuchsmod.config.categories;

import de.fuchsmod.config.FuchsModConfig;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.network.chat.Component;

public class AdvancedCategory {
    public static ConfigCategory create(FuchsModConfig defaults, FuchsModConfig config) {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("fuchsmod.config.advanced"))
                .group(ListOption.<String>createBuilder()
                        .name(Component.translatable("fuchsmod.config.advanced.game_end_trigger_regexes"))
                        .description(OptionDescription.of(
                                Component.translatable("fuchsmod.config.advanced.game_end_trigger_regexes.description")))
                        .binding(defaults.gameEndTriggerRegexes,
                                () -> config.gameEndTriggerRegexes,
                                newValue -> config.gameEndTriggerRegexes = newValue)
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .insertEntriesAtEnd(true)
                        .build())
                .build();
    }
}
