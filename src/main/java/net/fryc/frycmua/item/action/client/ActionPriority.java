package net.fryc.frycmua.item.action.client;

import net.fryc.frycmua.item.action.DefaultUseActions;
import net.fryc.frycmua.util.client.UseActionPriorityHelper;
import net.fryc.frycmua.util.interfaces.item.HoldsUseActions;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public enum ActionPriority {

    DEFAULT {
        @Override
        List<Identifier> getOrderedUseActions(ItemStack mainStack, ItemStack offStack) {
            return List.of();
        }
    },
    BLOCK_OR_ENTITY {
        @Override
        List<Identifier> getOrderedUseActions(ItemStack mainStack, ItemStack offStack) {
            HashSet<Identifier> set = new HashSet<>();
            set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseOnEntityActionIds());
            set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseOnEntityActionIds());
            set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseOnBlockActionIds());
            set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseOnBlockActionIds());

            List<Identifier> list = new ArrayList<>(set);
            list.sort(Comparator.comparingInt(UseActionPriorityHelper::getUseActionPriority).reversed());

            set.clear();
            set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseActionIds());
            set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseActionIds());
            set.removeIf(id -> {
                return id.toString().equals(DefaultUseActions.USE_DEFAULT.toString());
            });

            List<Identifier> list2 = new ArrayList<>(set);
            list2.sort(Comparator.comparingInt(UseActionPriorityHelper::getUseActionPriority).reversed());

            list.addAll(list2);

            return List.copyOf(list);
        }
    },
    USE {
        @Override
        List<Identifier> getOrderedUseActions(ItemStack mainStack, ItemStack offStack) {
            HashSet<Identifier> set = new HashSet<>();
            set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseActionIds());
            set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseActionIds());

            List<Identifier> list = new ArrayList<>(set);
            list.sort(Comparator.comparingInt(UseActionPriorityHelper::getUseActionPriority).reversed());

            set.clear();
            set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseOnEntityActionIds());
            set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseOnEntityActionIds());
            set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseOnBlockActionIds());
            set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseOnBlockActionIds());
            set.removeIf(id -> {
                return id.toString().equals(DefaultUseActions.USE_DEFAULT.toString());
            });

            List<Identifier> list2 = new ArrayList<>(set);
            list2.sort(Comparator.comparingInt(UseActionPriorityHelper::getUseActionPriority).reversed());

            list.addAll(list2);

            return List.copyOf(list);
        }
    },
    MIXED {
        @Override
        List<Identifier> getOrderedUseActions(ItemStack mainStack, ItemStack offStack) {
            HashSet<Identifier> set = new HashSet<>();
            set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseActionIds());
            set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseActionIds());
            set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseOnEntityActionIds());
            set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseOnEntityActionIds());
            set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseOnBlockActionIds());
            set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseOnBlockActionIds());

            List<Identifier> list = new ArrayList<>(set);
            list.sort(Comparator.comparingInt(UseActionPriorityHelper::getUseActionPriority).reversed());

            return List.copyOf(list);
        }
    };

    abstract List<Identifier> getOrderedUseActions(ItemStack mainStack, ItemStack offStack);
}
