package net.fryc.frycmua.item.action;

import net.minecraft.util.Identifier;

public interface HoldsUseActions {

    ItemUseInstance getItemUseActionInstance(Identifier id);

    ItemUseOnBlockInstance getItemUseOnBlockActionInstance(Identifier id);

    ItemUseOnEntityInstance getItemUseOnEntityActionInstance(Identifier id);

    // TODO dodac tu te reszte z UseActionRegistries
}
