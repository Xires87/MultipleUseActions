package net.fryc.frycmua.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fryc.frycmua.util.client.FileHelper;
import net.fryc.frycmua.util.client.UseActionHelperClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ActionDemandScreen extends Screen {

    private static final Text TITLE_TEXT = Text.translatable("text.frycmua.action_demand");
    private static final Text BUTTON_QUICK_SETUP = Text.translatable("button.frycmua.quick_setup");
    private static final Text BUTTON_IGNORE = Text.translatable("button.frycmua.ignore");
    private static final Text BUTTON_LATER = Text.translatable("button.frycmua.later");

    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, 61, 33);

    protected final Screen parent;
    protected final Map<Identifier, List<String>> demandedActions;

    public ActionDemandScreen(Screen parent, Map<Identifier, List<String>> demandedActions) {
        super(TITLE_TEXT);
        this.parent = parent;
        this.demandedActions = demandedActions;
        if(this.demandedActions.isEmpty()) this.close();
    }

    @Override
    protected void init() {
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addHeader(DirectionalLayoutWidget.vertical().spacing(8));
        directionalLayoutWidget.add(new TextWidget(TITLE_TEXT, this.textRenderer), Positioner::alignHorizontalCenter);

        ScrollableTextWidget text = new ScrollableTextWidget(
                0,
                0,
                this.layout.getWidth() / 2,
                this.layout.getContentHeight() / 2,
                Text.literal(
                        Text.translatable("text.frycmua.some_actions_need_setup").getString() + "\n" +
                        Text.translatable("text.frycmua.list_of_actions_that_need_setup").getString() + "\n" +
                                this.getListOfActionsThatNeedSetup()
                ),
                this.textRenderer
        );

        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().marginX(4).marginBottom(4).alignHorizontalCenter();
        GridWidget.Adder adder = gridWidget.createAdder(3);

        ButtonWidget quickSetupButton = ButtonWidget.builder(BUTTON_QUICK_SETUP, (button) -> {
            this.client.setScreen(new QuickSetupScreen(this.parent, List.copyOf(this.demandedActions.keySet())));
        }).width(100).build();
        ButtonWidget ignoreButton = ButtonWidget.builder(BUTTON_IGNORE, (button) -> {
            FileHelper.saveIgnoredUseActions(List.copyOf(this.demandedActions.keySet()));
            this.close();
        }).width(100).build();
        ButtonWidget laterButton = ButtonWidget.builder(BUTTON_LATER, (button) -> {
            this.close();
        }).width(100).build();

        adder.add(quickSetupButton);
        adder.add(ignoreButton);
        adder.add(laterButton);

        this.layout.addBody(text);
        this.layout.addFooter(gridWidget);
        this.layout.forEachChild(this::addDrawableChild);
        this.initTabNavigation();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void initTabNavigation() {
        this.layout.refreshPositions();
    }

    @Override
    public void close() {
        if (this.parent != null && this.client != null) {
            this.client.setScreen(this.parent);
        }
        else {
            super.close();
        }
    }

    private String getListOfActionsThatNeedSetup() {
        StringBuilder builder = new StringBuilder();

        for(Identifier id : this.demandedActions.keySet()) {
            builder.append(id.toString());
            builder.append(" - required by: ");
            for(String modId : this.demandedActions.get(id)) {
                builder.append(modId);
                builder.append(", ");
            }
            builder.append("\n");
        }

        return builder.toString();
    }
}
