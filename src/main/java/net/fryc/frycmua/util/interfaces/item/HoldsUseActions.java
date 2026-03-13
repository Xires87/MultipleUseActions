package net.fryc.frycmua.util.interfaces.item;

import net.fryc.frycmua.item.action.UseInstance;
import net.minecraft.util.Identifier;

import java.util.Set;

// for Item
public interface HoldsUseActions {

    UseInstance getUseInstance(Identifier id);


    Set<Identifier> getAllUseActionIds();

    Set<Identifier> getAllUseOnBlockActionIds();

    Set<Identifier> getAllUseOnEntityActionIds();

}
