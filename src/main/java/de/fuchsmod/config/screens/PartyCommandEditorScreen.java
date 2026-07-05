package de.fuchsmod.config.screens;

import de.fuchsmod.config.controllers.PartyCommandRecord;
import de.fuchsmod.features.partycommands.PartyCommandUtils;
import dev.isxander.yacl3.api.Option;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class PartyCommandEditorScreen extends Screen {
    private final Screen parent;
    private final Option<PartyCommandRecord> option;

    private boolean publicScope = false;
    private boolean partyScope = false;
    private boolean guildScope = false;
    private boolean officerScope = false;
    private final String trigger;
    private final String command;
    private String replacementFunction;

    private static final int DEFAULT_SPACING = 4;
    private static final int DEFAULT_WIDTH = 60;
    private static final int DEFAULT_HEIGHT = 20;

    private final StringWidget triggerLabel = new StringWidget(DEFAULT_WIDTH, DEFAULT_HEIGHT, Component.literal("Trigger:"), font);
    private final StringWidget commandLabel = new StringWidget(DEFAULT_WIDTH, DEFAULT_HEIGHT, Component.literal("Executes:"), font);
    private final StringWidget scopesLabel = new StringWidget(DEFAULT_WIDTH, DEFAULT_HEIGHT, Component.literal("Scopes:"), font);
    private EditBox triggerText;
    private EditBox commandText;
    private CycleButton<Boolean> publicScopeButton;
    private CycleButton<Boolean> partyScopeButton;
    private CycleButton<Boolean> guildScopeButton;
    private CycleButton<Boolean> officerScopeButton;
    private CycleButton<String> replacementFunctionButton;

    public PartyCommandEditorScreen(Screen parent, Option<PartyCommandRecord> option) {
        super(Component.literal("Edit Party Command"));
        this.parent = parent;
        this.option = option;
        PartyCommandRecord value = option.pendingValue();
        if (value.scopes() % 2 == 1)
            this.publicScope = true;
        if ((value.scopes() >> 1) % 2 == 1)
            this.partyScope = true;
        if ((value.scopes() >> 2) % 2 == 1)
            this.guildScope = true;
        if ((value.scopes() >> 3) % 2 == 1)
            this.officerScope = true;
        this.trigger = value.trigger();
        this.command = value.command();
        this.replacementFunction = value.replacementFunction();
    }

    public PartyCommandRecord getSettings() {
        int scopes = 0;
        if (this.publicScope)
            scopes += 1;
        if (this.partyScope)
            scopes += 2;
        if (this.guildScope)
            scopes += 4;
        if (this.officerScope)
            scopes += 8;

        return new PartyCommandRecord(scopes, this.triggerText.getValue(), this.commandText.getValue(), this.replacementFunction);
    }

    @Override
    public void onClose() {
        PartyCommandRecord newSettings = getSettings();
        if (!newSettings.equals(this.option.pendingValue()))
            this.option.requestSet(getSettings());
        minecraft.gui.setScreen(this.parent);
    }

    @Override
    protected void init() {
        HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
        layout.addTitleHeader(getTitle(), font);
        LinearLayout footerLayout = layout.addToFooter(LinearLayout.horizontal().spacing(DEFAULT_SPACING));
        footerLayout.addChild(Button.builder(CommonComponents.GUI_DONE, _ -> onClose()).build());

        LinearLayout content = layout.addToContents(LinearLayout.vertical().spacing(DEFAULT_SPACING));

        LinearLayout triggerRow = content.addChild(LinearLayout.horizontal().spacing(DEFAULT_SPACING));
        triggerRow.addChild(this.triggerLabel);
        this.triggerText = new EditBox(font, Component.literal(""));
        this.triggerText.setMaxLength(255);
        this.triggerText.setValue(this.trigger);
        this.triggerText.setWidth(width / 2);
        this.triggerText.setTooltip(Tooltip.create(Component.literal("The command will be executed if this string is found in chat.")));
        triggerRow.addChild(this.triggerText);

        LinearLayout commandRow = content.addChild(LinearLayout.horizontal().spacing(DEFAULT_SPACING));
        commandRow.addChild(this.commandLabel);
        this.commandText = new EditBox(font, Component.literal(""));
        this.commandText.setMaxLength(255);
        this.commandText.setValue(this.command);
        this.commandText.setWidth(width / 2);
        this.commandText.setTooltip(Tooltip.create(Component.literal("This is the command to be executed. " +
                "The {player} placeholder will be replaced with the senders name. " +
                "Use the {args[i]} placeholders to add arguments given after the trigger.")));
        commandRow.addChild(this.commandText);

        LinearLayout scopesRow = content.addChild(LinearLayout.horizontal().spacing(DEFAULT_SPACING));
        scopesRow.defaultCellSetting().alignVerticallyMiddle();
        scopesRow.addChild(this.scopesLabel);
        LinearLayout scopesColumn = scopesRow.addChild(LinearLayout.vertical().spacing(DEFAULT_SPACING));
        this.publicScopeButton = CycleButton.onOffBuilder(this.publicScope).create(Component.literal("Public"),
                (button, value) -> this.publicScope = value);
        this.publicScopeButton.setWidth(width / 2);
        scopesColumn.addChild(this.publicScopeButton);
        this.partyScopeButton = CycleButton.onOffBuilder(this.partyScope).create(Component.literal("Party"),
                (button, value) -> this.partyScope = value);
        this.partyScopeButton.setWidth(width / 2);
        scopesColumn.addChild(this.partyScopeButton);
        this.guildScopeButton = CycleButton.onOffBuilder(this.guildScope).create(Component.literal("Guild"),
                (button, value) -> this.guildScope = value);
        this.guildScopeButton.setWidth(width / 2);
        scopesColumn.addChild(this.guildScopeButton);
        this.officerScopeButton = CycleButton.onOffBuilder(this.officerScope).create(Component.literal("Officer"),
                (button, value) -> this.officerScope = value);
        this.officerScopeButton.setWidth(width / 2);
        scopesColumn.addChild(this.officerScopeButton);

        LinearLayout specialFunctionRow = content.addChild(LinearLayout.horizontal().spacing(DEFAULT_SPACING));
        this.replacementFunctionButton = CycleButton.builder(Component::literal, this.replacementFunction)
                .withValues(PartyCommandUtils.replacementCommands.keySet())
                .create(Component.literal("Special Replacement Function"),
                        (button, value) -> this.replacementFunction = value);
        this.replacementFunctionButton.setWidth(width / 2 + DEFAULT_WIDTH + DEFAULT_SPACING);
        this.replacementFunctionButton.setTooltip(Tooltip.create(Component.literal("On execution, the placeholder {function} will be replaced by this functions return value.")));
        specialFunctionRow.addChild(this.replacementFunctionButton);

        layout.arrangeElements();
        layout.visitWidgets(this::addRenderableWidget);
    }
}
