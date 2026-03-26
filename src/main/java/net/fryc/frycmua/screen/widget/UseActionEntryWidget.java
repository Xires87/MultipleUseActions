package net.fryc.frycmua.screen.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fryc.frycmua.MultipleUseActions;
import net.fryc.frycmua.item.action.client.PriorityManager;
import net.fryc.frycmua.screen.ActionManagerScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

@Environment(EnvType.CLIENT)
public class UseActionEntryWidget extends EntryListWidget<UseActionEntryWidget.UseActionEntry> {

    private static final Identifier MOVE_UP_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("transferable_list/move_up_highlighted");
    private static final Identifier MOVE_UP_TEXTURE = Identifier.ofVanilla("transferable_list/move_up");
    private static final Identifier MOVE_DOWN_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("transferable_list/move_down_highlighted");
    private static final Identifier MOVE_DOWN_TEXTURE = Identifier.ofVanilla("transferable_list/move_down");
    private static final Identifier LOCKED_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/locked");
    private static final Identifier LOCKED_HIGHLIGHTED_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/locked_highlighted");
    private static final Identifier UNLOCKED_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/unlocked");
    private static final Identifier UNLOCKED_HIGHLIGHTED_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/unlocked_highlighted");
    private static final Identifier INACTIVE_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/inactive");
    private static final Identifier INACTIVE_HIGHLIGHTED_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/inactive_highlighted");
    private static final Identifier ACTIVE_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/active");
    private static final Identifier ACTIVE_HIGHLIGHTED_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/active_highlighted");
    private static final Identifier ERROR_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/error");

    private static final int SMALL_TEXTURE_WIDTH = 16;
    private static final int SMALL_TEXTURE_HEIGHT = 16;
    private static final int TEXTURE_WIDTH = 32;
    private static final int TEXTURE_HEIGHT = 32;

    private static final String INVALID_ACTION_TOOLTIP_1 = "text.frycmua.invalid_action_tooltip.does_not_exist";
    private static final String INVALID_ACTION_TOOLTIP_2 = "text.frycmua.invalid_action_tooltip.may_be_leftover";

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
            int j = mouseX - x;
            int k = mouseY - y;

            context.drawGuiTexture(this.action.getActionId(), x + 1, y + 1, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            this.drawWarningIfNotValid(context, x, y, j, k);
            context.drawText(this.client.textRenderer, this.action.getActionId().toString(), x + 34, y + 4, 0xFFFFFFFF, true);

            if(this.equals(this.parent.getSelectedOrNull())){
                this.drawMoveUpTexture(context, x, y, j, k);
                this.drawMoveDownTexture(context, x, y, j, k);

                j = x + entryWidth - mouseX;

                this.drawActiveTexture(context, x, y, j, k, entryWidth);
                this.drawLockedTexture(context, x, y, j, k, entryWidth);
            }
            else {
                if(!this.action.isActive()) {
                    context.drawGuiTexture(INACTIVE_TEXTURE, x + entryWidth - 24, y, SMALL_TEXTURE_WIDTH, SMALL_TEXTURE_HEIGHT);
                }
                else if(this.action.isLocked()) {
                    context.drawGuiTexture(LOCKED_TEXTURE, x + entryWidth - 24, y + 18, SMALL_TEXTURE_WIDTH, SMALL_TEXTURE_HEIGHT);
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
                else {
                    d = (double)this.parent.getRowRight() - mouseX;

                    if(d >= 8 && d <= 24) {
                        if(e < 17) {
                            this.action.setActive(!this.action.isActive());

                            return true;
                        }
                        else if(e > 17) {
                            this.action.setLocked(!this.action.isLocked());

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

        private void drawWarningIfNotValid(DrawContext context, int x, int y, int j, int k) {
            if(!this.parent.screen.getSelectedPriorityManager().isEntryValid(this.action)) {
                context.drawGuiTexture(ERROR_TEXTURE, x + 1, y + 1, TEXTURE_WIDTH, TEXTURE_HEIGHT);

                if (j < 16 && j > 0 && k < 32 && k > 0) {
                    context.drawTooltip(this.client.textRenderer, List.of(
                            Text.translatable(INVALID_ACTION_TOOLTIP_1),
                            Text.translatable(INVALID_ACTION_TOOLTIP_2)
                    ), x + 8 + j + this.client.textRenderer.getWidth(Text.translatable(INVALID_ACTION_TOOLTIP_1)), y + k + 24);
                    //context.drawTextWithBackground(this.client.textRenderer, text, x, y, this.client.textRenderer.getWidth(text), 0xFFFFFFFF);
                }
            }
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

        private void drawMoveUpTexture(DrawContext context, int x, int y, int j, int k) {
            if(this.canBeHigherInList()){
                if (j < 32 && j > 16 && k < 16 && k > 0) {
                    context.drawGuiTexture(MOVE_UP_HIGHLIGHTED_TEXTURE, x, y, TEXTURE_WIDTH, TEXTURE_HEIGHT);
                }
                else {
                    context.drawGuiTexture(MOVE_UP_TEXTURE, x, y, TEXTURE_WIDTH, TEXTURE_HEIGHT);
                }
            }
        }

        private void drawMoveDownTexture(DrawContext context, int x, int y, int j, int k) {
            if(this.canBeLowerInList()){
                if (j < 32 && j > 16 && k > 16 && k < 32) {
                    context.drawGuiTexture(MOVE_DOWN_HIGHLIGHTED_TEXTURE, x, y, TEXTURE_WIDTH, TEXTURE_HEIGHT);
                }
                else {
                    context.drawGuiTexture(MOVE_DOWN_TEXTURE, x, y, TEXTURE_WIDTH, TEXTURE_HEIGHT);
                }
            }
        }

        private void drawActiveTexture(DrawContext context, int x, int y, int j, int k, int entryWidth) {
            if (j <= 24 && j >= 8 && k < 17 && k > 0) {
                context.drawGuiTexture(this.action.isActive() ? ACTIVE_HIGHLIGHTED_TEXTURE : INACTIVE_HIGHLIGHTED_TEXTURE, x + entryWidth - 24, y, SMALL_TEXTURE_WIDTH, SMALL_TEXTURE_HEIGHT);
            }
            else {
                context.drawGuiTexture(this.action.isActive() ? ACTIVE_TEXTURE : INACTIVE_TEXTURE, x + entryWidth - 24, y, SMALL_TEXTURE_WIDTH, SMALL_TEXTURE_HEIGHT);
            }
        }

        private void drawLockedTexture(DrawContext context, int x, int y, int j, int k, int entryWidth) {
            if (j <= 24 && j >= 8 && k > 17 && k < 33) {
                context.drawGuiTexture(this.action.isLocked() ? LOCKED_HIGHLIGHTED_TEXTURE : UNLOCKED_HIGHLIGHTED_TEXTURE, x + entryWidth - 24, y + 18, SMALL_TEXTURE_WIDTH, SMALL_TEXTURE_HEIGHT);
            }
            else {
                context.drawGuiTexture(this.action.isLocked() ? LOCKED_TEXTURE : UNLOCKED_TEXTURE, x + entryWidth - 24, y + 18, SMALL_TEXTURE_WIDTH, SMALL_TEXTURE_HEIGHT);
            }
        }
    }
}
