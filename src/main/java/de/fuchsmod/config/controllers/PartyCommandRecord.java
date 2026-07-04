package de.fuchsmod.config.controllers;

public record PartyCommandRecord(int scopes, String trigger, String command, String replacementFunction) {
}
