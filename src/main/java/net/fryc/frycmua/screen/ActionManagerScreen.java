package net.fryc.frycmua.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fryc.frycmua.item.action.client.ActionManager;
import net.fryc.frycmua.item.action.client.ActionPriority;
import net.fryc.frycmua.item.action.client.PriorityManager;
import net.fryc.frycmua.keybind.ModKeybinds;
import net.fryc.frycmua.screen.widget.UseActionEntryWidget;
import net.fryc.frycmua.util.interfaces.player.client.HoldsActionManager;
import net.fryc.frycmua.util.interfaces.widget.CyclingButtonWidgetAdditionalUpdate;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ActionManagerScreen extends Screen {

    private static final String ACTION_PRIORITY_TRANSLATION_KEY = "text.frycmua.action_priority";
    private static final String DEFAULT_BEHAVIOUR_TRANSLATION_KEY = "text.frycmua.default_behaviour";
    private static final String SWITCH_PRIORITY_TRANSLATION_KEY = "text.frycmua.switch_priority";
    private static final String ENTRIES_EMPTY_TRANSLATION_KEY = "text.frycmua.entries_empty";
    private static final String ADD_MISSING_ACTIONS_TRANSLATION_KEY = "text.frycmua.add_missing_actions";
    private static final String DELETE_UNKNOWN_ACTIONS_TRANSLATION_KEY = "text.frycmua.delete_unknown_actions";

    private UseActionEntryWidget widget;
    private PriorityManager selectedPriorityManager;
    private final List<ButtonWidget> keybindChoiceButtons = new ArrayList<>();
    private final List<CyclingButtonWidget<?>> cyclingButtons = new ArrayList<>();
    private final Screen parent;

    public ActionManagerScreen(Screen parent) {
        super(Text.empty());
        this.parent = parent;
        this.selectedPriorityManager = PriorityManager.DEFAULT_PRIORITY_MANAGER;
        PriorityManager.DEFAULT_PRIORITY_MANAGER.validate();
        ModKeybinds.MUA_ADDITIONAL_USE_KEYS.values().forEach(PriorityManager::validate);
    }


    @Override
    protected void init() {
        // TODO dodac scrollowanie jak sie nie mieszcza guziki na ekranie
        // TODO dodac guzik dodajacy pozostale akcje i usuwajacy nieistniejace
        this.keybindChoiceButtons.clear();
        this.cyclingButtons.clear();

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
            this.reorderAllChildren();
        }).position(20, 20).size(this.textRenderer.getWidth(useKeyText) + 5, 20).build();
        useKeyButton.active = !this.selectedPriorityManager.equals(PriorityManager.DEFAULT_PRIORITY_MANAGER);
        this.addDrawableChild(useKeyButton);
        this.keybindChoiceButtons.add(useKeyButton);
        // TODO jest cos takiego jak grid widget, musze tego uzyc
        ModKeybinds.MUA_ADDITIONAL_USE_KEYS.forEach((key, value) -> {
            Text text = Text.literal(
                    Text.translatable(key.getTranslationKey()).getString() +
                            " (" +
                            key.getBoundKeyLocalizedText().getString() +
                            ")"
            );
            int lastButtonXWithWidth = this.keybindChoiceButtons.isEmpty() ? 0 : this.keybindChoiceButtons.getLast().getX() + this.keybindChoiceButtons.getLast().getWidth();
            ButtonWidget buttonWidget = ButtonWidget.builder(text, (btn) -> {
                this.selectedPriorityManager = value;
                this.keybindChoiceButtons.forEach(btn2 -> btn2.active = true);
                this.cyclingButtons.forEach(btn2 -> ((CyclingButtonWidgetAdditionalUpdate) btn2).update());
                btn.active = false;
                this.reorderAllChildren();
            }).position(15 + lastButtonXWithWidth + 5, 20).size(this.textRenderer.getWidth(text) + 5, 20).build();

            buttonWidget.active = !this.selectedPriorityManager.equals(value);
            this.addDrawableChild(buttonWidget);
            this.keybindChoiceButtons.add(buttonWidget);
        });

        this.widget = new UseActionEntryWidget(this.client, this, this.width / 2, (this.height / 4) * 3 - 50, this.height / 4, this.width / 2, 40);
        this.addDrawableChild(this.widget);
        this.addAllChildren();

        CyclingButtonWidget<ActionPriority> actionPriorityButton =
                CyclingButtonWidget.<ActionPriority>builder((actionPriority) -> Text.translatable(actionPriority.getTranslationKey()))
                        .values(ActionPriority.values())
                        .initially(this.selectedPriorityManager.getActionPriority())
                        .build(40, this.height / 2, this.width / 5 + 5, 20, Text.translatable(ACTION_PRIORITY_TRANSLATION_KEY), (btn, actionPriority) -> {
                            this.selectedPriorityManager.setActionPriority(actionPriority);
                            this.reorderAllChildren();
                        });

        ((CyclingButtonWidgetAdditionalUpdate) actionPriorityButton).setUpdateFunction(() -> {
            actionPriorityButton.setValue(this.selectedPriorityManager.getActionPriority());
        });
        this.addDrawableChild(actionPriorityButton);
        this.cyclingButtons.add(actionPriorityButton);

        // TODO guzik do zmieniania czy switchable


        ButtonWidget addMissingActionsBtn = ButtonWidget.builder(Text.translatable(ADD_MISSING_ACTIONS_TRANSLATION_KEY), (btn) -> {
            if(!this.isActionPriorityDefault()) {
                this.selectedPriorityManager.addOtherUseActions();
                this.reorderAllChildren();
            }
        }).position(40, this.height / 2 + 30).size(this.width / 5 + 5, 20).build();
        this.addDrawableChild(addMissingActionsBtn);

        ButtonWidget delUnknownActionsBtn = ButtonWidget.builder(Text.translatable(DELETE_UNKNOWN_ACTIONS_TRANSLATION_KEY), (btn) -> {
            if(!this.isActionPriorityDefault()) {
                this.selectedPriorityManager.deleteInvalidActions();
                this.reorderAllChildren();
            }
        }).position(40, this.height / 2 + 60).size(this.width / 5 + 5, 20).build();
        this.addDrawableChild(delUnknownActionsBtn);

        this.addDrawableChild(
                ButtonWidget.builder(ScreenTexts.DONE, btn -> this.close())
                        .position(this.width / 2 - 100, this.height - 28)
                        .size(200, 20)
                        .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if(this.isActionPriorityDefault()) {
            context.drawTextWrapped(this.textRenderer, Text.translatable(DEFAULT_BEHAVIOUR_TRANSLATION_KEY), this.widget.getX() + this.widget.getWidth() / 6, this.height / 3, (int) (this.widget.getWidth() * 0.8F), 0xFFFFFFFF);
            context.drawTextWrapped(this.textRenderer, Text.translatable(SWITCH_PRIORITY_TRANSLATION_KEY), this.widget.getX() + this.widget.getWidth() / 6, this.height / 3 + 30, (int) (this.widget.getWidth() * 0.8F), 0xFFFFFFFF);
        }
        else if(this.getSelectedPriorityManager().getEntries().isEmpty()) {
            context.drawTextWrapped(this.textRenderer, Text.translatable(ENTRIES_EMPTY_TRANSLATION_KEY), this.widget.getX() + this.widget.getWidth() / 6, this.height / 3, (int) (this.widget.getWidth() * 0.8F), 0xFFFFFFFF);
        }
        /*
        ItemStack stack = this.client.player.getInventory().getStack(this.client.player.getInventory().selectedSlot);
        if(!stack.isEmpty()){
            context.drawItem(stack, 0, 0);

        }

        ((HoldsActionManager) this.client.player).getAllActionManagers().forEach(manager -> {
            int i = 0;
            for(Identifier id : manager.getAvailableActions()){
                context.drawText(this.textRenderer, id.toString(), 50, 50 + i, 0xFFFFFFFF, true);
                i += 10;
            }
        });
        */

        // Minecraft doesn't have a "label" widget, so we'll have to draw our own text.
        // We'll subtract the font height from the Y position to make the text appear above the button.
        // Subtracting an extra 10 pixels will give the text some padding.
        // textRenderer, text, x, y, color, hasShadow
        //context.drawText(this.textRenderer, "Special Button", 40, 40 - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
    }

    @Override
    public void close() {
        if(this.client.player != null) {
            ((HoldsActionManager) this.client.player).getAllActionManagers().forEach(ActionManager::forceUpdateAvailableActions);
        }

        if (this.parent != null) {
            this.client.setScreen(this.parent);
        }
        else {
            super.close();
        }
    }

    public void reorderAllChildren() {
        this.widget.children().clear();
        this.widget.setSelected(null);
        this.addAllChildren();
    }
// TODO switchable beda mogly byc wszystkie ale bedzie jeden keybind ktory bedzie zmienial je wszystkie (wiec bedzie mozna miec kilka zestawow do uzywania)
    public void addAllChildren() {
        if(!this.isActionPriorityDefault() /*|| this.getSelectedPriorityManager().isSwitchable()*/) {
            this.getSelectedPriorityManager().getEntries().forEach(entry -> {
                this.widget.children().add(new UseActionEntryWidget.UseActionEntry(this.client, this.widget, entry));
            });
        }
    }

    public PriorityManager getSelectedPriorityManager() {
        return this.selectedPriorityManager;
    }

    private boolean isActionPriorityDefault() {
        return this.selectedPriorityManager.getActionPriority().equals(ActionPriority.DEFAULT);
    }
}
