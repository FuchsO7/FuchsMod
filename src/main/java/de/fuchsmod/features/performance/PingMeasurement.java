package de.fuchsmod.features.performance;

import de.fuchsmod.events.ClientPacketEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.util.Util;


public class PingMeasurement extends AbstractMeasurement<Long> {
    private static final PingMeasurement INSTANCE = new PingMeasurement();

    private PingMeasurement() {
        AVERAGING_SAMPLE_AMOUNT = 100;
    }

    public static PingMeasurement getInstance() {
        return INSTANCE;
    }

    public static void init() {
        ClientPacketEvents.ON_PONG_RESPONSE_PACKET.register(INSTANCE::onPongResponsePacket);
        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((client, clientLevel) -> reset());
    }

    private void onPongResponsePacket(ClientboundPongResponsePacket packet) {
        addSample(calculatePing(packet.time()), Util.getMillis());
    }

    private long calculatePing(long packetTime) {
        return Util.getMillis() - packetTime;
    }

    public static void reset() {
        INSTANCE.clearSamples();
    }

    public static long getPing() {
        return INSTANCE.getCurrent();
    }

    public static long getAveragePing() {
        return (long) INSTANCE.getAverage();
    }

    public static long getMinimumPing() {
        return (long) INSTANCE.getMinimum();
    }

    public static long getMaximumPing() {
        return (long) INSTANCE.getMaximum();
    }
}
