package de.fuchsmod.config;

import de.fuchsmod.features.TPSMeasurement;
import de.fuchsmod.features.PingMeasurement;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.network.chat.Component;

public class FuchsModConfig {
    @SerialEntry
    public boolean showTPSHud = false;

    @SerialEntry
    public double TPSHudXPos = 2.0;

    @SerialEntry
    public double TPSHudYPos = 2.0;

    @SerialEntry
    public TPSPacketTypes packetTypeForTPSMeasurement = TPSPacketTypes.SetTime;

    public enum TPSPacketTypes implements NameableEnum {
        SetTime,
        Ping;

        @Override
        public Component getDisplayName() {
            return Component.literal(name());
        }
    }

    @SerialEntry
    public boolean showPingHud = false;

    @SerialEntry
    public double PingHudXPos = 2.0;

    @SerialEntry
    public double PingHudYPos = 5.0;

    public static ConfigCategory create(FuchsModConfig defaults, FuchsModConfig config) {
        return ConfigCategory.createBuilder()
                .name(Component.literal("General"))
                .group(OptionGroup.createBuilder()
                        .name(Component.literal("TPS"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Show TPS Hud"))
                                .description(OptionDescription.of(
                                        Component.literal("Show a Hud Element, which displays the average TPS over the last 5 seconds")))
                                .binding(defaults.showTPSHud,
                                        () -> config.showTPSHud,
                                        newValue -> config.showTPSHud = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.literal("TPS Hud X Position"))
                                .description(OptionDescription.of(
                                        Component.literal("Relative Horizontal Position of the TPS Hud")))
                                .binding(defaults.TPSHudXPos,
                                        () -> config.TPSHudXPos,
                                        newValue -> config.TPSHudXPos = newValue)
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                        .range(0.0, 100.0)
                                        .step(0.1)
                                        .formatValue(value -> Component.literal("%.1f %%".formatted(value))))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.literal("TPS Hud Y Position"))
                                .description(OptionDescription.of(
                                        Component.literal("Relative Vertical Position of the TPS Hud")))
                                .binding(defaults.TPSHudYPos,
                                        () -> config.TPSHudYPos,
                                        newValue -> config.TPSHudYPos = newValue)
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                        .range(0.0, 100.0)
                                        .step(0.1)
                                        .formatValue(value -> Component.literal("%.1f %%".formatted(value))))
                                .build())
                        .option(Option.<TPSPacketTypes>createBuilder()
                                .name(Component.literal("Packet Type"))
                                .description(OptionDescription.of(
                                        Component.literal("""
                                                Choose which kind of packet is used to measure the Server TPS
                                                The Set Time Packet is sent by any server and grants a good estimate
                                                The Ping Packet offers more accurate results, but depends on a modified server as it's not used by the vanilla game""")))
                                .binding(config.packetTypeForTPSMeasurement,
                                        () -> config.packetTypeForTPSMeasurement,
                                        newValue -> config.packetTypeForTPSMeasurement = newValue)
                                .controller(opt -> EnumControllerBuilder.create(opt)
                                        .enumClass(TPSPacketTypes.class))
                                .build())
                        .option(ButtonOption.createBuilder()
                                .name(Component.literal("Reset Data"))
                                .text(Component.literal(""))
                                .description(OptionDescription.of(
                                        Component.literal("Resets the currently cached TPS results")))
                                .action((screen, buttonOption) -> TPSMeasurement.INSTANCE.reset())
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.literal("Ping"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Show Ping Hud"))
                                .description(OptionDescription.of(
                                        Component.literal("Show a Hud Element, which displays the average Ping over the last 5 seconds")))
                                .binding(defaults.showPingHud,
                                        () -> config.showPingHud,
                                        newValue -> config.showPingHud = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.literal("Ping Hud X Position"))
                                .description(OptionDescription.of(
                                        Component.literal("Relative Horizontal Position of the Ping Hud")))
                                .binding(defaults.PingHudXPos,
                                        () -> config.PingHudXPos,
                                        newValue -> config.PingHudXPos = newValue)
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                        .range(0.0, 100.0)
                                        .step(0.1)
                                        .formatValue(value -> Component.literal("%.1f %%".formatted(value))))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.literal("Ping Hud Y Position"))
                                .description(OptionDescription.of(
                                        Component.literal("Relative Vertical Position of the Ping Hud")))
                                .binding(defaults.PingHudYPos,
                                        () -> config.PingHudYPos,
                                        newValue -> config.PingHudYPos = newValue)
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                        .range(0.0, 100.0)
                                        .step(0.1)
                                        .formatValue(value -> Component.literal("%.1f %%".formatted(value))))
                                .build())
                        .option(ButtonOption.createBuilder()
                                .name(Component.literal("Reset Data"))
                                .text(Component.literal(""))
                                .description(OptionDescription.of(
                                        Component.literal("Resets the currently cached Ping results")))
                                .action((screen, buttonOption) -> PingMeasurement.INSTANCE.reset())
                                .build())
                        .build())
                .build();

    }
}
