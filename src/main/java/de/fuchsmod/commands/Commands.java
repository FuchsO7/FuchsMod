package de.fuchsmod.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.fuchsmod.config.FuchsModConfigManager;
import de.fuchsmod.features.PingMeasurement;
import de.fuchsmod.features.TPSMeasurement;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

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
            );
        });
    }

    private static int executeConfigCommand(CommandContext<FabricClientCommandSource> context) {
        FuchsModConfigManager.open();
        return Command.SINGLE_SUCCESS;
    }

    private static int executeGetTPSCommand(CommandContext<FabricClientCommandSource> context) {
        Component message = Component.literal("Estimated TPS: ")
                .append(TPSMeasurement.INSTANCE.getCurrentTPSFormatted())
                .append(", Average (%ds): ".formatted(TPSMeasurement.AVERAGE_SAMPLE_TIME_SECONDS))
                .append(TPSMeasurement.INSTANCE.getAverageTPSFormatted());
        context.getSource().sendFeedback(message);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetPingCommand(CommandContext<FabricClientCommandSource> context) {
        Component message = Component.literal("Estimated Ping: ")
                .append(PingMeasurement.INSTANCE.getCurrentPingFormatted())
                .append(" ms, Average (%ds): ".formatted(PingMeasurement.AVERAGE_SAMPLE_TIME_SECONDS))
                .append(PingMeasurement.INSTANCE.getAveragePingFormatted())
                .append(" ms");
        context.getSource().sendFeedback(message);
        return Command.SINGLE_SUCCESS;
    }
}
