package net.fryc.frycmua.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ActionDemandScreen extends Screen {

    protected final Screen parent;

    public ActionDemandScreen(Screen parent) {
        super(Text.empty());
        this.parent = parent;
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
}
