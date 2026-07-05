package de.fuchsmod.config.categories;

import de.fuchsmod.config.FuchsModConfig;
import de.fuchsmod.config.controllers.PartyCommandControllerBuilder;
import de.fuchsmod.config.controllers.PartyCommandRecord;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.network.chat.Component;

public class PartyCommandsCategory {
    public static ConfigCategory create(FuchsModConfig defaults, FuchsModConfig config) {
        return ConfigCategory.createBuilder()
                .name(Component.literal("Party Commands"))
                .group(OptionGroup.createBuilder()
                        .name(Component.literal("Party Commands"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Enable Party Commands"))
                                .description(OptionDescription.of(
                                        Component.literal("Enable Party Commands.")))
                                .binding(defaults.enablePartyCommands,
                                        () -> config.enablePartyCommands,
                                        newValue -> config.enablePartyCommands = newValue)
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true))
                                .build())
                        .build())
                .group(ListOption.<PartyCommandRecord>createBuilder()
                        .name(Component.literal("Configure Commands"))
                        .description(OptionDescription.of(
                                Component.literal("""
                                        Configure Party Commands here.
                                        A command will be executed if the trigger is found in a chat message with matching scope.
                                        Placeholders allow to modify the command depending on variables:
                                        - {player} The senders player name
                                        - {chat} The slash command for the chat of the scope
                                            - AS example, if the scope is 'party', this placeholder becomes '/pc'
                                        - {args[i]} Arguments after trigger, starting at index 0
                                            - As example, {args[0]} refers to the first argument
                                        - {function} Return value of a selectable replacement function
                                        """)))
                        .binding(defaults.partyCommandsList,
                                () -> config.partyCommandsList,
                                newValue -> config.partyCommandsList = newValue)
                        .controller(PartyCommandControllerBuilder::create)
                        .initial(new PartyCommandRecord(0, "", "", "None"))
                        .insertEntriesAtEnd(true)
                        .build())
                .build();
    }
}
