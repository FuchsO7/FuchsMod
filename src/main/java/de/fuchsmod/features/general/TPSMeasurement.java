package de.fuchsmod.features.general;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import de.fuchsmod.events.ClientPacketEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;

import java.util.LinkedList;
import java.util.Queue;

public class TPSMeasurement {
    private static final TPSMeasurement INSTANCE = new TPSMeasurement();
    private static final FuchsModConfig config = FuchsModConfigManager.getInstance();
    public static final int AVERAGE_SAMPLE_TIME_SECONDS = 5;

    private int packetCount = 0;
    private long lastTimeMillis = 0L;
    private long lastTick = 0L;
    private double estimatedMSPT;
    private double estimatedTPS;
    private double averageTPS;
    private Queue<Double> TPSResults = new LinkedList<>();

    private TPSMeasurement() {
        ClientPacketEvents.SET_TIME_PACKET.register(packet -> {
            INSTANCE.onSetTimePacket(packet);
        });
        ClientPacketEvents.PING_PACKET.register(packet -> {
            INSTANCE.onPingPacket(packet);
        });
        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((client, clientLevel) -> {
            INSTANCE.reset();
        });
    }

    public static TPSMeasurement getInstance() {
        return INSTANCE;
    }

    public void onSetTimePacket(ClientboundSetTimePacket packet) {
        if (config.packetTypeForTPSMeasurement != FuchsModConfig.TPSPacketTypes.SetTime)
            return;
        long gameTime = packet.gameTime();
        long packetTimeMillis = Util.getMillis();

        long elapsedTimeMillis = packetTimeMillis - this.lastTimeMillis;
        this.lastTimeMillis = packetTimeMillis;
        long elapsedTicks = gameTime - this.lastTick;
        this.lastTick = gameTime;

        calculateTPS((double) elapsedTimeMillis, elapsedTicks);
    }

    public void onPingPacket(ClientboundPingPacket packet) {
        if (config.packetTypeForTPSMeasurement != FuchsModConfig.TPSPacketTypes.Ping)
            return;
        this.packetCount++;
        if (this.packetCount >= 20) {
            this.packetCount = 0;

            long packetTimeMillis = Util.getMillis();

            long elapsedTimeMillis = packetTimeMillis - this.lastTimeMillis;
            this.lastTimeMillis = packetTimeMillis;

            calculateTPS((double) elapsedTimeMillis, 20L);
        }
    }

    private void calculateTPS(double elapsedTimeMillis, long elapsedTicks) {
        if (elapsedTimeMillis < 10.0 || elapsedTicks == 0L)
            return;
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

    private static TextColor getDiscreteTPSColor(double tps) {
        if (tps > 19.0) {
            return TextColor.DARK_GREEN;
        } else if (tps > 17.5) {
            return TextColor.GREEN;
        } else if (tps > 15.0) {
            return TextColor.YELLOW;
        } else if (tps > 10.0) {
            return TextColor.RED;
        } else {
            return TextColor.DARK_RED;
        }
    }

    private static int getContinuousTPSColor(double tps) {
        final double BREAKPOINT = 17.5;
        final int darK_green = 0xFF00AA00;
        final int yellow = 0xFFFFFF55;
        final int dark_red = 0xFFAA0000;
        if (tps <= BREAKPOINT) {
            return ARGB.linearLerp((float) Double.max(tps - 10, 0) / 7.5f, dark_red, yellow);
        } else {
            return ARGB.linearLerp(Float.min((float) (tps - BREAKPOINT) / 2.5f, 1f), yellow, darK_green);
        }
    }

    private static int getTPSColor(double tps) {
        return config.useContinuousColorsForTPSHud ? getContinuousTPSColor(tps) : getDiscreteTPSColor(tps).getValue();
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
        return Component.literal("%.1f".formatted(this.estimatedTPS)).withColor(getTPSColor(this.estimatedTPS));
    }

    public Component getAverageTPSFormatted() {
        if (this.TPSResults.size() >= AVERAGE_SAMPLE_TIME_SECONDS) {
            return Component.literal("%.1f".formatted(this.averageTPS)).withColor(getTPSColor(this.averageTPS));
        } else {
            return Component.literal("???").withColor(TextColor.WHITE);
        }
    }
}
