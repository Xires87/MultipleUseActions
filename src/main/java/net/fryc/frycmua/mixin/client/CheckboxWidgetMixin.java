package net.fryc.frycmua.mixin.client;

import net.fryc.frycmua.util.mixin_interfaces.widget.CheckboxWidgetCheckSetter;
import net.minecraft.client.gui.widget.CheckboxWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CheckboxWidget.class)
abstract class CheckboxWidgetMixin implements CheckboxWidgetCheckSetter {

    @Shadow
    private boolean checked;

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
