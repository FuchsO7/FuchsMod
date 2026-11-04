package de.fuchsmod.features.partycommands;

public enum Scope {
    PUBLIC,
    PARTY,
    GUILD,
    OFFICER,
    CUSTOM;

    public int getConfigValue() {
        return 1 << this.ordinal();
    }
}
