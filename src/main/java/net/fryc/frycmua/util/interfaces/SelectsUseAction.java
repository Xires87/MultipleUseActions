package net.fryc.frycmua.util.interfaces;

import net.minecraft.util.Identifier;

public interface SelectsUseAction {

    Identifier getSelectedUseAction();

    void setSelectedUseAction(Identifier id);
}
