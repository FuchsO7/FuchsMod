package de.fuchsmod.features.performance;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.events.ClientPacketEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.util.Util;

import static de.fuchsmod.FuchsMod.CONFIG;

public class TPSMeasurement extends AbstractMeasurement<Double> {
    private static final TPSMeasurement INSTANCE = new TPSMeasurement();

    private int packetCount = 0;
    private long lastTimeMillis = 0L;
    private long lastGameTime = 0L;

    private TPSMeasurement() {

    }

    public static TPSMeasurement getInstance() {
        return INSTANCE;
    }

    public static void init() {
        ClientPacketEvents.ON_SET_TIME_PACKET.register(INSTANCE::onSetTimePacket);
        ClientPacketEvents.ON_PING_PACKET.register(INSTANCE::onPingPacket);
        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((client, clientLevel) -> reset());
    }

    private void onSetTimePacket(ClientboundSetTimePacket packet) {
        if (CONFIG.packetTypeForTPSMeasurement != FuchsModConfig.TPSPacketTypes.SetTime)
            return;
        long gameTime = packet.gameTime();
        long elapsedTicks = gameTime - this.lastGameTime;
        if (elapsedTicks < 10L)
            return;
        this.lastGameTime = gameTime;
        addElapsedTicksSample(elapsedTicks);
    }

    private void onPingPacket(ClientboundPingPacket packet) {
        if (CONFIG.packetTypeForTPSMeasurement != FuchsModConfig.TPSPacketTypes.Ping)
            return;
        this.packetCount++;
        if (this.packetCount >= 20) {
            this.packetCount = 0;
            addElapsedTicksSample(20L);
        }
    }

    private void addElapsedTicksSample(long elapsedTicks) {
        long packetTimeMillis = Util.getMillis();
        long elapsedTimeMillis = packetTimeMillis - this.lastTimeMillis;
        this.lastTimeMillis = packetTimeMillis;

        if (elapsedTimeMillis == lastTimeMillis)
            return;
        addSample(calculateTPS((double) elapsedTimeMillis, elapsedTicks), packetTimeMillis);
    }

    private double calculateTPS(double elapsedTimeMillis, long elapsedTicks) {
        return 1000.0 / (elapsedTimeMillis / (double) elapsedTicks);
    }

    public static void reset() {
        INSTANCE.packetCount = 0;
        INSTANCE.lastTimeMillis = 0L;
        INSTANCE.lastGameTime = 0L;
        INSTANCE.clearSamples();
    }

    public static double getTPS() {
        return INSTANCE.getCurrent();
    }

    public static double getAverageTPS() {
        return INSTANCE.getAverage();
    }

    public static double getMinimumTPS() {
        return INSTANCE.getMinimum();
    }

    public static double getMaximumTPS() {
        return INSTANCE.getMaximum();
    }
}
