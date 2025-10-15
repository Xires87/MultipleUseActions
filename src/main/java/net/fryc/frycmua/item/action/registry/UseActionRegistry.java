package net.fryc.frycmua.item.action.registry;

import net.fryc.frycmua.item.action.ItemUseInstance;
import net.fryc.frycmua.item.action.ItemUseOnBlockInstance;
import net.fryc.frycmua.item.action.ItemUseOnEntityInstance;
import net.minecraft.util.Identifier;

public interface UseActionRegistry {

    void addUseAction(Identifier id, ItemUseInstance useInstance);
    void addUseOnBlockAction(Identifier id, ItemUseOnBlockInstance useInstance);
    void addUseOnEntityAction(Identifier id, ItemUseOnEntityInstance useInstance);
}
