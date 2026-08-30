package de.fuchsmod.config.categories;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.features.general.Fullbright;
import de.fuchsmod.features.general.PingMeasurement;
import de.fuchsmod.features.general.TPSMeasurement;
import de.fuchsmod.features.general.TooltipScroll;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;

import static de.fuchsmod.FuchsMod.CLIENT;

public class GeneralCategory {

    public static ConfigCategory create(FuchsModConfig defaults, FuchsModConfig config) {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("fuchsmod.config.general"))
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("fuchsmod.config.general.tooltip_scroll"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.general.tooltip_scroll.enable"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.general.tooltip_scroll.enable.description")))
                                .binding(defaults.enableTooltipScroll,
                                        () -> config.enableTooltipScroll,
                                        newValue -> {
                                            config.enableTooltipScroll = newValue;
                                            TooltipScroll.getInstance().resetOffset();
                                        })
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("fuchsmod.config.general.tooltip_scroll.scroll_factor"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.general.tooltip_scroll.scroll_factor.description")))
                                .binding(defaults.scrollFactor,
                                        () -> config.scrollFactor,
                                        newValue -> config.scrollFactor = newValue)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 10)
                                        .step(1)
                                        .formatValue(value -> Component.literal("%d".formatted(value))))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.general.tooltip_scroll.vertical_scroll_direction"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.general.tooltip_scroll.vertical_scroll_direction.description")))
                                .binding(defaults.verticalScrollDirection == 1,
                                        () -> config.verticalScrollDirection == 1,
                                        newValue -> config.verticalScrollDirection = newValue ? 1 : -1)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .formatValue(value -> Component.translatable(value ?
                                                "fuchsmod.config.general.tooltip_scroll.vertical_scroll_direction.option_up_down" :
                                                "fuchsmod.config.general.tooltip_scroll.vertical_scroll_direction.option_up_up")))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.general.tooltip_scroll.horizontal_scroll_direction"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.general.tooltip_scroll.horizontal_scroll_direction.description")))
                                .binding(defaults.horizontalScrollDirection == 1,
                                        () -> config.horizontalScrollDirection == 1,
                                        newValue -> config.horizontalScrollDirection = newValue ? 1 : -1)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .formatValue(value -> Component.translatable(value ?
                                                "fuchsmod.config.general.tooltip_scroll.horizontal_scroll_direction.option_up_right" :
                                                "fuchsmod.config.general.tooltip_scroll.horizontal_scroll_direction.option_up_left")))
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("fuchsmod.config.general.fullbright"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.general.fullbright.enable"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.general.fullbright.enable.description")))
                                .binding(defaults.enableCustomGamma,
                                        () -> config.enableCustomGamma,
                                        newValue -> {
                                            config.enableCustomGamma = newValue;
                                            Fullbright.setGamma();})
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.translatable("fuchsmod.config.general.brightness"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.general.brightness.description")))
                                .binding(defaults.customGamma,
                                        () -> config.customGamma,
                                        newValue -> {
                                            config.customGamma = newValue;
                                            Fullbright.setGamma();
                                        })
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                        .range(0.0, 15.0)
                                        .step(0.1)
                                        .formatValue(value -> Component.literal("%.0f %%".formatted(100 * value))))
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("fuchsmod.config.general.zoom"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.general.zoom.enable"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.general.zoom.enable.description")))
                                .binding(defaults.enableZoom,
                                        () -> config.enableZoom,
                                        newValue -> {
                                            config.enableZoom = newValue;
                                            Fullbright.setGamma();})
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.general.smooth_camera"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.general.smooth_camera.description")))
                                .binding(defaults.smoothCameraOnZoom,
                                        () -> config.smoothCameraOnZoom,
                                        newValue -> {
                                            config.smoothCameraOnZoom = newValue;
                                            Fullbright.setGamma();})
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.translatable("fuchsmod.config.general.zoom_factor"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.general.zoom_factor.description")))
                                .binding(defaults.zoomFactor,
                                        () -> config.zoomFactor,
                                        newValue -> {
                                            config.zoomFactor = newValue;
                                            Fullbright.setGamma();
                                        })
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                        .range(0.0, 2.0)
                                        .step(0.01)
                                        .formatValue(value -> Component.literal("%.0f %%".formatted(100 * value))))
                                .build())
                        .option(Option.<Float>createBuilder()
                                .name(Component.translatable("fuchsmod.config.general.immediate_zoom_factor"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.general.immediate_zoom_factor.description")))
                                .binding(defaults.immediateZoomFactor,
                                        () -> config.immediateZoomFactor,
                                        newValue -> {
                                            config.immediateZoomFactor = newValue;
                                            Fullbright.setGamma();
                                        })
                                .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                        .range(1.0f, 32.0f)
                                        .step(1f)
                                        .formatValue(value -> Component.literal("%.0fx".formatted(value))))
                                .build())
                        .option(ButtonOption.createBuilder()
                                .name(Component.translatable("controls.keybinds"))
                                .text(Component.literal(""))
                                .action((screen, buttonOption) -> {
                                    CLIENT.gui.setScreen(new KeyBindsScreen(screen, CLIENT.options));
                                })
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("fuchsmod.config.general.server_resource_pack"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.general.server_resource_pack.auto_ignore"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.general.server_resource_pack.auto_ignore.description")))
                                .binding(defaults.autoIgnoreServerResourcePacks,
                                        () -> config.autoIgnoreServerResourcePacks,
                                        newValue -> config.autoIgnoreServerResourcePacks = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Long>createBuilder()
                                .name(Component.translatable("fuchsmod.config.general.server_resource_pack.ignore_delay"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.general.server_resource_pack.ignore_delay.description")))
                                .binding(defaults.serverResourcePackIgnoreTimeMillis,
                                        () -> config.serverResourcePackIgnoreTimeMillis,
                                        newValue -> config.serverResourcePackIgnoreTimeMillis = newValue)
                                .controller(opt -> LongSliderControllerBuilder.create(opt)
                                        .range(0L, 10000L)
                                        .step(100L)
                                        .formatValue(value -> Component.literal("%d ms".formatted(value))))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.general.server_resource_pack.send_link"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.general.server_resource_pack.send_link.description")))
                                .binding(defaults.sendServerResourcePackDownloadLink,
                                        () -> config.sendServerResourcePackDownloadLink,
                                        newValue -> config.sendServerResourcePackDownloadLink = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("fuchsmod.config.general.calculator"))
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("fuchsmod.config.general.calculator.precision"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.general.calculator.precision.description")))
                                .binding(defaults.calculatorPrecision,
                                        () -> config.calculatorPrecision,
                                        newValue -> config.calculatorPrecision = newValue)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 10)
                                        .step(1))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.general.calculator.default_screen_layout"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.general.calculator.default_screen_layout.description")))
                                .binding(defaults.showFunctionsOnCalculatorScreenOpen,
                                        () -> config.showFunctionsOnCalculatorScreenOpen,
                                        newValue -> config.showFunctionsOnCalculatorScreenOpen = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .formatValue(value -> Component.translatable(value ?
                                                "fuchsmod.config.general.calculator.default_screen_layout.advanced" :
                                                "fuchsmod.config.general.calculator.default_screen_layout.simple")))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.general.calculator.inventory_calculator"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.general.calculator.inventory_calculator.description")))
                                .binding(defaults.showInventoryCalculator,
                                        () -> config.showInventoryCalculator,
                                        newValue -> config.showInventoryCalculator = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("fuchsmod.config.general.death_message_saver"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.general.death_message_saver.enable"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.general.death_message_saver.enable.description")))
                                .binding(defaults.sendLastDeathLocationMessage,
                                        () -> config.sendLastDeathLocationMessage,
                                        newValue -> config.sendLastDeathLocationMessage = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .build())
                .build();
    }
}
