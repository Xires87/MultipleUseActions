package net.fryc.frycmua.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fryc.frycmua.MultipleUseActions;
import net.fryc.frycmua.item.action.client.ActionPriority;
import net.fryc.frycmua.item.action.client.PriorityManager;
import net.fryc.frycmua.keybind.ModKeybinds;
import net.fryc.frycmua.screen.widget.UseActionEntryWidget;
import net.fryc.frycmua.util.interfaces.player.client.HoldsActionManager;
import net.fryc.frycmua.util.interfaces.widget.CyclingButtonWidgetAdditionalUpdate;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AxisGridWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ActionManagerScreen extends Screen {

    private static final String ACTION_PRIORITY_TRANSLATION_KEY = "text.frycmua.action_priority";
    private static final String DEFAULT_BEHAVIOUR_TRANSLATION_KEY = "text.frycmua.default_behaviour";
    private static final String SWITCHABLE_TRANSLATION_KEY = "text.frycmua.switchable";
    private static final String SWITCH_PRIORITY_TRANSLATION_KEY = "text.frycmua.switch_priority";
    private static final String ENTRIES_EMPTY_TRANSLATION_KEY = "text.frycmua.entries_empty";
    private static final String ADD_MISSING_ACTIONS_TRANSLATION_KEY = "text.frycmua.add_missing_actions";
    private static final String DELETE_UNKNOWN_ACTIONS_TRANSLATION_KEY = "text.frycmua.delete_unknown_actions";
    private static final String U_SURE_U_WANT_KEYBIND_TRANSLATION_KEY = "text.frycmua.u_sure_u_want_new_keybind";

    private static final String ADD_NEW_KEYBIND_TRANSLATION_KEY = "button.frycmua.add_new_keybind";
    private static final String NAVIGATE_LEFT_TRANSLATION_KEY = "button.frycmua.navigate_left";
    private static final String NAVIGATE_RIGHT_TRANSLATION_KEY = "button.frycmua.navigate_right";

    private static final Identifier ICON_NAVIGATE_LEFT = Identifier.of(MultipleUseActions.MOD_ID, "icon/navigate_left");
    private static final Identifier ICON_NAVIGATE_RIGHT = Identifier.of(MultipleUseActions.MOD_ID, "icon/navigate_right");

    private static final int PADDING_TOP = 20;
    private static final int PADDING_BOTTOM = 28;
    private static final int PADDING_LEFT = 20;
    private static final int PADDING_RIGHT = 20;
    private static final int NAVIGATION_PADDING = 30;
    private static final int TEXT_BUTTON_PADDING = 5;

    private UseActionEntryWidget widget;
    private PriorityManager selectedPriorityManager;
    private int selectedGrid = 0;
    private ButtonWidget navigateLeftButton;
    private ButtonWidget navigateRightButton;
    private final List<ButtonWidget> keybindChoiceButtons = new ArrayList<>();
    private final List<CyclingButtonWidget<?>> cyclingButtons = new ArrayList<>();
    private final List<AxisGridWidget> navigationGrids = new ArrayList<>();
    private final Screen parent;
    private int lastRowWidth = 0;

    public ActionManagerScreen(Screen parent) {
        super(Text.empty());
        this.parent = parent;
        this.selectedPriorityManager = PriorityManager.DEFAULT_PRIORITY_MANAGER;
        PriorityManager.DEFAULT_PRIORITY_MANAGER.validate();
        ModKeybinds.MUA_ADDITIONAL_USE_KEYS.values().forEach(PriorityManager::validate);
    }


    @Override
    protected void init() {
        this.createNavigationTab();

        this.cyclingButtons.clear();
        this.createLeftPanel();
        this.createRightPanel();

        this.widget = new UseActionEntryWidget(this.client, this, this.width / 2 - PADDING_RIGHT, this.getBodyHeight(), this.getHeaderHeight(), this.width / 2, 6);
        this.addDrawableChild(this.widget);
        this.addAllEntries();

        this.addDrawableChild(
                ButtonWidget.builder(ScreenTexts.DONE, btn -> this.close())
                        .position(this.width / 2 - 100, this.height - PADDING_BOTTOM)
                        .size(200, 20)
                        .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if(this.shouldShowDefault()) {
            context.drawTextWrapped(
                    this.textRenderer, Text.translatable(DEFAULT_BEHAVIOUR_TRANSLATION_KEY),
                    this.widget.getX() + this.widget.getWidth() / 6, this.widget.getY() + this.widget.getHeight() / 3,
                    (int) (this.widget.getWidth() * 0.8F), 0xFFFFFFFF
            );
            context.drawTextWrapped(
                    this.textRenderer, Text.translatable(SWITCH_PRIORITY_TRANSLATION_KEY),
                    this.widget.getX() + this.widget.getWidth() / 6, this.widget.getY() + this.widget.getHeight() / 3 + 30,
                    (int) (this.widget.getWidth() * 0.8F), 0xFFFFFFFF
            );
        }
        else if(this.getSelectedPriorityManager().getEntries().isEmpty()) {
            context.drawTextWrapped(
                    this.textRenderer, Text.translatable(ENTRIES_EMPTY_TRANSLATION_KEY),
                    this.widget.getX() + this.widget.getWidth() / 6, this.widget.getY() + this.widget.getHeight() / 3,
                    (int) (this.widget.getWidth() * 0.8F), 0xFFFFFFFF
            );
        }
    }

    @Override
    public void close() {
        if(this.client.player != null) {
            ((HoldsActionManager) this.client.player).getAllActionManagers().forEach(manager -> {
                manager.resetChosenAction();
                manager.forceUpdateAvailableActions();
            });
        }

        if (this.parent != null) {
            this.client.setScreen(this.parent);
        }
        else {
            super.close();
        }
    }

    public void reorderAllEntries() {
        this.widget.children().clear();
        this.widget.setSelected(null);
        this.addAllEntries();
    }

    public void addAllEntries() {
        if(!this.shouldShowDefault()) {
            this.getSelectedPriorityManager().getEntries().forEach(entry -> {
                this.widget.children().add(new UseActionEntryWidget.UseActionEntry(this.client, this.widget, entry));
            });
        }
    }

    public PriorityManager getSelectedPriorityManager() {
        return this.selectedPriorityManager;
    }

    public AxisGridWidget getNavigationGridByIndex(int index) {
        if(index < 0) return this.navigationGrids.getFirst();

        return this.navigationGrids.size() > index ? this.navigationGrids.get(index) : this.navigationGrids.getLast();
    }

    private boolean shouldShowDefault() {
        return this.isActionPriorityDefault() && !this.getSelectedPriorityManager().isSwitchable();
    }

    private boolean isActionPriorityDefault() {
        return this.selectedPriorityManager.getActionPriority().equals(ActionPriority.DEFAULT);
    }

    private void createNavigationTab() {
        this.lastRowWidth = 0;

        TextIconButtonWidget navigateLeftButton = TextIconButtonWidget.builder(Text.translatable(NAVIGATE_LEFT_TRANSLATION_KEY), btn -> {
            this.jumpToNavigationGrid(-1);
        }, true).width(20).texture(ICON_NAVIGATE_LEFT, 15, 15).build();
        navigateLeftButton.setPosition(5, 20);
        this.navigateLeftButton = navigateLeftButton;
        this.addDrawableChild(navigateLeftButton);

        this.createKeybindChoiceButtons();

        TextIconButtonWidget navigateRightButton = TextIconButtonWidget.builder(Text.translatable(NAVIGATE_RIGHT_TRANSLATION_KEY), btn -> {
            this.jumpToNavigationGrid(1);
        }, true).width(20).texture(ICON_NAVIGATE_RIGHT, 15, 15).build();
        navigateRightButton.setPosition(this.width - 25, 20);
        this.navigateRightButton = navigateRightButton;
        this.addDrawableChild(navigateRightButton);

        this.getNavigationGridByIndex(this.selectedGrid).forEachChild(el -> el.visible = true);
        updateNavigationButtons();
    }

    private void jumpToNavigationGrid(int jumpValue) {
        this.getNavigationGridByIndex(this.selectedGrid).forEachChild(el -> el.visible = false);
        AxisGridWidget grid = this.getNavigationGridByIndex(this.selectedGrid + jumpValue);
        this.selectedGrid = this.navigationGrids.indexOf(grid);
        grid.forEachChild(el -> el.visible = true);
        this.updateNavigationButtons();
    }

    private void createKeybindChoiceButtons() {
        this.keybindChoiceButtons.clear();
        this.navigationGrids.clear();
        this.navigationGrids.add(new AxisGridWidget(30, 20, this.getNavigationGridWidth(), 20, AxisGridWidget.DisplayAxis.HORIZONTAL));

        Text useKeyText = Text.literal(
                Text.translatable(this.client.options.useKey.getTranslationKey()).getString() +
                        " (" +
                        this.client.options.useKey.getBoundKeyLocalizedText().getString() +
                        ")"
        );
        ButtonWidget useKeyButton = ButtonWidget.builder(useKeyText, (btn) -> {
            this.selectedPriorityManager = PriorityManager.DEFAULT_PRIORITY_MANAGER;
            this.keybindChoiceButtons.forEach(btn2 -> btn2.active = true);
            this.cyclingButtons.forEach(btn2 -> ((CyclingButtonWidgetAdditionalUpdate) btn2).update());
            btn.active = false;
            this.reorderAllEntries();
        }).position(20, 20).size(this.textRenderer.getWidth(useKeyText) + TEXT_BUTTON_PADDING, 20).build();
        useKeyButton.active = !this.selectedPriorityManager.equals(PriorityManager.DEFAULT_PRIORITY_MANAGER);
        this.keybindChoiceButtons.add(useKeyButton);

        ModKeybinds.MUA_ADDITIONAL_USE_KEYS.forEach((key, value) -> {
            Text text = Text.literal(
                    Text.translatable(key.getTranslationKey()).getString() +
                            " (" +
                            key.getBoundKeyLocalizedText().getString() +
                            ")"
            );
            ButtonWidget buttonWidget = ButtonWidget.builder(text, (btn) -> {
                this.selectedPriorityManager = value;
                this.keybindChoiceButtons.forEach(btn2 -> btn2.active = true);
                this.cyclingButtons.forEach(btn2 -> ((CyclingButtonWidgetAdditionalUpdate) btn2).update());
                btn.active = false;
                this.reorderAllEntries();
            }).size(this.textRenderer.getWidth(text) + TEXT_BUTTON_PADDING, 20).build();

            buttonWidget.active = !this.selectedPriorityManager.equals(value);
            this.keybindChoiceButtons.add(buttonWidget);
        });

        TextIconButtonWidget addNewKeyBind = TextIconButtonWidget.builder(Text.translatable(ADD_NEW_KEYBIND_TRANSLATION_KEY), btn -> {
            this.client.setScreen(new ConfirmWithNumberScreen((bl) -> {
                if(bl && this.client.currentScreen instanceof ConfirmWithNumberScreen confirmScreen) {
                    ModKeybinds.addNewAdditionalUseKeyAndQuit(confirmScreen.getNumber());
                }
                else {
                    this.client.setScreen(this);
                }
            }, Text.empty(), Text.translatable(U_SURE_U_WANT_KEYBIND_TRANSLATION_KEY)));
        }, true).width(20).texture(ConfirmWithNumberScreen.ICON_ADD, 15, 15).build();
        this.keybindChoiceButtons.add(addNewKeyBind);

        this.keybindChoiceButtons.forEach(this::addButtonToLastNavigationGrid);
        this.navigationGrids.forEach(AxisGridWidget::refreshPositions);
        this.navigationGrids.forEach(grid -> {
            grid.forEachChild(this::addDrawableChild);
            grid.forEachChild(el -> el.visible = false);
        });
    }

    private void addButtonToLastNavigationGrid(ButtonWidget widget) {
        AxisGridWidget grid = this.navigationGrids.getLast();
        if(this.lastRowWidth + widget.getWidth() > this.getNavigationGridWidth()) {
            this.navigationGrids.add(new AxisGridWidget(grid.getX(), grid.getY(), grid.getWidth(), grid.getHeight(), AxisGridWidget.DisplayAxis.HORIZONTAL));
            grid = this.navigationGrids.getLast();
            this.lastRowWidth = 0;
        }

        grid.add(widget);
        this.lastRowWidth += widget.getWidth();
    }

    private void createLeftPanel() {
        // TODO zrobic left panel
        AxisGridWidget verticalGrid = new AxisGridWidget(PADDING_LEFT, this.getHeaderHeight(), this.width / 4 - PADDING_LEFT, this.getBodyHeight(), AxisGridWidget.DisplayAxis.VERTICAL);

        CyclingButtonWidget<ActionPriority> actionPriorityButton =
                CyclingButtonWidget.<ActionPriority>builder((actionPriority) -> Text.translatable(actionPriority.getTranslationKey()))
                        .values(ActionPriority.values())
                        .initially(this.selectedPriorityManager.getActionPriority())
                        .build(0, 0, verticalGrid.getWidth(), 20, Text.translatable(ACTION_PRIORITY_TRANSLATION_KEY), (btn, actionPriority) -> {
                            this.selectedPriorityManager.setActionPriority(actionPriority);
                            this.reorderAllEntries();
                        });

        ((CyclingButtonWidgetAdditionalUpdate) actionPriorityButton).setUpdateFunction(() -> {
            actionPriorityButton.setValue(this.selectedPriorityManager.getActionPriority());
        });
        this.cyclingButtons.add(actionPriorityButton);

        CyclingButtonWidget<Boolean> switchableButton =
                CyclingButtonWidget.onOffBuilder()
                        .initially(this.selectedPriorityManager.isSwitchable())
                        .build(0, 0, verticalGrid.getWidth(), 20, Text.translatable(SWITCHABLE_TRANSLATION_KEY), (btn, value) -> {
                            this.selectedPriorityManager.setSwitchable(value);
                            this.reorderAllEntries();
                        });

        ((CyclingButtonWidgetAdditionalUpdate) switchableButton).setUpdateFunction(() -> {
            switchableButton.setValue(this.selectedPriorityManager.isSwitchable());
        });
        this.cyclingButtons.add(switchableButton);

        ButtonWidget addMissingActionsBtn = ButtonWidget.builder(Text.translatable(ADD_MISSING_ACTIONS_TRANSLATION_KEY), (btn) -> {
            if(!this.isActionPriorityDefault()) {
                this.selectedPriorityManager.addOtherUseActions();
                this.reorderAllEntries();
            }
        }).size(verticalGrid.getWidth(), 20).build();

        ButtonWidget delUnknownActionsBtn = ButtonWidget.builder(Text.translatable(DELETE_UNKNOWN_ACTIONS_TRANSLATION_KEY), (btn) -> {
            if(!this.isActionPriorityDefault()) {
                this.selectedPriorityManager.deleteInvalidActions();
                this.reorderAllEntries();
            }
        }).size(verticalGrid.getWidth(), 20).build();

        verticalGrid.add(actionPriorityButton);
        verticalGrid.add(switchableButton);
        verticalGrid.add(addMissingActionsBtn);
        verticalGrid.add(delUnknownActionsBtn);
        verticalGrid.refreshPositions();

        verticalGrid.forEachChild(this::addDrawableChild);
    }

    private void createRightPanel() {
        // TODO zrobic right panel (sortowanie itp)
    }

    public void updateNavigationButtons() {
        this.navigateLeftButton.active = this.selectedGrid > 0;
        this.navigateRightButton.active = this.selectedGrid < this.navigationGrids.size() - 1;
    }

    public int getNavigationGridWidth() {
        return this.width - NAVIGATION_PADDING * 2;
    }

    public int getFooterHeight() {
        return PADDING_BOTTOM + 7;
    }

    public int getHeaderHeight() {
        return PADDING_TOP + 25;
    }

    public int getBodyHeight() {
        return this.height - this.getHeaderHeight() - this.getFooterHeight();
    }
}
