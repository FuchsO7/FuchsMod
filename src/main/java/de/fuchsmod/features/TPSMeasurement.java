package de.fuchsmod.features;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.util.Util;

import java.util.LinkedList;
import java.util.Queue;

public class TPSMeasurement {
    private static final TPSMeasurement INSTANCE = new TPSMeasurement();
    public static final int AVERAGE_SAMPLE_TIME_SECONDS = 5;

    private int packetCount = 0;
    private long lastTimeMillis = 0L;
    private long lastTick = 0L;
    private double estimatedMSPT;
    private double estimatedTPS;
    private double averageTPS;
    private Queue<Double> TPSResults = new LinkedList<>();

    private TPSMeasurement() {
        ClientPlayConnectionEvents.DISCONNECT.register((listener, client) -> {
            TPSMeasurement.getInstance().reset();
        });
    }

    public static TPSMeasurement getInstance() {
        return INSTANCE;
    }

    public void onSetTimePacket(ClientboundSetTimePacket packet) {
        if (FuchsModConfigManager.getInstance().packetTypeForTPSMeasurement == FuchsModConfig.TPSPacketTypes.SetTime) {
            long gameTime = packet.gameTime();
            long packetTimeMillis = Util.getMillis();

            long elapsedTimeMillis = packetTimeMillis - this.lastTimeMillis;
            this.lastTimeMillis = packetTimeMillis;
            long elapsedTicks = gameTime - this.lastTick;
            this.lastTick = gameTime;

            calculateTPS((double) elapsedTimeMillis, elapsedTicks);
        }
    }

    public void onPingPacket(ClientboundPingPacket packet) {
        if (FuchsModConfigManager.getInstance().packetTypeForTPSMeasurement == FuchsModConfig.TPSPacketTypes.Ping) {
            this.packetCount++;
            if (this.packetCount >= 20) {
                this.packetCount = 0;

                long packetTimeMillis = Util.getMillis();

                long elapsedTimeMillis = packetTimeMillis - this.lastTimeMillis;
                this.lastTimeMillis = packetTimeMillis;

                calculateTPS((double) elapsedTimeMillis, 20L);
            }
        }
    }

    private void calculateTPS(double elapsedTimeMillis, long elapsedTicks) {
        if (elapsedTimeMillis < 10.0 || elapsedTicks == 0L) {
            return;
        }
        this.estimatedMSPT = elapsedTimeMillis / (double) elapsedTicks;
        this.estimatedTPS = 1000.0 / this.estimatedMSPT;

        this.TPSResults.offer((this.estimatedTPS));
        if (this.TPSResults.size() > AVERAGE_SAMPLE_TIME_SECONDS) {
            this.TPSResults.poll();
        }

        double sum = 0.0;
        for (double TPS : this.TPSResults) {
            sum += TPS;
        }
        this.averageTPS = sum / this.TPSResults.size();
    }

    public void reset() {
        this.packetCount = 0;
        this.lastTimeMillis = 0L;
        this.lastTick = 0L;
        this.estimatedMSPT = 0.0;
        this.averageTPS = 0.0;
        this.TPSResults = new LinkedList<>();
    }

    private static ChatFormatting getTPSColor(double tps) {
        if (tps > 19.0) {
            return ChatFormatting.DARK_GREEN;
        } else if (tps > 17.5) {
            return ChatFormatting.GREEN;
        } else if (tps > 15.0) {
            return ChatFormatting.YELLOW;
        } else if (tps > 10.0) {
            return ChatFormatting.RED;
        } else {
            return ChatFormatting.DARK_RED;
        }
    }

    public double getMSPT() {
        return this.estimatedMSPT;
    }

    public double getTPS() {
        return this.estimatedTPS;
    }

    public double getAverageTPS() {
        return this.averageTPS;
    }

    public Component getCurrentTPSFormatted() {
        return Component.literal("%.1f".formatted(this.estimatedTPS)).withStyle(getTPSColor(this.estimatedTPS));
    }

    public Component getAverageTPSFormatted() {
        if (this.TPSResults.size() >= AVERAGE_SAMPLE_TIME_SECONDS) {
            return Component.literal("%.1f".formatted(this.averageTPS)).withStyle(getTPSColor(this.averageTPS));
        } else {
            return Component.literal("???").withStyle(ChatFormatting.WHITE);
        }
    }
}
