package net.fryc.frycmua.util.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fryc.frycmua.util.mixin_interfaces.item.HoldsUseActions;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class UseActionHelperClient {

    public static Set<Identifier> getAllUseActionsForItems(ItemStack mainStack, ItemStack offStack) {
        HashSet<Identifier> set = new HashSet<>();
        set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseActionIds());
        set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseActionIds());
        set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseOnEntityActionIds());
        set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseOnEntityActionIds());
        set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseOnBlockActionIds());
        set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseOnBlockActionIds());

        return Set.copyOf(set);
    }
}
