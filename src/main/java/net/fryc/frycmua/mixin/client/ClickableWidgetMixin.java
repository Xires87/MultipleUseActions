package net.fryc.frycmua.mixin.client;

import net.fryc.frycmua.util.mixin_interfaces.widget.UpdatableWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClickableWidget.class)
abstract class ClickableWidgetMixin implements UpdatableWidget {

    @Unique
    private Runnable updateValue = () -> {};


    @Override
    public void update() {
        this.updateValue.run();
    }

    @Override
    public void setUpdateFunction(Runnable updateFunction) {
        this.updateValue = updateFunction;
    }
}
