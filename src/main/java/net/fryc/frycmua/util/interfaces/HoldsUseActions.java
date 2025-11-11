package net.fryc.frycmua.util.interfaces;

import net.fryc.frycmua.item.action.UseInstance;
import net.minecraft.util.Identifier;

import java.util.Set;

public interface HoldsUseActions {

    UseInstance getUseInstance(Identifier id);


    Set<Identifier> getAllUseActionIds();

    Set<Identifier> getAllUseOnBlockActionIds();

    Set<Identifier> getAllUseOnEntityActionIds();

    // TODO dodac tu te reszte z UseActionRegistries
}
