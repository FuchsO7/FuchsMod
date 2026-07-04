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
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

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

    private final StringWidget scopesLabel = new StringWidget(DEFAULT_WIDTH, DEFAULT_HEIGHT, Component.literal("Scopes"), font);
    private final StringWidget triggerLabel = new StringWidget(DEFAULT_WIDTH, DEFAULT_HEIGHT, Component.literal("Trigger:"), font);
    private final StringWidget commandLabel = new StringWidget(DEFAULT_WIDTH, DEFAULT_HEIGHT, Component.literal("Executes:"), font);
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
        if ((value.scopes() >> 2) % 2 == 1 )
            this.guildScope = true;
        if ((value.scopes() >> 3) % 2 == 1 )
            this.officerScope = true;
        this.trigger = value.trigger();
        this.command = value.command();
        this.replacementFunction = value.replacementFunction();
    }

    public PartyCommandRecord exportSettings() {
        int scopes = 0;
        if (publicScope)
            scopes += 1;
        if (partyScope)
            scopes += 2;
        if (guildScope)
            scopes += 4;
        if (officerScope)
            scopes += 8;

        return new PartyCommandRecord(scopes, triggerText.getValue(), commandText.getValue(), replacementFunction);
    }

    @Override
    public void onClose() {
        option.requestSet(exportSettings());
        minecraft.gui.setScreen(parent);
    }

    @Override
    protected void init() {
        super.init();
        layout.addTitleHeader(getTitle(), font);
        LinearLayout footerLayout = layout.addToFooter(LinearLayout.horizontal().spacing(DEFAULT_SPACING));
        footerLayout.addChild(Button.builder(CommonComponents.GUI_DONE, _ -> onClose()).build());

        LinearLayout content = layout.addToContents(LinearLayout.vertical().spacing(DEFAULT_SPACING));
        content.defaultCellSetting().alignHorizontallyCenter();

        LinearLayout triggerRow = content.addChild(LinearLayout.horizontal().spacing(DEFAULT_SPACING));
        triggerRow.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle();
        triggerRow.addChild(triggerLabel);
        triggerText = new EditBox(font, Component.literal(""));
        triggerText.setMaxLength(255);
        triggerText.setValue(trigger);
        triggerText.setWidth(width / 2);
        triggerText.setTooltip(Tooltip.create(Component.literal("The command will be executed if this string is found in chat.")));
        triggerRow.addChild(triggerText);

        LinearLayout commandRow = content.addChild(LinearLayout.horizontal().spacing(DEFAULT_SPACING));
        commandRow.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle();
        commandRow.addChild(commandLabel);
        commandText = new EditBox(font, Component.literal(""));
        commandText.setMaxLength(255);
        commandText.setValue(command);
        commandText.setWidth(width / 2);
        commandText.setTooltip(Tooltip.create(Component.literal("This is the command to be executed. " +
                "The {player} placeholder will be replaced with the senders name. " +
                "Use the {args[i]} placeholders to add arguments given after the trigger.")));
        commandRow.addChild(commandText);

        LinearLayout scopesRow = content.addChild(LinearLayout.horizontal().spacing(DEFAULT_SPACING));
        scopesRow.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle();
        scopesRow.addChild(scopesLabel);
        LinearLayout scopesColumn = scopesRow.addChild(LinearLayout.vertical().spacing(DEFAULT_SPACING));
        scopesColumn.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle();
        publicScopeButton = CycleButton.onOffBuilder(publicScope).create(Component.literal("Public"),
                (button, value) -> publicScope = value);
        publicScopeButton.setWidth(width / 2);
        scopesColumn.addChild(publicScopeButton);
        partyScopeButton = CycleButton.onOffBuilder(partyScope).create(Component.literal("Party"),
                (button, value) -> partyScope = value);
        partyScopeButton.setWidth(width / 2);
        scopesColumn.addChild(partyScopeButton);
        guildScopeButton = CycleButton.onOffBuilder(guildScope).create(Component.literal("Guild"),
                (button, value) -> guildScope = value);
        guildScopeButton.setWidth(width / 2);
        scopesColumn.addChild(guildScopeButton);
        officerScopeButton = CycleButton.onOffBuilder(officerScope).create(Component.literal("Officer"),
                (button, value) -> officerScope = value);
        officerScopeButton.setWidth(width / 2);
        scopesColumn.addChild(officerScopeButton);

        LinearLayout specialFunctionRow = content.addChild(LinearLayout.horizontal().spacing(DEFAULT_SPACING));
        replacementFunctionButton = CycleButton.builder(Component::literal, replacementFunction)
                .withValues(PartyCommandUtils.replacementCommands.keySet())
                .create(Component.literal("Special Replacement Function"),
                        (button, value) -> replacementFunction = value);
        replacementFunctionButton.setWidth(width / 2 + DEFAULT_WIDTH + DEFAULT_SPACING);
        replacementFunctionButton.setTooltip(Tooltip.create(Component.literal("On execution, the placeholder {function} will be replaced by this functions return value.")));
        specialFunctionRow.addChild(replacementFunctionButton);

        layout.visitWidgets(this::addRenderableWidget);
        layout.arrangeElements();
    }
}
