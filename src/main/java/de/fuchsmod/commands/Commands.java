package de.fuchsmod.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.fuchsmod.config.FuchsModConfigManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class Commands {
    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess)-> {
            dispatcher.register(ClientCommands.literal("fuchsmod")
                .then(ClientCommands.literal("config")
                    .executes(Commands::executeConfigCommand)));
        });
    }

    private static int executeConfigCommand(CommandContext<FabricClientCommandSource> context) {
        FuchsModConfigManager.open();
        return Command.SINGLE_SUCCESS;
    }
}
