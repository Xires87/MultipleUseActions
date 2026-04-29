package net.fryc.frycmua.util.mixin_interfaces.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;


// for CyclingButtonWidget
@Environment(EnvType.CLIENT)
public interface CyclingButtonWidgetAdditionalUpdate {

    void update();
    void setUpdateFunction(Runnable updateFunction);
}
