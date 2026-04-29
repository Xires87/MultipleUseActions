package net.fryc.frycmua.util.mixin_interfaces.item;

import net.fryc.frycmua.action.ItemUseInstance;
import net.fryc.frycmua.action.ItemUseOnBlockInstance;
import net.fryc.frycmua.action.ItemUseOnEntityInstance;
import net.minecraft.util.Identifier;

// for Item
public interface UseActionRegistry {

    void addUseAction(Identifier id, ItemUseInstance useInstance);
    void addUseOnBlockAction(Identifier id, ItemUseOnBlockInstance useInstance);
    void addUseOnEntityAction(Identifier id, ItemUseOnEntityInstance useInstance);
}
