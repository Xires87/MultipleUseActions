package net.fryc.frycmua.screen.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fryc.frycmua.item.action.client.PriorityManager;
import net.fryc.frycmua.screen.ActionManagerScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class UseActionEntryWidget extends EntryListWidget<UseActionEntryWidget.UseActionEntry> {

    private static final Identifier MOVE_UP_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("transferable_list/move_up_highlighted");
    private static final Identifier MOVE_UP_TEXTURE = Identifier.ofVanilla("transferable_list/move_up");
    private static final Identifier MOVE_DOWN_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("transferable_list/move_down_highlighted");
    private static final Identifier MOVE_DOWN_TEXTURE = Identifier.ofVanilla("transferable_list/move_down");

    private final ActionManagerScreen screen;


    public UseActionEntryWidget(MinecraftClient client, ActionManagerScreen screen, int width, int height, int y, int x, int itemHeight) {
        super(client, width, height, y, itemHeight);
        this.setX(x);
        this.screen = screen;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }


    @Environment(EnvType.CLIENT)
    public static class UseActionEntry extends EntryListWidget.Entry<UseActionEntry> {

        private final MinecraftClient client;
        private final UseActionEntryWidget parent;
        private final PriorityManager.Entry action;


        public UseActionEntry(MinecraftClient client, UseActionEntryWidget parent, PriorityManager.Entry action) {
            this.client = client;
            this.parent = parent;
            this.action = action;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawText(this.client.textRenderer, this.action.getActionId().toString(), x, y, 0xFFFFFFFF, true);

            int j = mouseX - x;
            int k = mouseY - y;

            if(this.equals(this.parent.getSelectedOrNull())){
                if(this.canBeHigherInList()){
                    if (j < 32 && j > 16 && k < 16) {
                        context.drawGuiTexture(MOVE_UP_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
                    }
                    else {
                        context.drawGuiTexture(MOVE_UP_TEXTURE, x, y, 32, 32);
                    }
                }

                if(this.canBeLowerInList()){
                    if (j < 32 && j > 16 && k > 16) {
                        context.drawGuiTexture(MOVE_DOWN_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
                    }
                    else {
                        context.drawGuiTexture(MOVE_DOWN_TEXTURE, x, y, 32, 32);
                    }
                }
            }
        }


        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if(this.equals(this.parent.getSelectedOrNull())){
                int i = this.parent.children().indexOf(this);
                double d = mouseX - (double)this.parent.getRowLeft();
                double e = mouseY - (double)this.parent.getRowTop(i);
                if (d <= (double)32.0F) {
                    if(this.canBeHigherInList(i)){
                        if (d > (double)16.0F && e < (double)16.0F) {
                            this.parent.screen.getSelectedPriorityManager().moveUp(this.action);
                            this.parent.screen.reorderAllChildren();
                            this.parent.children().stream().filter(child -> child.action.getActionId().toString().equals(this.action.getActionId().toString())).findFirst().ifPresent(action -> {
                                action.parent.setSelected(action);
                            });

                            return true;
                        }
                    }

                    if(this.canBeLowerInList(i)){
                        if (d > (double)16.0F && e > (double)16.0F) {
                            this.parent.screen.getSelectedPriorityManager().moveDown(this.action);
                            this.parent.screen.reorderAllChildren();
                            this.parent.children().stream().filter(child -> child.action.getActionId().toString().equals(this.action.getActionId().toString())).findFirst().ifPresent(action -> {
                                action.parent.setSelected(action);
                            });

                            return true;
                        }
                    }
                }
            }
            else {
                this.parent.setSelected(this);

                return true;
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        public boolean canBeHigherInList(int index) {
            return index > 0;
        }

        public boolean canBeHigherInList() {
            return this.canBeHigherInList(this.parent.children().indexOf(this));
        }

        public boolean canBeLowerInList(int index) {
            return index < this.parent.children().size() - 1;
        }

        public boolean canBeLowerInList() {
            return this.canBeLowerInList(this.parent.children().indexOf(this));
        }
    }
}
