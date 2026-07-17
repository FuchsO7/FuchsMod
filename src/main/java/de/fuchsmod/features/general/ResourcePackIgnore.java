package de.fuchsmod.features.general;

import de.fuchsmod.events.ClientPacketEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.util.Util;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import static de.fuchsmod.FuchsMod.LOGGER;

public class ResourcePackIgnore {
    private static final Minecraft client = Minecraft.getInstance();
    public static UUID packID;
    private static final Queue<ScheduledPacket> packetsToSend = new LinkedList<>();

    public static void init() {
        ClientPacketEvents.RESOURCE_PACK_PUSH_PACKET.register(packet -> {
            packID = packet.id();
        });
        ClientTickEvents.END_LEVEL_TICK.register(level ->{
            ScheduledPacket scheduledPacket = packetsToSend.peek();
            if (scheduledPacket == null)
                return;
            if (scheduledPacket.time() < Util.getMillis() && client.getConnection() != null) {
                Packet<?> packet = packetsToSend.poll().packet();
                client.getConnection().send(packet);
            }
        });
        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((client, clientLevel) -> {
            packetsToSend.clear();
        });
        LOGGER.info("Initialized Server Resource Pack Ignore!");
    }

    public record ScheduledPacket (long time, Packet<?> packet) {
    }

    public static void imitateResourcePackDownload() {
        long time = Util.getMillis();
        packetsToSend.offer(new ScheduledPacket(time + 1000L, new ServerboundResourcePackPacket(packID, ServerboundResourcePackPacket.Action.ACCEPTED)));
        packetsToSend.offer(new ScheduledPacket(time + 3000L, new ServerboundResourcePackPacket(packID, ServerboundResourcePackPacket.Action.DOWNLOADED)));
        packetsToSend.offer(new ScheduledPacket(time + 5000L, new ServerboundResourcePackPacket(packID, ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED)));
    }
}
