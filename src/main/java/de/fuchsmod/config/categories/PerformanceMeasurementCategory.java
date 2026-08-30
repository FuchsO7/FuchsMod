package de.fuchsmod.config.categories;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.features.general.PingMeasurement;
import de.fuchsmod.features.general.TPSMeasurement;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import net.minecraft.network.chat.Component;

public class PerformanceMeasurementCategory {

    public static ConfigCategory create(FuchsModConfig defaults, FuchsModConfig config) {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("fuchsmod.config.performance_measurement"))
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("fuchsmod.config.performance_measurement.tps"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.performance_measurement.tps.show_hud"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.performance_measurement.tps.show_hud.description")))
                                .binding(defaults.showTPSHud,
                                        () -> config.showTPSHud,
                                        newValue -> config.showTPSHud = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.performance_measurement.tps.color"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.performance_measurement.tps.color.description")))
                                .binding(defaults.useContinuousColorsForTPSHud,
                                        () -> config.useContinuousColorsForTPSHud,
                                        newValue -> config.useContinuousColorsForTPSHud = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.translatable("fuchsmod.config.performance_measurement.tps.hud_x_pos"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.performance_measurement.tps.hud_x_pos.description")))
                                .binding(defaults.TPSHudXPos,
                                        () -> config.TPSHudXPos,
                                        newValue -> config.TPSHudXPos = newValue)
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                        .range(0.0, 100.0)
                                        .step(0.1)
                                        .formatValue(value -> Component.literal("%.1f %%".formatted(value))))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.translatable("fuchsmod.config.performance_measurement.tps.hud_y_pos"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.performance_measurement.tps.hud_y_pos.description")))
                                .binding(defaults.TPSHudYPos,
                                        () -> config.TPSHudYPos,
                                        newValue -> config.TPSHudYPos = newValue)
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                        .range(0.0, 100.0)
                                        .step(0.1)
                                        .formatValue(value -> Component.literal("%.1f %%".formatted(value))))
                                .build())
                        .option(Option.<FuchsModConfig.TPSPacketTypes>createBuilder()
                                .name(Component.translatable("fuchsmod.config.performance_measurement.tps.packet_type"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.performance_measurement.tps.packet_type.description")))
                                .binding(defaults.packetTypeForTPSMeasurement,
                                        () -> config.packetTypeForTPSMeasurement,
                                        newValue -> {
                                            config.packetTypeForTPSMeasurement = newValue;
                                            TPSMeasurement.getInstance().reset();
                                        })
                                .controller(opt -> EnumControllerBuilder.create(opt)
                                        .enumClass(FuchsModConfig.TPSPacketTypes.class))
                                .build())
                        .option(ButtonOption.createBuilder()
                                .name(Component.translatable("fuchsmod.config.performance_measurement.tps.reset"))
                                .text(Component.literal(""))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.performance_measurement.tps.reset.description")))
                                .action((screen, buttonOption) -> TPSMeasurement.getInstance().reset())
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("fuchsmod.config.performance_measurement.fps"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.performance_measurement.fps.show_hud"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.performance_measurement.fps.show_hud.description")))
                                .binding(defaults.showFPSHud,
                                        () -> config.showFPSHud,
                                        newValue -> config.showFPSHud = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.performance_measurement.fps.color"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.performance_measurement.fps.color.description")))
                                .binding(defaults.useContinuousColorsForFPSHud,
                                        () -> config.useContinuousColorsForFPSHud,
                                        newValue -> config.useContinuousColorsForFPSHud = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.translatable("fuchsmod.config.performance_measurement.fps.hud_x_pos"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.performance_measurement.fps.hud_x_pos.description")))
                                .binding(defaults.FPSHudXPos,
                                        () -> config.FPSHudXPos,
                                        newValue -> config.FPSHudXPos = newValue)
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                        .range(0.0, 100.0)
                                        .step(0.1)
                                        .formatValue(value -> Component.literal("%.1f %%".formatted(value))))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.translatable("fuchsmod.config.performance_measurement.fps.hud_y_pos"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.performance_measurement.fps.hud_y_pos.description")))
                                .binding(defaults.FPSHudYPos,
                                        () -> config.FPSHudYPos,
                                        newValue -> config.FPSHudYPos = newValue)
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                        .range(0.0, 100.0)
                                        .step(0.1)
                                        .formatValue(value -> Component.literal("%.1f %%".formatted(value))))
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("fuchsmod.config.performance_measurement.ping"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.performance_measurement.ping.show_hud"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.performance_measurement.ping.show_hud.description")))
                                .binding(defaults.showPingHud,
                                        () -> config.showPingHud,
                                        newValue -> config.showPingHud = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.performance_measurement.ping.color"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.performance_measurement.ping.color.description")))
                                .binding(defaults.useContinuousColorsForPingHud,
                                        () -> config.useContinuousColorsForPingHud,
                                        newValue -> config.useContinuousColorsForPingHud = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.translatable("fuchsmod.config.performance_measurement.ping.hud_x_pos"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.performance_measurement.ping.hud_x_pos.description")))
                                .binding(defaults.PingHudXPos,
                                        () -> config.PingHudXPos,
                                        newValue -> config.PingHudXPos = newValue)
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                        .range(0.0, 100.0)
                                        .step(0.1)
                                        .formatValue(value -> Component.literal("%.1f %%".formatted(value))))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.translatable("fuchsmod.config.performance_measurement.ping.hud_y_pos"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.performance_measurement.ping.hud_y_pos.description")))
                                .binding(defaults.PingHudYPos,
                                        () -> config.PingHudYPos,
                                        newValue -> config.PingHudYPos = newValue)
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                        .range(0.0, 100.0)
                                        .step(0.1)
                                        .formatValue(value -> Component.literal("%.1f %%".formatted(value))))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fuchsmod.config.performance_measurement.ping.always_send_request"))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.performance_measurement.ping.always_send_request.description")))
                                .binding(defaults.alwaysSendPingRequest,
                                        () -> config.alwaysSendPingRequest,
                                        newValue -> config.alwaysSendPingRequest = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(ButtonOption.createBuilder()
                                .name(Component.translatable("fuchsmod.config.performance_measurement.ping.reset"))
                                .text(Component.literal(""))
                                .description(OptionDescription.of(
                                        Component.translatable("fuchsmod.config.performance_measurement.ping.reset.description")))
                                .action((screen, buttonOption) -> PingMeasurement.getInstance().reset())
                                .build())
                        .build())
                .build();
    }
}
