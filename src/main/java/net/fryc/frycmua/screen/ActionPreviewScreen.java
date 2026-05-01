package net.fryc.frycmua.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fryc.frycmua.action.client.PriorityManager;
import net.fryc.frycmua.screen.widget.UseActionPreviewWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ActionPreviewScreen extends Screen {

    private static final int PADDING_BOTTOM = 28;
    private static final int PADDING_LEFT = 20;
    private static final int PADDING_RIGHT = 20;

    private final Screen parent;
    private final PriorityManager manager;
    private final ClientPlayerEntity player;
    private UseActionPreviewWidget leftWidget;
    private UseActionPreviewWidget rightWidget;

    public ActionPreviewScreen(Screen parent, @NotNull PriorityManager manager, @NotNull ClientPlayerEntity player) {
        super(Text.empty());
        this.parent = parent;
        this.manager = manager;
        this.player = player;
    }
// TODO dokonczyc ten ekran

    @Override
    protected void init() {
        this.leftWidget = new UseActionPreviewWidget(
                this.client,
                PADDING_LEFT, this.height / 5,
                this.width / 3, this.height / 2,
                this.manager.getOrderedAvailableUseActions(this.player.getMainHandStack(), this.player.getOffHandStack())
        );

        this.rightWidget = new UseActionPreviewWidget(
                this.client,
                this.width - this.leftWidget.getWidth() - PADDING_RIGHT, this.height / 5,
                this.width / 3, this.height / 2,
                this.manager.getSwitchableUseActions(this.player.getMainHandStack(), this.player.getOffHandStack())
        );

        this.addDrawableChild(this.leftWidget);
        this.addDrawableChild(this.rightWidget);
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

        context.drawText(
                this.textRenderer,
                Text.of("I'll finish this screen some other day"),
                10,
                10,
                0xFFFFFFFF,
                true
        );

        context.drawText(
                this.textRenderer,
                Text.translatable(ActionManagerScreen.ACTION_PRIORITY_TRANSLATION_KEY),
                this.leftWidget.getX(),
                this.leftWidget.getY() - 10,
                0xFFFFFFFF,
                true
        );

        context.drawText(
                this.textRenderer,
                Text.translatable(ActionManagerScreen.SWITCHABLE_TRANSLATION_KEY),
                this.rightWidget.getX(),
                this.rightWidget.getY() - 10,
                0xFFFFFFFF,
                true
        );
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
