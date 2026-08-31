package de.fuchsmod.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.fuchsmod.config.FuchsModConfigManager;
import de.fuchsmod.events.GameEvents;
import de.fuchsmod.features.general.*;
import de.fuchsmod.features.partycommands.PartyCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import static de.fuchsmod.FuchsMod.*;

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
                .then(ClientCommands.literal("calculate")
                    .executes(Commands::executeCalculatorCommand)
                    .then(ClientCommands.argument("expression", StringArgumentType.greedyString())
                        .executes(Commands::executeCalculateCommand)))
                .then(ClientCommands.literal("folder")
                    .then(ClientCommands.argument("folder", StringArgumentType.string())
                        .suggests(new FolderOpener.FolderSuggestionProvider())
                        .executes(Commands::executeOpenFolderCommand))
                    .executes(Commands::executeOpenFolderCommand))
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
                    .then(ClientCommands.literal("ResourcePackPacketsListener")
                        .then(ClientCommands.argument("enable", BoolArgumentType.bool())
                            .executes(Commands::executeResourcePackListenerDebugToggle)))
                    .then(ClientCommands.literal("CalculatorDebug")
                        .then(ClientCommands.argument("enable", BoolArgumentType.bool())
                            .executes(Commands::executeCalculatorDebugToggle)))
                    .then(ClientCommands.literal("PartyCommands")
                        .then(ClientCommands.argument("enable", BoolArgumentType.bool())
                            .executes(Commands::executePartyCommandsDebugToggle)))
                    .then(ClientCommands.literal("triggerGameEnd")
                        .executes(Commands::executeGameEndTrigger)))
            );
        });
        LOGGER.info("Initialized Fuchs Mod Commands!");
    }

    private static int executeConfigCommand(CommandContext<FabricClientCommandSource> context) {
        FuchsModConfigManager.open();
        return Command.SINGLE_SUCCESS;
    }

    private static int executeGetTPSCommand(CommandContext<FabricClientCommandSource> context) {
        Component message = FUCHSMOD_CHAT_MESSAGE_PREFIX.get()
                .append(Component.translatable("fuchsmod.commands.tps",
                        TPSMeasurement.getInstance().getCurrentTPSFormatted(),
                        TPSMeasurement.AVERAGE_SAMPLE_TIME_SECONDS,
                        TPSMeasurement.getInstance().getAverageTPSFormatted()));
        context.getSource().sendFeedback(message);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetPingCommand(CommandContext<FabricClientCommandSource> context) {
        Component message = FUCHSMOD_CHAT_MESSAGE_PREFIX.get()
                .append(Component.translatable("fuchsmod.commands.ping",
                        PingMeasurement.getInstance().getCurrentPingFormatted(),
                        PingMeasurement.AVERAGE_SAMPLE_TIME_SECONDS,
                        PingMeasurement.getInstance().getAveragePingFormatted()));
        context.getSource().sendFeedback(message);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeCalculatorCommand(CommandContext<FabricClientCommandSource> context) {
        CLIENT.execute(() -> CLIENT.gui.setScreen(new CalculatorScreen(CLIENT.gui.screen())));
        CLIENT.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        return Command.SINGLE_SUCCESS;
    }

    private static int executeCalculateCommand(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        String expression = StringArgumentType.getString(context, "expression");
        double result;
        try {
            result = Calculator.calculateExpression(expression);
        } catch (Calculator.CalculatorException exception) {
            SimpleCommandExceptionType commandException = new SimpleCommandExceptionType(Component.literal(exception.getMessage()));
            throw exception.reader == null ? commandException.create() : commandException.createWithContext(exception.reader);
        }
        context.getSource().sendFeedback(FUCHSMOD_CHAT_MESSAGE_PREFIX.get()
                .append(Component.translatable("fuchsmod.commands.calculator_result",
                        Calculator.roundResult(result))));
        return Command.SINGLE_SUCCESS;
    }

    private static int executeOpenFolderCommand(CommandContext<FabricClientCommandSource> context) {
        String folder;
        try {
            folder = StringArgumentType.getString(context, "folder");
        } catch (IllegalArgumentException e) {
            folder = "";
        }
        FolderOpener.openFolder(folder);
        return Command.SINGLE_SUCCESS;
    }

    private static void sendDebugFeedback(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(FUCHSMOD_DEBUG_CHAT_MESSAGE_PREFIX.get()
                .append(Component.translatable("fuchsmod.commands.debug_feedback",
                        BoolArgumentType.getBool(context, "enable"))));
    }

    private static int executeSetTimeListenerDebugToggle(CommandContext<FabricClientCommandSource> context) {
        Debug.enableSetTimePacketListenerDebug = BoolArgumentType.getBool(context, "enable");
        sendDebugFeedback(context);
        return Command.SINGLE_SUCCESS;
    }

    private static int executePingListenerDebugToggle(CommandContext<FabricClientCommandSource> context) {
        Debug.enablePingPacketListenerDebug = BoolArgumentType.getBool(context, "enable");
        sendDebugFeedback(context);
        return Command.SINGLE_SUCCESS;
    }

    private static int executePongResponseListenerDebugToggle(CommandContext<FabricClientCommandSource> context) {
        Debug.enablePongResponsePacketListenerDebug = BoolArgumentType.getBool(context, "enable");
        sendDebugFeedback(context);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeResourcePackListenerDebugToggle(CommandContext<FabricClientCommandSource> context) {
        Debug.enableResourcePackPacketsListenerDebug = BoolArgumentType.getBool(context, "enable");
        sendDebugFeedback(context);
        return Command.SINGLE_SUCCESS;
    }

    private static int executePartyCommandsDebugToggle(CommandContext<FabricClientCommandSource> context) {
        PartyCommands.enablePartyCommandsDebug = BoolArgumentType.getBool(context, "enable");
        sendDebugFeedback(context);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeCalculatorDebugToggle(CommandContext<FabricClientCommandSource> context) {
        Calculator.enableCalculatorCommandsDebug = BoolArgumentType.getBool(context, "enable");
        sendDebugFeedback(context);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeGameEndTrigger(CommandContext<FabricClientCommandSource> context) {
        GameEvents.GAME_ENDED.invoker().onGameEnd();
        context.getSource().sendFeedback(FUCHSMOD_DEBUG_CHAT_MESSAGE_PREFIX.get()
                .append(Component.translatable("fuchsmod.commands.debug_game_end_trigger")));
        return Command.SINGLE_SUCCESS;
    }
}
