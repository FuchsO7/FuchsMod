package de.fuchsmod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;

public class ClientPacketEvents {

    public static final Event<SetTimePacket> SET_TIME_PACKET = EventFactory.createArrayBacked(SetTimePacket.class, callbacks -> packet -> {
        for (SetTimePacket event : callbacks) {
            event.onSetTimePacket(packet);
        }
    });

    public static final Event<PongResponsePacket> PONG_RESPONSE_PACKET = EventFactory.createArrayBacked(PongResponsePacket.class, callbacks -> packet -> {
        for (PongResponsePacket event : callbacks) {
            event.onPongResponsePacket(packet);
        }
    });

    public static final Event<PingPacket> PING_PACKET = EventFactory.createArrayBacked(PingPacket.class, callbacks -> packet -> {
        for (PingPacket event : callbacks) {
            event.onPingPacket(packet);
        }
    });

    @FunctionalInterface
    public interface SetTimePacket {
        void onSetTimePacket(ClientboundSetTimePacket packet);
    }

    @FunctionalInterface
    public interface PongResponsePacket {
        void onPongResponsePacket(ClientboundPongResponsePacket packet);
    }

    @FunctionalInterface
    public interface PingPacket {
        void onPingPacket(ClientboundPingPacket packet);
    }
}
