package de.fuchsmod.features.general;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
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
        ClientPlayConnectionEvents.DISCONNECT.register((listener, client) -> {
            TPSMeasurement.getInstance().reset();
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
        final int BREAKPOINT = 15;
        int red = Integer.max(Integer.min((int) (tps <= BREAKPOINT ?
                8.5 * tps + 170 :
                -51.0 * tps + 1020
        ), 255), 0);
        int green = Integer.max(Integer.min((int) (tps <= BREAKPOINT ?
                34.0 * tps - 255 :
                -51.0 * tps + 1150
        ), 255), tps <= BREAKPOINT ? 0 : 170);
        int blue = Integer.max(Integer.min((int) (tps <= BREAKPOINT ?
                8.5 * tps :
                -51.0 * tps + 1020
        ), 85), 0);
        return 256 * 256 * red + 256 * green + blue;
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
