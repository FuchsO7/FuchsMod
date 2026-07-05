package de.fuchsmod.config.categories;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.features.general.Fullbright;
import de.fuchsmod.features.general.PingMeasurement;
import de.fuchsmod.features.general.TPSMeasurement;
import de.fuchsmod.features.general.TooltipScroll;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;

public class GeneralCategory {
    private static final Minecraft client = Minecraft.getInstance();

    public static ConfigCategory create(FuchsModConfig defaults, FuchsModConfig config) {
        return ConfigCategory.createBuilder()
                .name(Component.literal("General"))
                .group(OptionGroup.createBuilder()
                        .name(Component.literal("TPS"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Show TPS Hud"))
                                .description(OptionDescription.of(
                                        Component.literal("Show a Hud Element, which displays the average TPS over the last 5 seconds.")))
                                .binding(defaults.showTPSHud,
                                        () -> config.showTPSHud,
                                        newValue -> config.showTPSHud = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.literal("TPS Hud X Position"))
                                .description(OptionDescription.of(
                                        Component.literal("Relative Horizontal Position of the TPS Hud.")))
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
                                        Component.literal("Relative Vertical Position of the TPS Hud.")))
                                .binding(defaults.TPSHudYPos,
                                        () -> config.TPSHudYPos,
                                        newValue -> config.TPSHudYPos = newValue)
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                        .range(0.0, 100.0)
                                        .step(0.1)
                                        .formatValue(value -> Component.literal("%.1f %%".formatted(value))))
                                .build())
                        .option(Option.<FuchsModConfig.TPSPacketTypes>createBuilder()
                                .name(Component.literal("Packet Type"))
                                .description(OptionDescription.of(
                                        Component.literal("""
                                                Choose which kind of packet is used to measure the Server TPS.
                                                The Set Time Packet is sent by any server and grants a good estimate.
                                                The Ping Packet offers more accurate results, but depends on a modified server as it's not used by the vanilla game.""")))
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
                                .name(Component.literal("Reset Data"))
                                .text(Component.literal(""))
                                .description(OptionDescription.of(
                                        Component.literal("Resets the currently cached TPS results.")))
                                .action((screen, buttonOption) -> TPSMeasurement.getInstance().reset())
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.literal("FPS"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Show FPS Hud"))
                                .description(OptionDescription.of(
                                        Component.literal("Show a Hud Element, which displays the current FPS.")))
                                .binding(defaults.showFPSHud,
                                        () -> config.showFPSHud,
                                        newValue -> config.showFPSHud = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.literal("FPS Hud X Position"))
                                .description(OptionDescription.of(
                                        Component.literal("Relative Horizontal Position of the FS Hud.")))
                                .binding(defaults.FPSHudXPos,
                                        () -> config.FPSHudXPos,
                                        newValue -> config.FPSHudXPos = newValue)
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                        .range(0.0, 100.0)
                                        .step(0.1)
                                        .formatValue(value -> Component.literal("%.1f %%".formatted(value))))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.literal("FPS Hud Y Position"))
                                .description(OptionDescription.of(
                                        Component.literal("Relative Vertical Position of the FPS Hud.")))
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
                        .name(Component.literal("Ping"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Show Ping Hud"))
                                .description(OptionDescription.of(
                                        Component.literal("Show a Hud Element, which displays the average Ping over the last 5 seconds.")))
                                .binding(defaults.showPingHud,
                                        () -> config.showPingHud,
                                        newValue -> config.showPingHud = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Always send ping requests"))
                                .description(OptionDescription.of(
                                        Component.literal("""
                                                If enabled, a ping request packet will be sent to the server each tick. Required for Ping measurement.
                                                If disabled, ping requests will only be sent if the F3 Network Chart is open.""")))
                                .binding(defaults.alwaysSendPingRequest,
                                        () -> config.alwaysSendPingRequest,
                                        newValue -> config.alwaysSendPingRequest = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.literal("Ping Hud X Position"))
                                .description(OptionDescription.of(
                                        Component.literal("Relative Horizontal Position of the Ping Hud.")))
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
                                        Component.literal("Relative Vertical Position of the Ping Hud.")))
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
                                        Component.literal("Resets the currently cached Ping results.")))
                                .action((screen, buttonOption) -> PingMeasurement.getInstance().reset())
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.literal("Tooltip Scroll"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Enable Tooltip Scroll"))
                                .description(OptionDescription.of(
                                        Component.literal("""
                                                Allows to move tooltips vertically with the mouse wheel.
                                                Hold shift to scroll horizontally.""")))
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
                                .name(Component.literal("Scroll Factor"))
                                .description(OptionDescription.of(
                                        Component.literal("Determine by how much a tooltip should be moved each scroll.")))
                                .binding(defaults.scrollFactor,
                                        () -> config.scrollFactor,
                                        newValue -> config.scrollFactor = newValue)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 10)
                                        .step(1)
                                        .formatValue(value -> Component.literal("%d".formatted(value))))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Vertical Scroll Direction"))
                                .description(OptionDescription.of(
                                        Component.literal("Determine in which direction the tooltip will be scrolled vertically.")))
                                .binding(defaults.verticalScrollDirection == 1,
                                        () -> config.verticalScrollDirection == 1,
                                        newValue -> config.verticalScrollDirection = newValue ? 1 : -1)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .formatValue(value -> Component.literal(value ? "Scroll Up -> Move Down" : "Scroll Up -> Move Up")))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Horizontal Scroll Direction"))
                                .description(OptionDescription.of(
                                        Component.literal("Determine in which direction the tooltip will be scrolled horizontally.")))
                                .binding(defaults.horizontalScrollDirection == 1,
                                        () -> config.horizontalScrollDirection == 1,
                                        newValue -> config.horizontalScrollDirection = newValue ? 1 : -1)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .formatValue(value -> Component.literal(value ? "Scroll Up -> Move Right" : "Scroll Up -> Move Left")))
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.literal("Fullbright"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Use Custom Brightness"))
                                .description(OptionDescription.of(
                                        Component.literal("If enabled, the custom brightness will be set as brightness. Otherwise, the vanilla brightness setting is used.")))
                                .binding(defaults.enableCustomGamma,
                                        () -> config.enableCustomGamma,
                                        newValue -> {
                                            config.enableCustomGamma = newValue;
                                            Fullbright.setGamma();})
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.literal("Custom Brightness"))
                                .description(OptionDescription.of(
                                        Component.literal("Set a custom brightness. 100% is the normal Brightness.")))
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
                        .name(Component.literal("Zoom"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Enable Zoom"))
                                .description(OptionDescription.of(
                                        Component.literal("If enabled, allows to zoom by holding control and scrolling with the mouse wheel.")))
                                .binding(defaults.enableZoom,
                                        () -> config.enableZoom,
                                        newValue -> {
                                            config.enableZoom = newValue;
                                            Fullbright.setGamma();})
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.literal("Zoom Factor"))
                                .description(OptionDescription.of(
                                        Component.literal("Determine by how much a scroll should zoom.")))
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
                                .name(Component.literal("Immediate Zoom"))
                                .description(OptionDescription.of(
                                        Component.literal("Determine by how much the zoom hotkey scales.")))
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
                                .name(Component.literal("Open Key Binds"))
                                .text(Component.literal(""))
                                .action((screen, buttonOption) -> {
                                    client.gui.setScreen(new KeyBindsScreen(screen, client.options));
                                })
                                .build())
                        .build())
                .build();
    }
}
