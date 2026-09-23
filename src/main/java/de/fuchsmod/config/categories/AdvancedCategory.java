package de.fuchsmod.config.categories;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.features.general.TooltipScroll;
import de.fuchsmod.features.partycommands.PartyCommandUtils;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.LongFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.LongSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.network.chat.Component;

public class AdvancedCategory {
    public static ConfigCategory create(FuchsModConfig defaults, FuchsModConfig config) {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("fuchsmod.config.advanced"))
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("fuchsmod.config.advanced.enable_game_end_trigger"))
                        .description(OptionDescription.of(
                                Component.translatable("fuchsmod.config.advanced.enable_game_end_trigger.description")))
                        .binding(defaults.enableGameEndTrigger,
                                () -> config.enableGameEndTrigger,
                                newValue -> config.enableGameEndTrigger = newValue)
                        .controller(opt -> BooleanControllerBuilder.create(opt)
                                .coloured(true))
                        .build())
                .group(ListOption.<String>createBuilder()
                        .name(Component.translatable("fuchsmod.config.advanced.game_end_trigger_regexes"))
                        .description(OptionDescription.of(
                                Component.translatable("fuchsmod.config.advanced.game_end_trigger_regexes.description")))
                        .binding(defaults.gameEndTriggerPatterns,
                                () -> config.gameEndTriggerPatterns,
                                newValue -> config.gameEndTriggerPatterns = newValue)
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .insertEntriesAtEnd(true)
                        .build())
                .group(ListOption.<String>createBuilder()
                        .name(Component.translatable("fuchsmod.config.advanced.partycommands_regexes"))
                        .description(OptionDescription.of(
                                Component.translatable("fuchsmod.config.advanced.partycommands_regexes.description")))
                        .binding(defaults.partyCommandsPatterns,
                                () -> config.partyCommandsPatterns,
                                newValue -> config.partyCommandsPatterns = newValue)
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .insertEntriesAtEnd(true)
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("fuchsmod.config.advanced.partycommand_bedwars_dreams_settings"))
                        .option(Option.<Long>createBuilder()
                                .name(Component.translatable("fuchsmod.config.advanced.partycommand_bedwars_dreams_settings.rotation_time"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.advanced.partycommand_bedwars_dreams_settings.rotation_time.description")))
                                .binding(defaults.dreamRotationTimeSeconds,
                                        () -> config.dreamRotationTimeSeconds,
                                        newValue -> {
                                            config.dreamRotationTimeSeconds = newValue;
                                            TooltipScroll.getInstance().resetOffset();
                                        })
                                .controller(LongFieldControllerBuilder::create)
                                .build())
                        .option(Option.<Long>createBuilder()
                                .name(Component.translatable("fuchsmod.config.advanced.partycommand_bedwars_dreams_settings.offset"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.advanced.partycommand_bedwars_dreams_settings.offset.description")))
                                .binding(defaults.dreamRotationOffsetSeconds,
                                        () -> config.dreamRotationOffsetSeconds,
                                        newValue -> {
                                            config.dreamRotationOffsetSeconds = newValue;
                                            TooltipScroll.getInstance().resetOffset();
                                        })
                                .controller(LongFieldControllerBuilder::create)
                                .build())
                        .build())
                .group(ListOption.<String>createBuilder()
                        .name(Component.translatable("fuchsmod.config.advanced.partycommands_bedwars_dreams"))
                        .description(OptionDescription.of(
                                Component.translatable("fuchsmod.config.advanced.partycommands_bedwars_dreams.description", PartyCommandUtils.getDream.apply(null, null, null))))
                        .binding(defaults.partyCommandsBedwarsDreams,
                                () -> config.partyCommandsBedwarsDreams,
                                newValue -> config.partyCommandsBedwarsDreams = newValue)
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .insertEntriesAtEnd(true)
                        .build())
                .build();
    }
}
