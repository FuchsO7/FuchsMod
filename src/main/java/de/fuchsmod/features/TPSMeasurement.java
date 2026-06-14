package de.fuchsmod.features;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.util.Util;

import java.util.LinkedList;
import java.util.Queue;

public class TPSMeasurement {
    public static TPSMeasurement INSTANCE = new TPSMeasurement();

    private int packetCount = 0;
    private long lastTimeMillis = 0L;
    private long lastTick = 0L;
    private double estimatedMSPT;
    private double estimatedTPS;
    private double averageTPS5sec;
    Queue<Double> TPSResults = new LinkedList<>();

    public void onSetTimePacket(ClientboundSetTimePacket packet) {
        if (FuchsModConfigManager.get().packetTypeForTPSMeasurement == FuchsModConfig.TPSPacketTypes.SetTime) {
            long gameTime = packet.gameTime();
            long packetTimeMillis = Util.getMillis();

            long elapsedTimeMillis = packetTimeMillis - this.lastTimeMillis;
            this.lastTimeMillis = packetTimeMillis;
            long elapsedTicks = gameTime - this.lastTick;
            this.lastTick = gameTime;

            calculateTPS((double) elapsedTimeMillis, (double) elapsedTicks);
        }
    }

    public void onPingPacket(ClientboundPingPacket packet) {
        if (FuchsModConfigManager.get().packetTypeForTPSMeasurement == FuchsModConfig.TPSPacketTypes.Ping) {
            this.packetCount++;
            if (this.packetCount >= 20) {
                this.packetCount = 0;

                long packetTimeMillis = Util.getMillis();

                long elapsedTimeMillis = packetTimeMillis - this.lastTimeMillis;
                this.lastTimeMillis = packetTimeMillis;

                calculateTPS((double) elapsedTimeMillis, 20.0);
            }
        }
    }

    private void calculateTPS(double elapsedTimeMillis, double elapsedTicks) {
        this.estimatedMSPT = elapsedTimeMillis / elapsedTicks;
        this.estimatedTPS = 1000.0 / this.estimatedMSPT;

        this.TPSResults.offer((estimatedTPS));
        if (this.TPSResults.size() > 5) {
            this.TPSResults.poll();
        }

        double sum = 0.0;
        for (double TPS : TPSResults) {
            sum += TPS;
        }
        this.averageTPS5sec = sum / TPSResults.size();
    }

    public void reset() {
        this.packetCount = 0;
        this.lastTimeMillis = 0L;
        this.lastTick = 0L;
        this.estimatedMSPT = 0.0;
        this.averageTPS5sec = 0.0;
        this.TPSResults = new LinkedList<>();
    }

    public double getCurrentTPS() {
        return this.estimatedTPS;
    }

    public double[] getTPS() {
        return new double[]{this.estimatedTPS, this.averageTPS5sec, (double) this.TPSResults.size()};
    }
}
