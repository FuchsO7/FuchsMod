package de.fuchsmod.commands;

import de.fuchsmod.events.ClientPacketEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import static de.fuchsmod.FuchsMod.LOGGER;

public class Debug {
    private static final Minecraft client = Minecraft.getInstance();

    static boolean enableSetTimePacketListenerDebug = false;
    static boolean enablePingPacketListenerDebug = false;
    static boolean enablePongResponsePacketListenerDebug = false;
    static boolean enableResourcePackPacketsListenerDebug = false;

    public static void init() {
        ClientPacketEvents.SET_TIME_PACKET.register(packet -> {
            if (enableSetTimePacketListenerDebug) {
                sendDebugMessages("Received Set Time Packet\n- GameTime: %d".formatted(
                        packet.gameTime()));
            }
        });
        ClientPacketEvents.PING_PACKET.register(packet -> {
            if (enablePingPacketListenerDebug) {
                sendDebugMessages("Received Ping Packet\n- ID: %d".formatted(
                        packet.getId()));
            }
        });
        ClientPacketEvents.PONG_RESPONSE_PACKET.register(packet -> {
            if (enablePongResponsePacketListenerDebug) {
                sendDebugMessages("Received Pong Response Packet\n- Time: %d".formatted(
                        packet.time()));
            }
        });
        ClientPacketEvents.RESOURCE_PACK_PUSH_PACKET.register(packet -> {
            if (enableResourcePackPacketsListenerDebug) {
                sendDebugMessages("Received Resource Pack Push:\n- ID: %s\n- Hash: %s\n- URL: %s\n- Prompt: %s\n- Required: %s".formatted(
                        packet.id(), packet.hash(), packet.url(), packet.prompt(), packet.required()));
            }
        });
        ClientPacketEvents.RESOURCE_PACK_POP_PACKET.register(packet -> {
            if (enableResourcePackPacketsListenerDebug) {
                sendDebugMessages("Received Resource Pack Pop:\n- ID: %s".formatted(
                        packet.id()));
            }
        });
        ClientPacketEvents.RESOURCE_PACK_ACTION_PACKET.register(packet -> {
            if (enableResourcePackPacketsListenerDebug) {
                sendDebugMessages("Send Resource Pack Action:\n- ID: %s\n- Action: %s".formatted(
                        packet.id(), packet.action()));
            }
        });
    }

    private static void sendDebugMessages(String message) {
        if (client.player != null)
            client.player.sendSystemMessage(Component.literal(message));
        LOGGER.info(message);
    }
}
