package net.fryc.frycmua.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fryc.frycmua.MultipleUseActions;
import net.fryc.frycmua.action.DefaultUseActions;
import net.fryc.frycmua.action.client.ActionPriority;
import net.fryc.frycmua.action.client.PriorityManager;
import net.fryc.frycmua.keybind.ModKeybinds;
import net.fryc.frycmua.screen.widget.UpdatableIconWidget;
import net.fryc.frycmua.util.client.UseActionHelperClient;
import net.fryc.frycmua.util.mixin_interfaces.widget.CheckboxWidgetCheckSetter;
import net.fryc.frycmua.util.mixin_interfaces.widget.UpdatableWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class QuickSetupScreen extends Screen {

    private static final Identifier NULL_ID = Identifier.of(MultipleUseActions.MOD_ID, "null");

    private static final Text TITLE_TEXT = Text.translatable("text.frycmua.quick_setup");
    private static final Text NOTHING_TO_SHOW_TEXT = Text.translatable("text.frycmua.nothing_to_show");
    private static final Text OPTION_PRIORITY_HIGH = Text.translatable("text.frycmua.high_priority");
    private static final Text OPTION_PRIORITY_LOW = Text.translatable("text.frycmua.low_priority");
    private static final Text OPTION_PRIORITY_SWITCH = Text.translatable("text.frycmua.switch");
    private static final Text OPTION_PRIORITY_SWITCH_STRICT = Text.translatable("text.frycmua.switch_strict");
    private static final Text NAVIGATE_LEFT_TRANSLATION_KEY = Text.translatable("button.frycmua.navigate_left");
    private static final Text NAVIGATE_RIGHT_TRANSLATION_KEY = Text.translatable("button.frycmua.navigate_right");
    private static final Text CONFIRM_BUTTON = Text.translatable("text.frycmua.confirm");
    private static final Text SKIP_BUTTON = Text.translatable("text.frycmua.skip");

    private static final Identifier ICON_NAVIGATE_LEFT = Identifier.of(MultipleUseActions.MOD_ID, "icon/navigate_left");
    private static final Identifier ICON_NAVIGATE_RIGHT = Identifier.of(MultipleUseActions.MOD_ID, "icon/navigate_right");

    private static final List<KeyBinding> AVAILABLE_KEYBINDS = getAvailableKeybinds();

    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, 61, 33);
    private final List<ClickableWidget> keybindChoiceWidgets = new ArrayList<>();
    private final List<ClickableWidget> actionInfoWidgets = new ArrayList<>();

    private SetupOption setupOption = SetupOption.HIGH;
    private KeyBinding chosenKeybind = AVAILABLE_KEYBINDS.getFirst();
    private Identifier chosenAction = NULL_ID;
    private boolean isModifiyngKeybind = false;
    private ButtonWidget closeButton;
    private TextWidget titleText;

    protected final Screen parent;
    private final Iterator<Identifier> actionsToSet;

    public QuickSetupScreen(Screen parent, List<Identifier> actionsToSet) {
        super(TITLE_TEXT);
        this.parent = parent;
        this.actionsToSet = actionsToSet.iterator();
        if(this.actionsToSet.hasNext()) this.chosenAction = this.actionsToSet.next();
    }

    public QuickSetupScreen(Screen parent, List<Identifier> actionsToSet, KeyBinding chosenKeybind) {
        this(parent, actionsToSet);
        this.chosenKeybind = chosenKeybind;
    }

    @Override
    protected void init() {
        this.titleText = new TextWidget(TITLE_TEXT, this.textRenderer);

        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addHeader(DirectionalLayoutWidget.vertical().spacing(8));
        directionalLayoutWidget.add(this.titleText, Positioner::alignHorizontalCenter);

        if(this.chosenAction != NULL_ID) {
            GridWidget gridWidget = new GridWidget();
            gridWidget.getMainPositioner().marginX(4).marginBottom(16).alignHorizontalCenter();
            GridWidget.Adder adder = gridWidget.createAdder(4);

            int width = this.layout.getWidth() / 4;

            this.createActionInfoRow(adder, width);
            this.createKeybindChoiceRow(adder, width);
            this.createSetupOptionRow(adder, width);
            this.createConfirmAndSkipButtons(adder, width);

            this.layout.addBody(gridWidget);
        }

        this.closeButton = ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.close()).width(200).build();

        this.layout.addFooter(this.closeButton);
        this.layout.forEachChild(this::addDrawableChild);
        this.initTabNavigation();
    }


    protected void initTabNavigation() {
        this.layout.refreshPositions();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if(this.chosenAction == NULL_ID) {
            context.drawText(this.textRenderer, NOTHING_TO_SHOW_TEXT, this.width / 2 - this.textRenderer.getWidth(NOTHING_TO_SHOW_TEXT) / 2, this.height / 3, 0xFFFFFFFF, true);
        }
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isModifiyngKeybind && this.client != null) {
            this.client.options.setKeyCode(this.chosenKeybind, InputUtil.Type.MOUSE.createFromCode(button));
            this.isModifiyngKeybind = false;
            this.updateKeybindChoiceRow();

            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.isModifiyngKeybind && this.client != null) {
            if (keyCode == 256) {
                this.client.options.setKeyCode(this.chosenKeybind, InputUtil.UNKNOWN_KEY);
            } else {
                this.client.options.setKeyCode(this.chosenKeybind, InputUtil.fromKeyCode(keyCode, scanCode));
            }

            this.isModifiyngKeybind = false;
            this.updateKeybindChoiceRow();

            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    private void createSetupOptionRow(GridWidget.Adder adder, int width) {
        ArrayList<CheckboxWidget> prioritySelection = new ArrayList<>();
        adder.add(this.createSingleChoiceCheckbox(OPTION_PRIORITY_HIGH, prioritySelection, SetupOption.HIGH));
        adder.add(this.createSingleChoiceCheckbox(OPTION_PRIORITY_LOW, prioritySelection, SetupOption.LOW));
        adder.add(this.createSingleChoiceCheckbox(OPTION_PRIORITY_SWITCH, prioritySelection, SetupOption.SWITCH));
        adder.add(this.createSingleChoiceCheckbox(OPTION_PRIORITY_SWITCH_STRICT, prioritySelection, SetupOption.SWITCH_STRICT));
    }

    private void createActionInfoRow(GridWidget.Adder adder, int width) {
        UpdatableIconWidget icon = new UpdatableIconWidget(32, 32, this.chosenAction);
        ((UpdatableWidget) icon).setUpdateFunction(() -> {
            icon.texture = this.chosenAction;
        });

        TextWidget text = new TextWidget(width, 32, Text.of(this.chosenAction), this.textRenderer);
        ((UpdatableWidget) text).setUpdateFunction(() -> {
            text.setMessage(Text.of(this.chosenAction));
        });

        adder.add(new EmptyWidget(width, 48));
        adder.add(icon);
        adder.add(text);
        adder.add(new EmptyWidget(width, 48));

        this.actionInfoWidgets.add(icon);
        this.actionInfoWidgets.add(text);
    }

    private void updateActionInfoRow() {
        this.actionInfoWidgets.forEach(clickableWidget -> ((UpdatableWidget) clickableWidget).update());
    }

    private void createKeybindChoiceRow(GridWidget.Adder adder, int width) {
        TextIconButtonWidget navLeftButton = TextIconButtonWidget.builder(NAVIGATE_LEFT_TRANSLATION_KEY, btn -> {
            int i = AVAILABLE_KEYBINDS.indexOf(this.chosenKeybind);
            if(i > 0) {
                this.chosenKeybind = AVAILABLE_KEYBINDS.get(i - 1);
                this.updateKeybindChoiceRow();
            }
        }, true).width(20).texture(ICON_NAVIGATE_LEFT, 15, 15).build();
        ((UpdatableWidget) navLeftButton).setUpdateFunction(() -> {
            navLeftButton.active = AVAILABLE_KEYBINDS.indexOf(this.chosenKeybind) > 0;
        });

        TextIconButtonWidget navRightButton = TextIconButtonWidget.builder(NAVIGATE_RIGHT_TRANSLATION_KEY, btn -> {
            int i = AVAILABLE_KEYBINDS.indexOf(this.chosenKeybind);
            if(i < AVAILABLE_KEYBINDS.size() - 1) {
                this.chosenKeybind = AVAILABLE_KEYBINDS.get(i + 1);
                this.updateKeybindChoiceRow();
            }
        }, true).width(20).texture(ICON_NAVIGATE_RIGHT, 15, 15).build();
        ((UpdatableWidget) navRightButton).setUpdateFunction(() -> {
            navRightButton.active = AVAILABLE_KEYBINDS.indexOf(this.chosenKeybind) < AVAILABLE_KEYBINDS.size() - 1;
        });

        TextWidget keybindInfo = new TextWidget(width, 20, Text.translatable(this.chosenKeybind.getTranslationKey()), this.textRenderer);
        ((UpdatableWidget) keybindInfo).setUpdateFunction(() -> {
            keybindInfo.setMessage(Text.translatable(this.chosenKeybind.getTranslationKey()));
        });

        ButtonWidget changeBindingButton = ButtonWidget.builder(this.chosenKeybind.getBoundKeyLocalizedText(), (button) -> {
            button.setMessage(Text.literal("> ").append(this.chosenKeybind.getBoundKeyLocalizedText().copy().formatted(Formatting.WHITE)).append(" <").formatted(Formatting.YELLOW));
            KeyBinding.updateKeysByCode();
            this.isModifiyngKeybind = true;
        }).dimensions(0, 0, 75, 20).build();
        ((UpdatableWidget) changeBindingButton).setUpdateFunction(() -> {
            if (this.client != null) {
                if(this.chosenKeybind.isUnbound() || Arrays.stream(this.client.options.allKeys).noneMatch(key -> {
                    return key != this.chosenKeybind && this.chosenKeybind.equals(key);
                })) {
                    changeBindingButton.setMessage(this.chosenKeybind.getBoundKeyLocalizedText());
                }
                else {
                    changeBindingButton.setMessage(Text.literal("[ ").append(this.chosenKeybind.getBoundKeyLocalizedText().copy().formatted(Formatting.WHITE)).append(" ]").formatted(Formatting.RED));
                }
            }
        });

        adder.add(navLeftButton);
        adder.add(keybindInfo);
        adder.add(changeBindingButton);
        adder.add(navRightButton);

        this.keybindChoiceWidgets.add(navLeftButton);
        this.keybindChoiceWidgets.add(navRightButton);
        this.keybindChoiceWidgets.add(keybindInfo);
        this.keybindChoiceWidgets.add(changeBindingButton);
        this.updateKeybindChoiceRow();
    }

    private void updateKeybindChoiceRow() {
        this.keybindChoiceWidgets.forEach(widget -> ((UpdatableWidget) widget).update());
    }

    private CheckboxWidget createSingleChoiceCheckbox(Text text, List<CheckboxWidget> group, SetupOption option) {
        CheckboxWidget widget = CheckboxWidget.builder(text, this.textRenderer).checked(group.isEmpty()).callback((checkbox, checked) -> {
            group.stream().filter(box -> !box.equals(checkbox)).forEach(box -> {
                ((CheckboxWidgetCheckSetter) box).setChecked(false);
            });

            if(!checkbox.isChecked()) {
                ((CheckboxWidgetCheckSetter) checkbox).setChecked(true);
            }

            this.setupOption = option;
        }).build();
        group.add(widget);

        return widget;
    }

    private void createConfirmAndSkipButtons(GridWidget.Adder adder, int width) {
        ButtonWidget confirm = ButtonWidget.builder(CONFIRM_BUTTON, button -> {
            PriorityManager manager = UseActionHelperClient.getPriorityManager(this.chosenKeybind);
            PriorityManager.Entry entry = new PriorityManager.Entry(
                    this.chosenAction,
                    this.setupOption == SetupOption.HIGH || this.setupOption == SetupOption.LOW,
                    this.setupOption == SetupOption.SWITCH_STRICT,
                    this.setupOption == SetupOption.SWITCH || this.setupOption == SetupOption.SWITCH_STRICT
            );

            this.setupOption.fixPriorityManager(manager);
            manager.addUseAction(entry);
            while(this.setupOption.move(manager, entry));

            this.updateChosenAction();
        }).build();

        ButtonWidget skip = ButtonWidget.builder(SKIP_BUTTON, button -> this.updateChosenAction()).build();

        adder.add(new EmptyWidget(width, 20));
        adder.add(skip);
        adder.add(confirm);
        adder.add(new EmptyWidget(width, 20));
    }

    private void updateChosenAction() {
        if(this.actionsToSet.hasNext()) {
            this.chosenAction = this.actionsToSet.next();
            this.updateActionInfoRow();
        }
        else {
            this.chosenAction = NULL_ID;
            this.layout.forEachChild(child -> {
                child.visible = false;
            });

            this.titleText.visible = true;
            this.closeButton.visible = true;
        }
    }

    private static List<KeyBinding> getAvailableKeybinds() {
        ArrayList<KeyBinding> list = new ArrayList<>(ModKeybinds.MUA_ADDITIONAL_USE_KEYS.keySet());
        list.add(MinecraftClient.getInstance().options.useKey);

        return List.copyOf(list);
    }

    private enum SetupOption {
        HIGH {
            @Override
            public boolean move(PriorityManager manager, PriorityManager.Entry entry) {
                if(manager.canMoveUp(entry)){
                    manager.moveUp(entry);

                    return true;
                }

                return false;
            }

            @Override
            public void fixPriorityManager(PriorityManager manager) {
                if(manager.getActionPriority().equals(ActionPriority.DEFAULT)) {
                    manager.setActionPriority(ActionPriority.MIXED);
                    manager.addUseAction(new PriorityManager.Entry(DefaultUseActions.USE_DEFAULT, true));
                    manager.addUseAction(new PriorityManager.Entry(DefaultUseActions.USE_ON_BLOCK_DEFAULT, true));
                    manager.addUseAction(new PriorityManager.Entry(DefaultUseActions.USE_ON_ENTITY_DEFAULT, true));
                }
            }
        },
        LOW {
            @Override
            public boolean move(PriorityManager manager, PriorityManager.Entry entry) {
                if(manager.canMoveDown(entry)){
                    manager.moveDown(entry);

                    return true;
                }

                return false;
            }

            @Override
            public void fixPriorityManager(PriorityManager manager) {
                if(manager.getActionPriority().equals(ActionPriority.DEFAULT)) {
                    manager.setActionPriority(ActionPriority.MIXED);
                    manager.addUseAction(new PriorityManager.Entry(DefaultUseActions.USE_DEFAULT, true));
                    manager.addUseAction(new PriorityManager.Entry(DefaultUseActions.USE_ON_BLOCK_DEFAULT, true));
                    manager.addUseAction(new PriorityManager.Entry(DefaultUseActions.USE_ON_ENTITY_DEFAULT, true));
                }
            }
        },
        SWITCH {
            @Override
            public boolean move(PriorityManager manager, PriorityManager.Entry entry) {
                return false;
            }

            @Override
            public void fixPriorityManager(PriorityManager manager) {
                manager.setSwitchable(true);
            }
        },
        SWITCH_STRICT {
            @Override
            public boolean move(PriorityManager manager, PriorityManager.Entry entry) {
                return false;
            }

            @Override
            public void fixPriorityManager(PriorityManager manager) {
                manager.setSwitchable(true);
            }
        };

        public abstract boolean move(PriorityManager manager, PriorityManager.Entry entry);

        public abstract void fixPriorityManager(PriorityManager manager);
    }
}
