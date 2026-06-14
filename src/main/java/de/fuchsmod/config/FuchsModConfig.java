package de.fuchsmod.config;

import de.fuchsmod.features.TPSMeasurement;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.network.chat.Component;

public class FuchsModConfig {
    @SerialEntry
    public boolean showTPSHud = true;

    @SerialEntry
    public int TPSHudXPos = 5;

    @SerialEntry
    public int TPSHudYPos = 5;

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

    public static ConfigCategory create(FuchsModConfig defaults, FuchsModConfig config) {
        return ConfigCategory.createBuilder()
                .name(Component.literal("General"))
                .group(OptionGroup.createBuilder()
                        .name(Component.literal("TPS"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Show TPS Hud"))
                                .description(OptionDescription.of(
                                        Component.literal("Show a Hud Element, which displays the average TPS over the last 10 seconds")))
                                .binding(defaults.showTPSHud,
                                        () -> config.showTPSHud,
                                        newValue -> config.showTPSHud = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.literal("TPS Hud X Position"))
                                .description(OptionDescription.of(
                                        Component.literal("Relative Horizontal Position of the TPS Hud")))
                                .binding(defaults.TPSHudXPos,
                                        () -> config.TPSHudXPos,
                                        newValue -> config.TPSHudXPos = newValue)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100)
                                        .step(1)
                                        .formatValue(value -> Component.literal(value + " %")))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.literal("TPS Hud Y Position"))
                                .description(OptionDescription.of(
                                        Component.literal("Relative Vertical Position of the TPS Hud")))
                                .binding(defaults.TPSHudYPos,
                                        () -> config.TPSHudYPos,
                                        newValue -> config.TPSHudYPos = newValue)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100)
                                        .step(1)
                                        .formatValue(value -> Component.literal(value + " %")))
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
                .build();

    }
}
