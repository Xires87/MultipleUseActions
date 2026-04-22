package net.fryc.frycmua.screen.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

@Environment(EnvType.CLIENT)
public class UseActionPreviewWidget extends ScrollableWidget {

    private final List<Identifier> actions;
    private final MinecraftClient client;

    private int contentHeight;


    public UseActionPreviewWidget(MinecraftClient client, int x, int y, int width, int height, List<Identifier> actions) {
        super(x, y, width, height, Text.empty());
        this.client = client;
        this.actions = actions;
        this.contentHeight = height;
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        int x = this.getX() + this.getPaddingDoubled();
        this.contentHeight = this.getY() + this.getPaddingDoubled();
        for(Identifier id : this.actions) {
            context.drawGuiTexture(id, x, this.contentHeight, this.getItemWidth(), this.getItemHeight());
            context.drawText(this.client.textRenderer, Text.of(id), x + this.getItemWidth() + this.getPaddingDoubled(), this.contentHeight + this.getPadding() / 2, 0xFFFFFFFF, true);

            this.contentHeight += this.getItemHeight() + this.getPadding();
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    protected int getContentsHeight() {
        return this.contentHeight;
    }

    protected int getItemWidth() {
        return 16;
    }

    protected int getItemHeight() {
        return 16;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 2;
    }
}
