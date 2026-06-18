package de.fuchsmod.features;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.util.Util;

import java.util.LinkedList;
import java.util.Queue;

public class PingMeasurement {
    private static final PingMeasurement INSTANCE = new PingMeasurement();
    public static final int AVERAGE_SAMPLE_TIME_SECONDS = 5;

    public long estimatedPing;
    public long averagePing;
    Queue<Long> PingResults = new LinkedList<>();

    public static PingMeasurement getInstance() {
        return INSTANCE;
    }

    public void onPongResponsePacket(ClientboundPongResponsePacket packet) {
        calculatePing(packet.time());
    }

    private void calculatePing(long packetTime) {
        this.estimatedPing = Util.getMillis() - packetTime;

        this.PingResults.offer((this.estimatedPing));
        if (this.PingResults.size() > 20 * AVERAGE_SAMPLE_TIME_SECONDS) {
            this.PingResults.poll();
        }

        long sum = 0L;
        for (long ping : this.PingResults) {
            sum += ping;
        }
        this.averagePing = sum / this.PingResults.size();
    }

    public void reset() {
        this.averagePing = 0L;
        this.PingResults = new LinkedList<>();
    }

    private static ChatFormatting getPingColor(long ping) {
        if (ping < 50L) {
            return ChatFormatting.DARK_GREEN;
        } else if (ping < 150L) {
            return ChatFormatting.GREEN;
        } else if (ping < 250L) {
            return ChatFormatting.YELLOW;
        } else if (ping < 500L) {
            return ChatFormatting.RED;
        } else {
            return ChatFormatting.DARK_RED;
        }
    }

    public Component getCurrentPingFormatted() {
        return Component.literal("%d".formatted(this.estimatedPing)).withStyle(getPingColor(this.estimatedPing));
    }

    public Component getAveragePingFormatted() {
        if (this.PingResults.size() >= 20 * AVERAGE_SAMPLE_TIME_SECONDS) {
            return Component.literal("%d".formatted(this.averagePing)).withStyle(getPingColor(this.averagePing));
        } else {
            return Component.literal("???").withStyle(ChatFormatting.WHITE);
        }
    }
}
