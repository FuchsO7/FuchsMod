package de.fuchsmod.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.fuchsmod.config.FuchsModConfigManager;
import de.fuchsmod.features.general.PingMeasurement;
import de.fuchsmod.features.general.TPSMeasurement;
import de.fuchsmod.features.partycommands.PartyCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

import static de.fuchsmod.FuchsMod.LOGGER;

public class Commands {
    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess)-> {
            dispatcher.register(ClientCommands.literal("fuchsmod")
                .then(ClientCommands.literal("config")
                    .executes(Commands::executeConfigCommand))
                .then(ClientCommands.literal("tps")
                    .executes(Commands::executeGetTPSCommand))
                .then(ClientCommands.literal("ping")
                    .executes(Commands::executeGetPingCommand))
                .then(ClientCommands.literal("debug")
                    .then(ClientCommands.literal("SetTimePacketListener")
                        .then(ClientCommands.argument("enable", BoolArgumentType.bool())
                            .executes(Commands::executeSetTimeListenerDebugToggle)))
                    .then(ClientCommands.literal("PingPacketListener")
                        .then(ClientCommands.argument("enable", BoolArgumentType.bool())
                            .executes(Commands::executePingListenerDebugToggle)))
                    .then(ClientCommands.literal("PongResponsePacketListener")
                        .then(ClientCommands.argument("enable", BoolArgumentType.bool())
                            .executes(Commands::executePongResponseListenerDebugToggle)))
                    .then(ClientCommands.literal("PartyCommands")
                            .then(ClientCommands.argument("enable", BoolArgumentType.bool())
                                    .executes(Commands::executePartyCommandsDebugToggle))))
            );
        });
        LOGGER.info("Initialized Fuchs Mod Commands!");
    }

    private static int executeConfigCommand(CommandContext<FabricClientCommandSource> context) {
        FuchsModConfigManager.open();
        return Command.SINGLE_SUCCESS;
    }

    private static int executeGetTPSCommand(CommandContext<FabricClientCommandSource> context) {
        Component message = Component.literal("Estimated TPS: ")
                .append(TPSMeasurement.getInstance().getCurrentTPSFormatted())
                .append(", Average (%ds): ".formatted(TPSMeasurement.AVERAGE_SAMPLE_TIME_SECONDS))
                .append(TPSMeasurement.getInstance().getAverageTPSFormatted());
        context.getSource().sendFeedback(message);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetPingCommand(CommandContext<FabricClientCommandSource> context) {
        Component message = Component.literal("Estimated Ping: ")
                .append(PingMeasurement.getInstance().getCurrentPingFormatted())
                .append(" ms, Average (%ds): ".formatted(PingMeasurement.AVERAGE_SAMPLE_TIME_SECONDS))
                .append(PingMeasurement.getInstance().getAveragePingFormatted())
                .append(" ms");
        context.getSource().sendFeedback(message);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeSetTimeListenerDebugToggle(CommandContext<FabricClientCommandSource> context) {
        Debug.enableSetTimePacketListenerDebug = BoolArgumentType.getBool(context, "enable");
        return Command.SINGLE_SUCCESS;
    }

    private static int executePingListenerDebugToggle(CommandContext<FabricClientCommandSource> context) {
        Debug.enablePingPacketListenerDebug = BoolArgumentType.getBool(context, "enable");
        return Command.SINGLE_SUCCESS;
    }

    private static int executePongResponseListenerDebugToggle(CommandContext<FabricClientCommandSource> context) {
        Debug.enablePongResponsePacketListenerDebug = BoolArgumentType.getBool(context, "enable");
        return Command.SINGLE_SUCCESS;
    }

    private static int executePartyCommandsDebugToggle(CommandContext<FabricClientCommandSource> context) {
        PartyCommands.enablePartyCommandsDebug = BoolArgumentType.getBool(context, "enable");
        return Command.SINGLE_SUCCESS;
    }
}
