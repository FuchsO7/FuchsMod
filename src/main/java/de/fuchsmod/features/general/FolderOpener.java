package de.fuchsmod.features.general;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Util;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class FolderOpener {

    public static void openFolder(String folder) {
        Path path = FabricLoader.getInstance().getGameDir().resolve(folder);
        Util.getPlatform().openPath(path);
    }

    public static class FolderSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
            File[] files = FabricLoader.getInstance().getGameDir().toFile().listFiles();
            if (files == null)
                return builder.buildFuture();
            for (File file : files) {
                if (file.isDirectory()) {
                    String directoryName = file.getName();
                    if (directoryName.startsWith(builder.getRemaining()))
                        builder.suggest(file.getName());
                }
            }
            return builder.buildFuture();
        }
    }
}
