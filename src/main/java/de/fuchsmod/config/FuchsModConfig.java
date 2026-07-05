package de.fuchsmod.config;

import de.fuchsmod.config.controllers.PartyCommandRecord;
import dev.isxander.yacl3.api.*;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

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

    @SerialEntry
    public boolean enableCustomGamma = false;

    @SerialEntry
    public double customGamma = 1.0;

    // PartyCommands
    @SerialEntry
    public boolean enablePartyCommands = false;

    @SerialEntry
    public int commandDelay = 500;

    @SerialEntry
    public List<PartyCommandRecord> partyCommandsList = new ArrayList<>(List.of(
            new PartyCommandRecord(2, "!warp", "/party warp", "None"),
            new PartyCommandRecord(2, "!allinv", "/party settings allinvite true", "None"),
            new PartyCommandRecord(2, "!ptme", "/party transfer {player}", "None"),
            new PartyCommandRecord(2, "!invite", "/party invite {args[0]}", "None"),
            new PartyCommandRecord(2, "!kick", "/party kick {args[0]}", "None"),
            new PartyCommandRecord(2, "!kickoffline", "/party kickoffline", "None"),
            new PartyCommandRecord(2, "!promote", "/party promote {args[0]}", "None"),
            new PartyCommandRecord(2, "!demote", "/party demote {args[0]}", "None"),
            new PartyCommandRecord(2, "!dice", "/pc {player} rolled a {function}!", "Dice Roll"),
            new PartyCommandRecord(2, "!tps", "/pc TPS: {function}", "Get TPS"),
            new PartyCommandRecord(2, "!fps", "/pc FPS: {function}", "Get FPS"),
            new PartyCommandRecord(2, "!ping", "/pc Ping: {function}", "Get Ping"),
            new PartyCommandRecord(2, "!f1", "/joininstance CATACOMBS_FLOOR_ONE", "None"),
            new PartyCommandRecord(2, "!f2", "/joininstance CATACOMBS_FLOOR_TWO", "None"),
            new PartyCommandRecord(2, "!f3", "/joininstance CATACOMBS_FLOOR_THREE", "None"),
            new PartyCommandRecord(2, "!f4", "/joininstance CATACOMBS_FLOOR_FOUR", "None"),
            new PartyCommandRecord(2, "!f5", "/joininstance CATACOMBS_FLOOR_FIVE", "None"),
            new PartyCommandRecord(2, "!f6", "/joininstance CATACOMBS_FLOOR_SIX", "None"),
            new PartyCommandRecord(2, "!f7", "/joininstance CATACOMBS_FLOOR_SEVEN", "None"),
            new PartyCommandRecord(2, "!m1", "/joininstance MASTER_CATACOMBS_FLOOR_ONE", "None"),
            new PartyCommandRecord(2, "!m2", "/joininstance MASTER_CATACOMBS_FLOOR_TWO", "None"),
            new PartyCommandRecord(2, "!m3", "/joininstance MASTER_CATACOMBS_FLOOR_THREE", "None"),
            new PartyCommandRecord(2, "!m4", "/joininstance MASTER_CATACOMBS_FLOOR_FOUR", "None"),
            new PartyCommandRecord(2, "!m5", "/joininstance MASTER_CATACOMBS_FLOOR_FIVE", "None"),
            new PartyCommandRecord(2, "!m6", "/joininstance MASTER_CATACOMBS_FLOOR_SIX", "None"),
            new PartyCommandRecord(2, "!m7", "/joininstance MASTER_CATACOMBS_FLOOR_SEVEN", "None"),
            new PartyCommandRecord(2, "!t1", "/joininstance KUUDRA_NORMAL", "None"),
            new PartyCommandRecord(2, "!t2", "/joininstance KUUDRA_HOT", "None"),
            new PartyCommandRecord(2, "!t3", "/joininstance KUUDRA_BURNING", "None"),
            new PartyCommandRecord(2, "!t4", "/joininstance KUUDRA_FIERY", "None"),
            new PartyCommandRecord(2, "!t5", "/joininstance KUUDRA_INFERNAL", "None"),
            new PartyCommandRecord(2, "!b8x2", "/play bedwars_eight_two", "None"),
            new PartyCommandRecord(2, "!b4x4", "/play bedwars_four_four", "None"),
            new PartyCommandRecord(2, "!b4x3", "/play bedwars_four_three", "None"),
            new PartyCommandRecord(2, "!b2x4", "/play bedwars_two_four", "None"),
            new PartyCommandRecord(2, "!b4v4", "/play bedwars_two_four", "None"),
            new PartyCommandRecord(2, "!b8x2d", "/play bedwars_eight_two_{function}", "Bedwars Dream"),
            new PartyCommandRecord(2, "!b4x4d", "/play bedwars_four_four_{function}", "Bedwars Dream"),
            new PartyCommandRecord(2, "!mc", "/play murder_classic", "None"),
            new PartyCommandRecord(2, "!md", "/play murder_double_up", "None"),
            new PartyCommandRecord(2, "!mi", "/play murder_infection", "None"),
            new PartyCommandRecord(2, "!ma", "/play murder_assassins", "None")
    ));
}
