package de.fuchsmod.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.fuchsmod.config.FuchsModConfigManager;
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
            );
        });
    }

    private static int executeConfigCommand(CommandContext<FabricClientCommandSource> context) {
        FuchsModConfigManager.open();
        return Command.SINGLE_SUCCESS;
    }

    private static int executeGetTPSCommand(CommandContext<FabricClientCommandSource> context) {
        double[] tps = TPSMeasurement.INSTANCE.getTPS();
        String message = "Estimated TPS: %.1f".formatted(tps[0]) + ((tps[2] >= 5.0) ?  " Average (10s): %.1f".formatted(tps[1]) : "");
        context.getSource().sendFeedback(Component.literal(message));
        return Command.SINGLE_SUCCESS;
    }
}
