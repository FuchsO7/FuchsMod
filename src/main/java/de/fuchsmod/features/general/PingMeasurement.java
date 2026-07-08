package de.fuchsmod.features.general;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.FuchsModConfigManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.util.Util;

import java.util.LinkedList;
import java.util.Queue;

public class PingMeasurement {
    private static final PingMeasurement INSTANCE = new PingMeasurement();
    private static final FuchsModConfig config = FuchsModConfigManager.getInstance();
    public static final int AVERAGE_SAMPLE_TIME_SECONDS = 5;

    private long estimatedPing;
    private long averagePing;
    private Queue<Long> PingResults = new LinkedList<>();

    private PingMeasurement() {
        ClientPlayConnectionEvents.DISCONNECT.register((listener, client) -> {
            INSTANCE.reset();
        });
    }

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

    private static TextColor getDiscretePingColor(long ping) {
        if (ping < 50L) {
            return TextColor.DARK_GREEN;
        } else if (ping < 150L) {
            return TextColor.GREEN;
        } else if (ping < 250L) {
            return TextColor.YELLOW;
        } else if (ping < 400L) {
            return TextColor.RED;
        } else {
            return TextColor.DARK_RED;
        }
    }

    private static int getContinuousPingColor(long ping) {
        final int BREAKPOINT = 150;
        int red = Integer.max(Integer.min((int) (ping <= BREAKPOINT ?
                        1.7f * ping :
                        -0.34f * ping + 340
                ), 255), 0);
        int green = Integer.max(Integer.min((int) (ping <= BREAKPOINT ?
                        1.7f * ping + 170 :
                        -1.7f * ping + 510
                ), 255), ping <= BREAKPOINT ? 170 : 0);
        int blue = Integer.max(Integer.min((int) (ping <= BREAKPOINT ?
                        1.7f * ping :
                        -0.34f * ping + 170
                ), 85), 0);
        return 256 * 256 * red + 256 * green + blue;
    }

    private static int getPingColor(long ping) {
        return config.useContinuousColorsForPingHud ? getContinuousPingColor(ping) : getDiscretePingColor(ping).getValue();
    }

    public long getPing() {
        return this.estimatedPing;
    }

    public long getAveragePing() {
        return this.averagePing;
    }

    public Component getCurrentPingFormatted() {
        return Component.literal("%d".formatted(this.estimatedPing)).withColor(getPingColor(this.estimatedPing));
    }

    public Component getAveragePingFormatted() {
        if (this.PingResults.size() >= 20 * AVERAGE_SAMPLE_TIME_SECONDS) {
            return Component.literal("%d".formatted(this.averagePing)).withColor(getPingColor(this.estimatedPing));
        } else {
            return Component.literal("???").withColor(TextColor.WHITE);
        }
    }
}
