package net.fryc.frycmua.item.action;

import net.minecraft.util.Identifier;

public interface SelectsUseAction {

    Identifier getSelectedUseAction();

    void setSelectedUseAction(Identifier id);
}
