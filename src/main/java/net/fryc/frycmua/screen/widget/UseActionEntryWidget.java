package net.fryc.frycmua.screen.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fryc.frycmua.MultipleUseActions;
import net.fryc.frycmua.item.action.client.ActionPriority;
import net.fryc.frycmua.item.action.client.PriorityManager;
import net.fryc.frycmua.screen.ActionManagerScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
    private static final Identifier SWITCHABLE_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/switchable");
    private static final Identifier SWITCHABLE_HIGHLIGHTED_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/switchable_highlighted");
    private static final Identifier NOT_SWITCHABLE_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/not_switchable");
    private static final Identifier NOT_SWITCHABLE_HIGHLIGHTED_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/not_switchable_highlighted");
    private static final Identifier LOCK_TYPE_ANY_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/lock_type_any");
    private static final Identifier LOCK_TYPE_SWITCH_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/lock_type_switch");
    private static final Identifier LOCK_TYPE_PRIORITY_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/lock_type_priority");
    private static final Identifier LOCK_TYPE_ANY_HIGHLIGHTED_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/lock_type_any_highlighted");
    private static final Identifier LOCK_TYPE_SWITCH_HIGHLIGHTED_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/lock_type_switch_highlighted");
    private static final Identifier LOCK_TYPE_PRIORITY_HIGHLIGHTED_TEXTURE = Identifier.of(MultipleUseActions.MOD_ID, "transferable_list/lock_type_priority_highlighted");

    private static final int SMALL_TEXTURE_WIDTH = 16;
    private static final int SMALL_TEXTURE_HEIGHT = 16;
    private static final int TEXTURE_WIDTH = 32;
    private static final int TEXTURE_HEIGHT = 32;
    private static final int SCROLLBAR_WIDTH = 6;

    private static final int HORIZONTAL_SPACER = 4;
    private static final int VERTICAL_SPACER = 2;

    private static final String INVALID_ACTION_TOOLTIP_1 = "text.frycmua.invalid_action_tooltip.does_not_exist";
    private static final String INVALID_ACTION_TOOLTIP_2 = "text.frycmua.invalid_action_tooltip.may_be_leftover";

    private final ActionManagerScreen screen;


    public UseActionEntryWidget(MinecraftClient client, ActionManagerScreen screen, int width, int height, int y, int x, int additionalItemHeight) {
        super(client, width, height, y, TEXTURE_HEIGHT + VERTICAL_SPACER + additionalItemHeight);
        this.setX(x);
        this.screen = screen;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public int getRowWidth() {
        return (int) (this.width * 0.9F);
    }

    @Override
    protected int getScrollbarX() {
        return this.getX() + this.getWidth() - SCROLLBAR_WIDTH;
    }

    public int getEntryTextWidth() {
        return this.getRowWidth() - TEXTURE_WIDTH - SMALL_TEXTURE_WIDTH * 2 - (int) (HORIZONTAL_SPACER * 2.5F);
    }

    public boolean isSwitchable() {
        return this.screen.getSelectedPriorityManager().isSwitchable();
    }

    public boolean isActionPriorityDefault() {
        return this.screen.getSelectedPriorityManager().getActionPriority().equals(ActionPriority.DEFAULT);
    }


    @Environment(EnvType.CLIENT)
    public static class UseActionEntry extends Entry<UseActionEntry> {

        private final MinecraftClient client;
        private final UseActionEntryWidget parent;
        private final PriorityManager.Entry action;
        private boolean highlighted = false;


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
            context.drawTextWrapped(
                    this.client.textRenderer, this.getEntryText(),
                    x + TEXTURE_WIDTH + HORIZONTAL_SPACER / 2, y + VERTICAL_SPACER * 2,
                    this.parent.getEntryTextWidth(), 0xFFFFFFFF
            );

            if(this.equals(this.parent.getSelectedOrNull())){
                this.drawMoveUpTexture(context, x, y, j, k);
                this.drawMoveDownTexture(context, x, y, j, k);

                j = x + entryWidth - mouseX;

                this.drawActiveTexture(context, x, y, j, k, entryWidth);
                this.drawLockedTexture(context, x, y, j, k, entryWidth);
                this.drawSwitchableTexture(context, x, y, j, k, entryWidth);
                this.drawLockTypeTexture(context, x, y, j, k, entryWidth);
            }
            else {
                boolean isSwitchable = false;
                boolean isActive = true;

                if(this.parent.isSwitchable()) {
                    if(this.action.isSwitchable()) {
                        context.drawGuiTexture(
                                SWITCHABLE_TEXTURE,
                                x + entryWidth - this.getSmallWidthWithSpacer() * 2, y,
                                SMALL_TEXTURE_WIDTH, SMALL_TEXTURE_HEIGHT
                        );

                        isSwitchable = true;
                    }
                }

                if(!this.parent.isActionPriorityDefault()) {
                    if(!this.action.isActive()) {
                        context.drawGuiTexture(
                                INACTIVE_TEXTURE,
                                x + entryWidth - this.getSmallWidthWithSpacer(), y,
                                SMALL_TEXTURE_WIDTH, SMALL_TEXTURE_HEIGHT
                        );

                        isActive = false;
                    }
                }
                else {
                    isActive = false;
                }

                if(this.action.isLocked() && (isActive || isSwitchable)) {
                    if(isLockTypeCorrectlySet(isActive, isSwitchable, this.action.getLockType())) {
                        context.drawGuiTexture(
                                LOCKED_TEXTURE,
                                x + entryWidth - this.getSmallWidthWithSpacer(), y + this.getSmallHeightWithSpacer(),
                                SMALL_TEXTURE_WIDTH, SMALL_TEXTURE_HEIGHT
                        );

                        if(isActive && isSwitchable) {
                            context.drawGuiTexture(
                                    this.getLockTypeTexture(),
                                    x + entryWidth - this.getSmallWidthWithSpacer() * 2, y + this.getSmallHeightWithSpacer(),
                                    SMALL_TEXTURE_WIDTH, SMALL_TEXTURE_HEIGHT
                            );
                        }
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

                if (d <= TEXTURE_WIDTH) {
                    if(this.canBeHigherInList(i)){
                        if (d > SMALL_TEXTURE_WIDTH && e < SMALL_TEXTURE_HEIGHT) {
                            this.parent.screen.getSelectedPriorityManager().moveUp(this.action);
                            this.parent.screen.reorderAllEntries();
                            this.reselectUseActionEntry();

                            return true;
                        }
                    }

                    if(this.canBeLowerInList(i)){
                        if (d > SMALL_TEXTURE_WIDTH && e > SMALL_TEXTURE_HEIGHT) {
                            this.parent.screen.getSelectedPriorityManager().moveDown(this.action);
                            this.parent.screen.reorderAllEntries();
                            this.reselectUseActionEntry();

                            return true;
                        }
                    }
                }
                else {
                    d = (double)this.parent.getRowRight() - mouseX;

                    if(d >= HORIZONTAL_SPACER && d <= this.getSmallWidthWithSpacer()) {
                        if(!this.parent.isActionPriorityDefault() && e <= SMALL_TEXTURE_HEIGHT) {
                            this.action.setActive(!this.action.isActive());

                            return true;
                        }
                        else if(e >= SMALL_TEXTURE_HEIGHT + VERTICAL_SPACER) {
                            this.action.setLocked(!this.action.isLocked());

                            return true;
                        }
                    }
                    else if(this.parent.isSwitchable()) {
                        if(d >= this.getSmallWidthWithSpacer() + HORIZONTAL_SPACER && d <= this.getSmallWidthWithSpacer() * 2) {
                            if(e <= SMALL_TEXTURE_HEIGHT) {
                                this.action.setSwitchable(!this.action.isSwitchable());

                                return true;
                            }
                            else if(!this.parent.isActionPriorityDefault() && e >= SMALL_TEXTURE_HEIGHT + VERTICAL_SPACER) {
                                this.action.setLockType(this.getNextLockType());

                                return true;
                            }
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

                if (j < TEXTURE_WIDTH / 2 && j > 0 && k < TEXTURE_HEIGHT && k > 0) {
                    context.drawTooltip(this.client.textRenderer, List.of(
                            Text.translatable(INVALID_ACTION_TOOLTIP_1),
                            Text.translatable(INVALID_ACTION_TOOLTIP_2)
                    ), x + j + this.client.textRenderer.getWidth(Text.translatable(INVALID_ACTION_TOOLTIP_1)), y + k + 28);
                    //context.drawTextWithBackground(this.client.textRenderer, text, x, y, this.client.textRenderer.getWidth(text), 0xFFFFFFFF);
                }
            }
        }

        private void drawMoveUpTexture(DrawContext context, int x, int y, int j, int k) {
            if(this.canBeHigherInList()){
                if (j < TEXTURE_WIDTH && j > TEXTURE_WIDTH / 2 && k < TEXTURE_HEIGHT / 2 && k > 0) {
                    context.drawGuiTexture(MOVE_UP_HIGHLIGHTED_TEXTURE, x, y, TEXTURE_WIDTH, TEXTURE_HEIGHT);
                }
                else {
                    context.drawGuiTexture(MOVE_UP_TEXTURE, x, y, TEXTURE_WIDTH, TEXTURE_HEIGHT);
                }
            }
        }

        private void drawMoveDownTexture(DrawContext context, int x, int y, int j, int k) {
            if(this.canBeLowerInList()){
                if (j < TEXTURE_WIDTH && j > TEXTURE_WIDTH / 2 && k > TEXTURE_HEIGHT / 2 && k < TEXTURE_HEIGHT) {
                    context.drawGuiTexture(MOVE_DOWN_HIGHLIGHTED_TEXTURE, x, y, TEXTURE_WIDTH, TEXTURE_HEIGHT);
                }
                else {
                    context.drawGuiTexture(MOVE_DOWN_TEXTURE, x, y, TEXTURE_WIDTH, TEXTURE_HEIGHT);
                }
            }
        }

        private void drawActiveTexture(DrawContext context, int x, int y, int j, int k, int entryWidth) {
            if(!this.parent.isActionPriorityDefault()) {
                if (j <= this.getSmallWidthWithSpacer() && j >= HORIZONTAL_SPACER && k <= SMALL_TEXTURE_HEIGHT && k > 0) {
                    context.drawGuiTexture(
                            this.action.isActive() ? ACTIVE_HIGHLIGHTED_TEXTURE : INACTIVE_HIGHLIGHTED_TEXTURE,
                            x + entryWidth - this.getSmallWidthWithSpacer(),
                            y,
                            SMALL_TEXTURE_WIDTH,
                            SMALL_TEXTURE_HEIGHT
                    );
                }
                else {
                    context.drawGuiTexture(
                            this.action.isActive() ? ACTIVE_TEXTURE : INACTIVE_TEXTURE,
                            x + entryWidth - this.getSmallWidthWithSpacer(),
                            y,
                            SMALL_TEXTURE_WIDTH,
                            SMALL_TEXTURE_HEIGHT
                    );
                }
            }
        }

        private void drawLockedTexture(DrawContext context, int x, int y, int j, int k, int entryWidth) {
            if (j <= this.getSmallWidthWithSpacer() && j >= HORIZONTAL_SPACER && k >= SMALL_TEXTURE_HEIGHT && k < SMALL_TEXTURE_HEIGHT * 2 + VERTICAL_SPACER) {
                context.drawGuiTexture(
                        this.action.isLocked() ? LOCKED_HIGHLIGHTED_TEXTURE : UNLOCKED_HIGHLIGHTED_TEXTURE,
                        x + entryWidth - this.getSmallWidthWithSpacer(),
                        y + this.getSmallHeightWithSpacer(),
                        SMALL_TEXTURE_WIDTH,
                        SMALL_TEXTURE_HEIGHT
                );
            }
            else {
                context.drawGuiTexture(
                        this.action.isLocked() ? LOCKED_TEXTURE : UNLOCKED_TEXTURE,
                        x + entryWidth - this.getSmallWidthWithSpacer(),
                        y + this.getSmallHeightWithSpacer(),
                        SMALL_TEXTURE_WIDTH,
                        SMALL_TEXTURE_HEIGHT
                );
            }
        }

        private void drawSwitchableTexture(DrawContext context, int x, int y, int j, int k, int entryWidth) {
            if(this.parent.isSwitchable()) {
                if (j <= this.getSmallWidthWithSpacer() * 2 && j >= this.getSmallWidthWithSpacer() + HORIZONTAL_SPACER && k <= SMALL_TEXTURE_HEIGHT && k > 0) {
                    context.drawGuiTexture(
                            this.action.isSwitchable() ? SWITCHABLE_HIGHLIGHTED_TEXTURE : NOT_SWITCHABLE_HIGHLIGHTED_TEXTURE,
                            x + entryWidth - this.getSmallWidthWithSpacer() * 2,
                            y,
                            SMALL_TEXTURE_WIDTH,
                            SMALL_TEXTURE_HEIGHT
                    );
                }
                else {
                    context.drawGuiTexture(
                            this.action.isSwitchable() ? SWITCHABLE_TEXTURE : NOT_SWITCHABLE_TEXTURE,
                            x + entryWidth - this.getSmallWidthWithSpacer() * 2,
                            y,
                            SMALL_TEXTURE_WIDTH,
                            SMALL_TEXTURE_HEIGHT
                    );
                }
            }
        }

        private void drawLockTypeTexture(DrawContext context, int x, int y, int j, int k, int entryWidth) {
            if(this.parent.isSwitchable() && !this.parent.isActionPriorityDefault()) {
                if (j <= this.getSmallWidthWithSpacer() * 2 && j >= this.getSmallWidthWithSpacer() + HORIZONTAL_SPACER && k >= SMALL_TEXTURE_HEIGHT && k < SMALL_TEXTURE_HEIGHT * 2 + VERTICAL_SPACER) {
                    context.drawGuiTexture(
                            this.getLockTypeHighlightedTexture(),
                            x + entryWidth - this.getSmallWidthWithSpacer() * 2,
                            y + this.getSmallHeightWithSpacer(),
                            SMALL_TEXTURE_WIDTH,
                            SMALL_TEXTURE_HEIGHT
                    );
                }
                else {
                    context.drawGuiTexture(
                            this.getLockTypeTexture(),
                            x + entryWidth - this.getSmallWidthWithSpacer() * 2,
                            y + this.getSmallHeightWithSpacer(),
                            SMALL_TEXTURE_WIDTH,
                            SMALL_TEXTURE_HEIGHT
                    );
                }
            }
        }

        private Text getEntryText() {
            return this.isHighlighted() ? Text.literal(this.action.getActionId().toString()).formatted(Formatting.GOLD) : Text.of(this.action.getActionId());
        }

        public boolean isTheSameAs(UseActionEntry entry) {
            if(this.equals(entry)) {
                return true;
            }

            return this.action.equals(entry.action);
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

        public PriorityManager.Entry getAction() {
            return this.action;
        }

        public boolean isHighlighted() {
            return this.highlighted;
        }

        public void setHighlighted(boolean highlighted) {
            this.highlighted = highlighted;
        }

        private void reselectUseActionEntry() {
            this.parent.children().stream().filter(this::isTheSameAs).findFirst().ifPresent(action -> {
                action.parent.setSelected(action);
            });
        }

        private int getSmallWidthWithSpacer() {
            return SMALL_TEXTURE_WIDTH + HORIZONTAL_SPACER;
        }

        private int getSmallHeightWithSpacer() {
            return SMALL_TEXTURE_HEIGHT + VERTICAL_SPACER;
        }

        private Identifier getLockTypeTexture() {
            return switch(this.action.getLockType()) {
                case ANY -> LOCK_TYPE_ANY_TEXTURE;
                case PRIORITY -> LOCK_TYPE_PRIORITY_TEXTURE;
                case SWITCH -> LOCK_TYPE_SWITCH_TEXTURE;
            };
        }

        private Identifier getLockTypeHighlightedTexture() {
            return switch(this.action.getLockType()) {
                case ANY -> LOCK_TYPE_ANY_HIGHLIGHTED_TEXTURE;
                case PRIORITY -> LOCK_TYPE_PRIORITY_HIGHLIGHTED_TEXTURE;
                case SWITCH -> LOCK_TYPE_SWITCH_HIGHLIGHTED_TEXTURE;
            };
        }

        private PriorityManager.LockType getNextLockType() {
            return switch(this.action.getLockType()) {
                case ANY -> PriorityManager.LockType.PRIORITY;
                case PRIORITY -> PriorityManager.LockType.SWITCH;
                case SWITCH -> PriorityManager.LockType.ANY;
            };
        }

        private static boolean isLockTypeCorrectlySet(boolean isActive, boolean isSwitchable, PriorityManager.LockType lockType) {
            return (isActive && isSwitchable) || !((isActive && lockType.equals(PriorityManager.LockType.SWITCH)) || (isSwitchable && lockType.equals(PriorityManager.LockType.PRIORITY)));
        }
    }
}
