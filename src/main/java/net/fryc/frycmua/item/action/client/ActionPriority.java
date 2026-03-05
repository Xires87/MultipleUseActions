package net.fryc.frycmua.item.action.client;

import net.fryc.frycmua.item.action.DefaultUseActions;
import net.fryc.frycmua.item.action.registry.UseActionRegistries;
import net.fryc.frycmua.util.interfaces.item.HoldsUseActions;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public enum ActionPriority {
// TODO sortowanie jest teraz bez sensu musze to jakos inaczej zrobic (mozliwe ze nie bedzie sortowania)
    DEFAULT {
        // TODO nie bedzie use action default tylko ten typ wlasnie i on sie jak vanilla bedzie zachowywac
        @Override
        public List<Identifier> getOrderedUseActions(ItemStack mainStack, ItemStack offStack, List<PriorityManager.Entry> managerActions) {
            return List.of();
        }

        @Override
        public List<Identifier> getAllPartiallyOrderedUseActions() {
            return List.of();
        }

        @Override
        public List<PriorityManager.Entry> addOtherUseActions(List<PriorityManager.Entry> existingActions) {
            return existingActions;
        }
    },
    BLOCK_OR_ENTITY {
        @Override
        public List<Identifier> getOrderedUseActions(ItemStack mainStack, ItemStack offStack, List<PriorityManager.Entry> managerActions) {
            HashSet<Identifier> set = new HashSet<>();
            set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseOnEntityActionIds());
            set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseOnEntityActionIds());
            set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseOnBlockActionIds());
            set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseOnBlockActionIds());

            List<Identifier> list = new ArrayList<>(set);
            //list.sort(Comparator.comparingInt(UseActionPriorityHelper::getUseActionPriority).reversed());

            set.clear();
            set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseActionIds());
            set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseActionIds());
            set.removeIf(id -> {
                return id.toString().equals(DefaultUseActions.USE_DEFAULT.toString());
            });

            List<Identifier> list2 = new ArrayList<>(set);
            //list2.sort(Comparator.comparingInt(UseActionPriorityHelper::getUseActionPriority).reversed());

            list.addAll(list2);

            return List.copyOf(list);
        }

        @Override
        public List<Identifier> getAllPartiallyOrderedUseActions() {
            return List.of();
        }

        @Override
        public List<PriorityManager.Entry> addOtherUseActions(List<PriorityManager.Entry> existingActions) {
            return List.of();
        }
    },
    USE {
        @Override
        public List<Identifier> getOrderedUseActions(ItemStack mainStack, ItemStack offStack, List<PriorityManager.Entry> managerActions) {
            HashSet<Identifier> set = new HashSet<>();
            set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseActionIds());
            set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseActionIds());

            List<Identifier> list = new ArrayList<>(set);
            //list.sort(Comparator.comparingInt(UseActionPriorityHelper::getUseActionPriority).reversed());

            set.clear();
            set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseOnEntityActionIds());
            set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseOnEntityActionIds());
            set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseOnBlockActionIds());
            set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseOnBlockActionIds());
            set.removeIf(id -> {
                return id.toString().equals(DefaultUseActions.USE_DEFAULT.toString());
            });

            List<Identifier> list2 = new ArrayList<>(set);
            //list2.sort(Comparator.comparingInt(UseActionPriorityHelper::getUseActionPriority).reversed());

            list.addAll(list2);

            return List.copyOf(list);
        }

        @Override
        public List<Identifier> getAllPartiallyOrderedUseActions() {
            return List.of();
        }

        @Override
        public List<PriorityManager.Entry> addOtherUseActions(List<PriorityManager.Entry> existingActions) {
            return List.of();
        }
    },
    MIXED {
        @Override
        public List<Identifier> getOrderedUseActions(ItemStack mainStack, ItemStack offStack, List<PriorityManager.Entry> managerActions) {
            HashSet<Identifier> set = new HashSet<>();
            set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseActionIds());
            set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseActionIds());
            set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseOnEntityActionIds());
            set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseOnEntityActionIds());
            set.addAll(((HoldsUseActions) mainStack.getItem()).getAllUseOnBlockActionIds());
            set.addAll(((HoldsUseActions) offStack.getItem()).getAllUseOnBlockActionIds());

            //List<Identifier> list = new ArrayList<>(set);// TODO tutaj chyba inaczej bedzie z priority managerem
            //list.sort(Comparator.comparingInt(UseActionPriorityHelper::getUseActionPriority).reversed());
            List<Identifier> list = new ArrayList<>(
                    managerActions.stream().filter(PriorityManager.Entry::isActive).map(PriorityManager.Entry::getActionId).toList()
            ).stream().filter(set::contains).toList();


            return List.copyOf(list);
        }

        @Override
        public List<Identifier> getAllPartiallyOrderedUseActions() {
            ArrayList<Identifier> list = new ArrayList<>();
            list.add(DefaultUseActions.USE_DEFAULT);
            list.add(DefaultUseActions.USE_ON_ENTITY_DEFAULT);
            list.add(DefaultUseActions.USE_ON_BLOCK_DEFAULT);
            list.addAll(UseActionRegistries.REGISTERED_USE_ACTIONS.keySet());
            list.addAll(UseActionRegistries.REGISTERED_USE_ON_ENTITY_ACTIONS.keySet());
            list.addAll(UseActionRegistries.REGISTERED_USE_ON_BLOCK_ACTIONS.keySet());

            //list.sort(Comparator.comparingInt(UseActionPriorityHelper::getUseActionPriority).reversed());

            return List.copyOf(list);
        }

        @Override
        public List<PriorityManager.Entry> addOtherUseActions(List<PriorityManager.Entry> existingActions) {
            ArrayList<PriorityManager.Entry> newList = new ArrayList<>(existingActions);
            this.getAllPartiallyOrderedUseActions().stream().filter(id -> {
                return newList.stream().noneMatch(entry -> entry.getActionId().equals(id));
            }).forEach(id -> {
                newList.add(new PriorityManager.Entry(id, false));
            });

            return newList;
        }
    };

    public abstract List<Identifier> getOrderedUseActions(ItemStack mainStack, ItemStack offStack, List<PriorityManager.Entry> managerActions);

    public abstract List<Identifier> getAllPartiallyOrderedUseActions();

    public abstract List<PriorityManager.Entry> addOtherUseActions(List<PriorityManager.Entry> existingActions);
}
