package net.fryc.frycmua.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fryc.frycmua.MultipleUseActions;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ConfirmWithNumberScreen extends ConfirmScreen {

    public static final Identifier ICON_ADD = Identifier.of(MultipleUseActions.MOD_ID, "icon/add");
    public static final Identifier ICON_REMOVE = Identifier.of(MultipleUseActions.MOD_ID, "icon/delete");

    public static final String INCREASE_NUMBER_TRANSLATION_KEY = "button.frycmua.increase_number";
    public static final String DECREASE_NUMBER_TRANSLATION_KEY = "button.frycmua.decrease_number";

    private int number = 1;
    private int numberY = 0;

    public ConfirmWithNumberScreen(BooleanConsumer callback, Text title, Text message) {
        super(callback, title, message);
    }

    @Override
    protected void addButtons(int y) {
        TextIconButtonWidget buttonMinus = TextIconButtonWidget.builder(Text.translatable(INCREASE_NUMBER_TRANSLATION_KEY), btn -> {
            this.decreaseNumber();
        }, true).width(20).texture(ICON_REMOVE, 15, 15).build();
        buttonMinus.setPosition(this.width / 2 - 30, y);
        this.addButton(buttonMinus);

        TextIconButtonWidget buttonPlus = TextIconButtonWidget.builder(Text.translatable(DECREASE_NUMBER_TRANSLATION_KEY), btn -> {
            this.increaseNumber();
        }, true).width(20).texture(ICON_ADD, 15, 15).build();
        buttonPlus.setPosition(this.width / 2 + 9, y);
        this.addButton(buttonPlus);

        this.numberY = y + 7;

        super.addButtons(y + 30);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, Text.of(String.valueOf(this.number)), this.width / 2, this.numberY, 16777215);
    }

    private void increaseNumber() {
        if(this.number < 10) {
            this.number++;
        }
    }

    private void decreaseNumber() {
        if(this.number > 1) {
            this.number--;
        }
    }

    public int getNumber() {
        return this.number;
    }
}
