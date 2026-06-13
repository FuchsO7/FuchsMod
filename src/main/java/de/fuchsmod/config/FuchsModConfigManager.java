package de.fuchsmod.config;

import com.google.gson.FieldNamingPolicy;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;

public class FuchsModConfigManager {
    public static final Minecraft client = Minecraft.getInstance();
    public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("fuchsmod.json5");
    protected static ConfigClassHandler<FuchsModConfig> HANDLER = ConfigClassHandler.createBuilder(FuchsModConfig.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(PATH)
                    .setJson5(true)
                    .appendGsonBuilder(builder -> builder
                            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY))
                    .build())
            .build();

    public static void init() {
        HANDLER.load();
    }

    public static FuchsModConfig get() {
        return HANDLER.instance();
    }

    public static void save() {
        HANDLER.save();
    }

    public static Screen createGui(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, (defaults, config, builder) -> builder
                .title(Component.literal("Test Mod"))
                .category(FuchsModConfig.create(defaults, config))
                .save(FuchsModConfigManager::save)
        ).generateScreen(parent);
    }

    public static void open() {
        client.execute(() -> {
            client.setScreen(createGui(client.screen));
        });
    }
}
