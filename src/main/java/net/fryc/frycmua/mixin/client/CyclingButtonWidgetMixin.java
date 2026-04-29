package net.fryc.frycmua.mixin.client;

import net.fryc.frycmua.util.mixin_interfaces.widget.CyclingButtonWidgetAdditionalUpdate;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CyclingButtonWidget.class)
abstract class CyclingButtonWidgetMixin implements CyclingButtonWidgetAdditionalUpdate {

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
