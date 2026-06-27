package de.fuchsmod.config;

import dev.isxander.yacl3.api.*;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.network.chat.Component;

public class FuchsModConfig {
    // General
    @SerialEntry
    public boolean showTPSHud = false;

    @SerialEntry
    public double TPSHudXPos = 2.0;

    @SerialEntry
    public double TPSHudYPos = 2.0;

    @SerialEntry
    public TPSPacketTypes packetTypeForTPSMeasurement = TPSPacketTypes.SetTime;

    public enum TPSPacketTypes implements NameableEnum {
        SetTime,
        Ping;

        @Override
        public Component getDisplayName() {
            return Component.literal(name());
        }
    }

    @SerialEntry
    public boolean showFPSHud = false;

    @SerialEntry
    public double FPSHudXPos = 2.0;

    @SerialEntry
    public double FPSHudYPos = 5.0;

    @SerialEntry
    public boolean showPingHud = false;

    @SerialEntry
    public double PingHudXPos = 2.0;

    @SerialEntry
    public double PingHudYPos = 8.0;

    @SerialEntry
    public boolean alwaysSendPingRequest = true;

    @SerialEntry
    public boolean enableTooltipScroll = false;

    @SerialEntry
    public int scrollFactor = 5;

    @SerialEntry
    public int verticalScrollDirection = -1;

    @SerialEntry
    public int horizontalScrollDirection = 1;

    // PartyCommands
    @SerialEntry
    public boolean enablePartyCommands = false;
}
