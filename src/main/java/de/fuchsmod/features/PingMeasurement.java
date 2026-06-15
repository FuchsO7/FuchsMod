package de.fuchsmod.features;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.util.Util;

import java.util.LinkedList;
import java.util.Queue;

public class PingMeasurement {
    public static PingMeasurement INSTANCE = new PingMeasurement();

    private long estimatedPing;
    private long averagePing5sec;
    Queue<Long> PingResults = new LinkedList<>();

    public PingMeasurement() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.getConnection() != null) {
                ServerboundPingRequestPacket packet = new ServerboundPingRequestPacket(Util.getMillis());
                client.getConnection().getConnection().send(packet);
            }
        });
    }

    public void onPongResponsePacket(ClientboundPongResponsePacket packet) {
        calculatePing(packet.time());
    }

    private void calculatePing(long packetTime) {
        this.estimatedPing = Util.getMillis() - packetTime;

        this.PingResults.offer((this.estimatedPing));
        if (this.PingResults.size() > 100) {
            this.PingResults.poll();
        }

        long sum = 0L;
        for (long ping : this.PingResults) {
            sum += ping;
        }
        this.averagePing5sec = sum / this.PingResults.size();
    }

    public void reset() {
        this.averagePing5sec = 0L;
        this.PingResults = new LinkedList<>();
    }

    public long getCurrentPing() {
        return this.estimatedPing;
    }

    public long[] getPing() {
        return new long[]{this.estimatedPing, this.averagePing5sec, (long) this.PingResults.size()};
    }
}
