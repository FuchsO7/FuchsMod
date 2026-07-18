package de.fuchsmod.features.general;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import de.fuchsmod.events.ClientPacketEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.util.Util;

import java.net.URI;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import static de.fuchsmod.FuchsMod.LOGGER;

public class ResourcePackIgnore {
    private static final FuchsModConfig config = FuchsModConfigManager.getInstance();
    private static UUID packID;
    private static String url;
    private static final Queue<ScheduledPacket> packetsToSend = new LinkedList<>();
    private static Component scheduledMessage;
    private static Connection connection;

    public static void init() {
        ClientPacketEvents.RESOURCE_PACK_PUSH_PACKET.register(packet -> {
            packID = packet.id();
            url = packet.url();
        });
        ClientPacketEvents.NEW_CONNECTION.register(newConnection -> {
            connection = newConnection;
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ScheduledPacket scheduledPacket = packetsToSend.peek();
            if (scheduledPacket == null) {
                if (client.player != null && scheduledMessage != null) {
                    client.player.sendSystemMessage(scheduledMessage);
                    scheduledMessage = null;
                }
                return;
            }
            if (scheduledPacket.time() < Util.getMillis()) {
                Packet<?> packet = packetsToSend.poll().packet();
                if (client.getConnection() != null)
                    client.getConnection().send(packet);
                else {
                    LOGGER.warn("Client Connection was null, using last known connection to send resource pack download packets");
                    connection.send(packet);
                }
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
        packetsToSend.offer(new ScheduledPacket(time, new ServerboundResourcePackPacket(packID, ServerboundResourcePackPacket.Action.ACCEPTED)));
        packetsToSend.offer(new ScheduledPacket(time + config.serverResourcePackIgnoreTimeMillis / 2, new ServerboundResourcePackPacket(packID, ServerboundResourcePackPacket.Action.DOWNLOADED)));
        packetsToSend.offer(new ScheduledPacket(time + config.serverResourcePackIgnoreTimeMillis, new ServerboundResourcePackPacket(packID, ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED)));
        LOGGER.info("Scheduled Serverbound Packets for Pack Download Imitation");
        if (config.sendServerResourcePackDownloadLink)
            scheduledMessage = Component.literal("The ignored Server Resource Pack is available at: %s".formatted(url))
                    .withStyle(Style.EMPTY.withClickEvent(new ClickEvent.OpenUrl(URI.create(url))));
    }
}
