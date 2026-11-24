package net.fryc.frycmua.util.mixin_interfaces.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;


// for CyclingButtonWidget and ClickableWidget
@Environment(EnvType.CLIENT)
public interface UpdatableWidget {

    void update();
    void setUpdateFunction(Runnable updateFunction);
}
