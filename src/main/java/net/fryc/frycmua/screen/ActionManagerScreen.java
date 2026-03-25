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
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ActionManagerScreen extends Screen {

    private static final String ACTION_PRIORITY_TRANSLATION_KEY = "text.frycmua.action_priority";

    private UseActionEntryWidget widget;
    private PriorityManager selectedPriorityManager;
    private final List<ButtonWidget> keybindChoiceButtons = new ArrayList<>();
    private final List<CyclingButtonWidget<?>> cyclingButtons = new ArrayList<>();

    public ActionManagerScreen(Text title) {
        super(title);
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

        Text useKeyText = Text.translatable(this.client.options.useKey.getTranslationKey());
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

        ModKeybinds.MUA_ADDITIONAL_USE_KEYS.forEach((key, value) -> {
            Text text = Text.translatable(key.getTranslationKey());
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

        this.widget = new UseActionEntryWidget(this.client, this, this.width / 2, (this.height / 4) * 3, this.height / 4, this.width / 2, 40);
        this.addDrawableChild(this.widget);
        this.addAllChildren();

        CyclingButtonWidget<ActionPriority> actionPriorityButton =
                CyclingButtonWidget.<ActionPriority>builder((actionPriority) -> Text.of(actionPriority.toString()))
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
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);


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

        super.close();
    }

    public void reorderAllChildren() {
        this.widget.children().clear();
        this.widget.setSelected(null);
        this.addAllChildren();
    }
// TODO switchable beda mogly byc wszystkie ale bedzie jeden keybind ktory bedzie zmienial je wszystkie (wiec bedzie mozna miec kilka zestawow do uzywania)
    public void addAllChildren() {
        if(!this.getSelectedPriorityManager().getActionPriority().equals(ActionPriority.DEFAULT) /*|| this.getSelectedPriorityManager().isSwitchable()*/) {
            this.getSelectedPriorityManager().getEntries().forEach(entry -> {
                this.widget.children().add(new UseActionEntryWidget.UseActionEntry(this.client, this.widget, entry));
            });
        }
    }

    public PriorityManager getSelectedPriorityManager() {
        return this.selectedPriorityManager;
    }

    public void setSelectedPriorityManager(PriorityManager priorityManager) {
        this.selectedPriorityManager = priorityManager;
    }

    private ActionPriority getNextActionPriority(ActionPriority current) {
        return current.ordinal() + 1 < ActionPriority.values().length ? ActionPriority.values()[current.ordinal() + 1] : ActionPriority.values()[0];
    }
}
