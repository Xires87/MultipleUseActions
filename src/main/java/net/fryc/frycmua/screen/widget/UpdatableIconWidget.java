package net.fryc.frycmua.screen.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class UpdatableIconWidget extends ClickableWidget {

    public Identifier texture;

    public UpdatableIconWidget(int width, int height, Identifier texture) {
        super(0, 0, width, height, ScreenTexts.EMPTY);
        this.texture = texture;
    }

    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    public void playDownSound(SoundManager soundManager) {
    }

    public boolean isNarratable() {
        return false;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawGuiTexture(this.texture, this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    @Nullable
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        return null;
    }
}
