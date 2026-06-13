package de.fuchsmod.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.network.chat.Component;

public class FuchsModConfig {
    @SerialEntry
    public boolean booleanToggle = true;

    public static ConfigCategory create(FuchsModConfig defaults, FuchsModConfig config) {
        return ConfigCategory.createBuilder()
                .name(Component.literal("General"))
                .group(OptionGroup.createBuilder()
                        .name(Component.literal("Test 1"))
                        .collapsed(true)
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Enable Test 1"))
                                .binding(defaults.booleanToggle,
                                        () -> config.booleanToggle,
                                        newValue -> config.booleanToggle = newValue)
                                .controller(FuchsModConfig::createBooleanController)
                                .build())
                        .build())
                .build();

    }

    public static BooleanControllerBuilder createBooleanController(Option<Boolean> opt) {
        return BooleanControllerBuilder.create(opt).coloured(true);
    }
}
