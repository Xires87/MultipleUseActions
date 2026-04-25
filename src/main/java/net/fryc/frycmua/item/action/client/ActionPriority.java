package net.fryc.frycmua.item.action.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fryc.frycmua.item.action.DefaultUseActions;
import net.fryc.frycmua.item.action.registry.UseActionRegistries;
import net.fryc.frycmua.util.client.UseActionHelperClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Environment(EnvType.CLIENT)
public enum ActionPriority {

    DEFAULT {
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

        @Override
        public String getTranslationKey() {
            return "text.frycmua.action_priority.default";
        }
    },
    MIXED {
        @Override
        public List<Identifier> getOrderedUseActions(ItemStack mainStack, ItemStack offStack, List<PriorityManager.Entry> managerActions) {
            Set<Identifier> set = UseActionHelperClient.getAllUseActionsForItems(mainStack, offStack);

            List<PriorityManager.Entry> list = managerActions.stream().filter(PriorityManager.Entry::isActive).filter(entry -> {
                return set.contains(entry.getActionId());
            }).toList();

            List<Identifier> finalList = new ArrayList<>();
            for(PriorityManager.Entry entry : list) {
                finalList.add(entry.getActionId());
                if(entry.isLocked() && entry.getLockType().isPriority()) break;
            }

            return List.copyOf(finalList);
        }

        @Override
        public List<Identifier> getAllPartiallyOrderedUseActions() {
            ArrayList<Identifier> list = new ArrayList<>();
            list.add(DefaultUseActions.USE_ON_ENTITY_DEFAULT);
            list.add(DefaultUseActions.USE_ON_BLOCK_DEFAULT);
            list.add(DefaultUseActions.USE_DEFAULT);
            list.addAll(UseActionRegistries.REGISTERED_USE_ON_ENTITY_ACTIONS.keySet());
            list.addAll(UseActionRegistries.REGISTERED_USE_ON_BLOCK_ACTIONS.keySet());
            list.addAll(UseActionRegistries.REGISTERED_USE_ACTIONS.keySet());

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

        @Override
        public String getTranslationKey() {
            return "text.frycmua.action_priority.mixed";
        }
    };

    public abstract List<Identifier> getOrderedUseActions(ItemStack mainStack, ItemStack offStack, List<PriorityManager.Entry> managerActions);

    public abstract List<Identifier> getAllPartiallyOrderedUseActions();

    public abstract List<PriorityManager.Entry> addOtherUseActions(List<PriorityManager.Entry> existingActions);

    public abstract String getTranslationKey();
}
