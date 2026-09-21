package de.fuchsmod.commands;

import de.fuchsmod.events.ClientPacketEvents;
import net.minecraft.network.chat.Component;

import static de.fuchsmod.FuchsMod.FUCHSMOD_DEBUG_CHAT_MESSAGE_PREFIX;
import static de.fuchsmod.FuchsMod.LOGGER;
import static de.fuchsmod.FuchsMod.CLIENT;

public class Debug {
    static boolean enableSetTimePacketListenerDebug = false;
    static boolean enablePingPacketListenerDebug = false;
    static boolean enablePongResponsePacketListenerDebug = false;
    static boolean enableResourcePackPacketsListenerDebug = false;

    public static void init() {
        ClientPacketEvents.SET_TIME_PACKET.register(packet -> {
            if (enableSetTimePacketListenerDebug) {
                sendDebugMessage("Received Set Time Packet\n- GameTime: %d".formatted(
                        packet.gameTime()));
            }
        });
        ClientPacketEvents.PING_PACKET.register(packet -> {
            if (enablePingPacketListenerDebug) {
                sendDebugMessage("Received Ping Packet\n- ID: %d".formatted(
                        packet.getId()));
            }
        });
        ClientPacketEvents.PONG_RESPONSE_PACKET.register(packet -> {
            if (enablePongResponsePacketListenerDebug) {
                sendDebugMessage("Received Pong Response Packet\n- Time: %d".formatted(
                        packet.time()));
            }
        });
        ClientPacketEvents.RESOURCE_PACK_PUSH_PACKET.register(packet -> {
            if (enableResourcePackPacketsListenerDebug) {
                sendDebugMessage("Received Resource Pack Push:\n- ID: %s\n- Hash: %s\n- URL: %s\n- Prompt: %s\n- Required: %s".formatted(
                        packet.id(), packet.hash(), packet.url(), packet.prompt(), packet.required()));
            }
        });
        ClientPacketEvents.RESOURCE_PACK_POP_PACKET.register(packet -> {
            if (enableResourcePackPacketsListenerDebug) {
                sendDebugMessage("Received Resource Pack Pop:\n- ID: %s".formatted(
                        packet.id()));
            }
        });
        ClientPacketEvents.RESOURCE_PACK_ACTION_PACKET.register(packet -> {
            if (enableResourcePackPacketsListenerDebug) {
                sendDebugMessage("Send Resource Pack Action:\n- ID: %s\n- Action: %s".formatted(
                        packet.id(), packet.action()));
            }
        });
    }

    public static void sendDebugMessage(String message) {
        Component debugMessage = FUCHSMOD_DEBUG_CHAT_MESSAGE_PREFIX.get().append(message);
        if (CLIENT.player != null)
            CLIENT.player.sendSystemMessage(debugMessage);
        LOGGER.info(message);
    }
    
    public static void sendDebugMessage(String message, boolean enabled) {
        if (enabled)
            sendDebugMessage(message);
    }
}
