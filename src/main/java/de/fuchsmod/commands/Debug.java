package de.fuchsmod.commands;

import de.fuchsmod.events.ClientPacketEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class Debug {
    private static final Minecraft client = Minecraft.getInstance();

    static boolean enableSetTimePacketListenerDebug = false;
    static boolean enablePingPacketListenerDebug = false;
    static boolean enablePongResponsePacketListenerDebug = false;

    public static void init() {
        ClientPacketEvents.SET_TIME_PACKET.register(packet -> {
            if (enableSetTimePacketListenerDebug && client.player != null)
                client.player.sendSystemMessage(Component.literal("Received Set Time Packet\n- GameTime: %d".formatted(packet.gameTime())));
        });
        ClientPacketEvents.PING_PACKET.register(packet -> {
            if (enablePingPacketListenerDebug && client.player != null)
                client.player.sendSystemMessage(Component.literal("Received Ping Packet\n- ID: %d".formatted(packet.getId())));
        });
        ClientPacketEvents.PONG_RESPONSE_PACKET.register(packet -> {
            if (enablePongResponsePacketListenerDebug && client.player != null)
                client.player.sendSystemMessage(Component.literal("Received Pong Response Packet\n- Time: %d".formatted(packet.time())));
        });
    }
}
