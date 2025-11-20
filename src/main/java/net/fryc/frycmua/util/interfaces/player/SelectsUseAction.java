package net.fryc.frycmua.util.interfaces.player;

import net.minecraft.util.Identifier;

// for PlayerEntity
public interface SelectsUseAction {

    Identifier getSelectedUseAction();

    void setSelectedUseAction(Identifier id);
}
