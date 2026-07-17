package de.fuchsmod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
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

    public static final Event<ResourcePackPushPacket> RESOURCE_PACK_PUSH_PACKET = EventFactory.createArrayBacked(ResourcePackPushPacket.class, callbacks -> packet -> {
        for (ResourcePackPushPacket event : callbacks) {
            event.onResourcePackPushPacket(packet);
        }
    });

    public static final Event<ResourcePackPopPacket> RESOURCE_PACK_POP_PACKET = EventFactory.createArrayBacked(ResourcePackPopPacket.class, callbacks -> packet -> {
        for (ResourcePackPopPacket event : callbacks) {
            event.onResourcePackPopPacket(packet);
        }
    });

    public static final Event<ResourcePackActionPacket> RESOURCE_PACK_ACTION_PACKET = EventFactory.createArrayBacked(ResourcePackActionPacket.class, callbacks -> packet -> {
        for (ResourcePackActionPacket event : callbacks) {
            event.onResourcePackActionPacket(packet);
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

    @FunctionalInterface
    public interface ResourcePackPushPacket {
        void onResourcePackPushPacket(ClientboundResourcePackPushPacket packet);
    }

    @FunctionalInterface
    public interface ResourcePackPopPacket {
        void onResourcePackPopPacket(ClientboundResourcePackPopPacket packet);
    }

    @FunctionalInterface
    public interface ResourcePackActionPacket {
        void onResourcePackActionPacket(ServerboundResourcePackPacket packet);
    }
}
