package net.fryc.frycmua.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fryc.frycmua.item.action.client.ActionManager;
import net.fryc.frycmua.item.action.client.ActionPriority;
import net.fryc.frycmua.screen.widget.UseActionEntryWidget;
import net.fryc.frycmua.util.interfaces.player.client.HoldsActionManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ActionManagerScreen extends Screen {

    private UseActionEntryWidget widget;

    public ActionManagerScreen(Text title) {
        super(title);
    }


    @Override
    protected void init() {
        /*
        ButtonWidget buttonWidget = ButtonWidget.builder(Text.of("Hello World"), (btn) -> {
            if(this.client != null){
                if(this.client.player != null){
                    //((SelectsUseAction) this.client.player).setSelectedUseAction(Identifier.ofVanilla("bow_pull"));
                    btn.active = false;
                }
            }

            // When the button is clicked, we can display a toast to the screen.
            this.client.getToastManager().add(
                    SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE, Text.of("Hello World!"), Text.of("This is a toast."))
            );
        }).dimensions(40, 40, 120, 20).build();


        // x, y, width, height
        // It's recommended to use the fixed height of 20 to prevent rendering issues with the button
        // textures.


        // Register the button widget.
        //this.addDrawableChild(buttonWidget);


        int i = 0;
        for(ActionManager manager : ((HoldsActionManager) this.client.player).getAllActionManagers()){
            ButtonWidget buttonWidget = ButtonWidget.builder(Text.translatable(manager.keybinding.getTranslationKey()), (btn) -> {
                

                btn.active = false;
            }).position(120 + i, 40).build();
            this.addDrawableChild(buttonWidget);

            i += 5 + buttonWidget.getWidth();
        }
*/
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
        ActionPriority.MIXED.getAllPartiallyOrderedUseActions().forEach(id -> {
            this.widget.children().add(new UseActionEntryWidget.UseActionEntry(this.client, this.widget, id));
        });
    }
}
