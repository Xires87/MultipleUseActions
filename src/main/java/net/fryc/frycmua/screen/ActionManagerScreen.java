package net.fryc.frycmua.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fryc.frycmua.item.action.client.ActionManager;
import net.fryc.frycmua.item.action.client.PriorityManager;
import net.fryc.frycmua.keybind.ModKeybinds;
import net.fryc.frycmua.screen.widget.UseActionEntryWidget;
import net.fryc.frycmua.util.interfaces.player.client.HoldsActionManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ActionManagerScreen extends Screen {

    private UseActionEntryWidget widget;
    private PriorityManager selectedPriorityManager;
    private List<ButtonWidget> buttons = new ArrayList<>();

    public ActionManagerScreen(Text title) {
        super(title);
        this.selectedPriorityManager = PriorityManager.DEFAULT_PRIORITY_MANAGER;
    }


    @Override
    protected void init() {
        // TODO dodac scrollowanie jak sie nie mieszcza guziki na ekranie
        this.buttons = new ArrayList<>();
        Text useKeyText = Text.translatable(this.client.options.useKey.getTranslationKey());
        ButtonWidget useKeyButton = ButtonWidget.builder(useKeyText, (btn) -> {
            this.selectedPriorityManager = PriorityManager.DEFAULT_PRIORITY_MANAGER;
            this.buttons.forEach(btn2 -> btn2.active = true);
            btn.active = false;
            this.reorderAllChildren();
        }).position(20, 20).size(this.textRenderer.getWidth(useKeyText) + 5, 20).build();
        useKeyButton.active = !this.selectedPriorityManager.equals(PriorityManager.DEFAULT_PRIORITY_MANAGER);
        this.addDrawableChild(useKeyButton);
        this.buttons.add(useKeyButton);

        ModKeybinds.MUA_ADDITIONAL_USE_KEYS.forEach((key, value) -> {
            Text text = Text.translatable(key.getTranslationKey());
            int lastButtonXWithWidth = this.buttons.isEmpty() ? 0 : this.buttons.getLast().getX() + this.buttons.getLast().getWidth();
            ButtonWidget buttonWidget = ButtonWidget.builder(text, (btn) -> {
                this.selectedPriorityManager = value;
                this.buttons.forEach(btn2 -> btn2.active = true);
                btn.active = false;
                this.reorderAllChildren();
            }).position(15 + lastButtonXWithWidth + 5, 20).size(this.textRenderer.getWidth(text) + 5, 20).build();

            buttonWidget.active = !this.selectedPriorityManager.equals(value);
            this.addDrawableChild(buttonWidget);
            this.buttons.add(buttonWidget);
        });

        this.widget = new UseActionEntryWidget(this.client, this, this.width / 2, (this.height / 4) * 3, this.height / 4, this.width / 2, 40);
        this.addDrawableChild(this.widget);
        this.addAllChildren();
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

    public void addAllChildren() {
        this.getSelectedPriorityManager().getEntries().forEach(entry -> {
            this.widget.children().add(new UseActionEntryWidget.UseActionEntry(this.client, this.widget, entry));
        });
    }

    public PriorityManager getSelectedPriorityManager() {
        return this.selectedPriorityManager;
    }

    public void setSelectedPriorityManager(PriorityManager priorityManager) {
        this.selectedPriorityManager = priorityManager;
    }
}
