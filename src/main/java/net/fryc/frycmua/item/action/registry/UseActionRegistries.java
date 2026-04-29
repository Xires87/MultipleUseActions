package net.fryc.frycmua.item.action.registry;

import net.fryc.frycmua.item.action.ItemUseInstance;
import net.fryc.frycmua.item.action.ItemUseOnBlockInstance;
import net.fryc.frycmua.item.action.ItemUseOnEntityInstance;
import net.fryc.frycmua.item.action.UseInstance;
import net.fryc.frycmua.util.mixin_interfaces.registry.FrozenCheckable;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UseActionRegistries {

    private static final Map<Identifier, List<ItemUseInstance>> REGISTERED_USE_ACTIONS = new HashMap<>();
    private static final Map<Identifier, List<ItemUseOnBlockInstance>> REGISTERED_USE_ON_BLOCK_ACTIONS = new HashMap<>();
    private static final Map<Identifier, List<ItemUseOnEntityInstance>> REGISTERED_USE_ON_ENTITY_ACTIONS = new HashMap<>();

    private static Map<Identifier, List<ItemUseInstance>> CACHED_REGISTERED_USE_ACTIONS = Map.of();
    private static Map<Identifier, List<ItemUseOnBlockInstance>> CACHED_REGISTERED_USE_ON_BLOCK_ACTIONS = Map.of();
    private static Map<Identifier, List<ItemUseOnEntityInstance>> CACHED_REGISTERED_USE_ON_ENTITY_ACTIONS = Map.of();

    /**
     * Adds {@link ItemUseInstance} to REGISTERED_USE_ACTIONS under selected id. Many use instances can be registered under the same id, but only one of the three maps can hold this id.
     *
     * @param id {@link Identifier} that has to be unique among the three maps (REGISTERED_USE_ACTIONS, REGISTERED_USE_ON_BLOCK_ACTIONS, REGISTERED_USE_ON_ENTITY_ACTIONS).
     * @param useInstance object holding {@link ItemUseInstance.ItemUse} implementation.
     * @throws IllegalArgumentException when given id is present in 'REGISTERED_USE_ON_BLOCK_ACTIONS' or 'REGISTERED_USE_ON_ENTITY_ACTIONS'
     * @throws IllegalStateException when item registry is frozen
     */
    public static void registerUseAction(Identifier id, ItemUseInstance useInstance) throws IllegalArgumentException {
        throwIfItemRegistryFrozen();
        if(REGISTERED_USE_ON_BLOCK_ACTIONS.containsKey(id) || REGISTERED_USE_ON_ENTITY_ACTIONS.containsKey(id)){
            throw new IllegalArgumentException("The following id: " + id.toString() + " cannot be used for use action, because it is already used by " + getCollectionNameThatContainsId(id));
        }
        REGISTERED_USE_ACTIONS.putIfAbsent(id, new ArrayList<>());
        REGISTERED_USE_ACTIONS.get(id).add(useInstance);
    }

    /**
     * Adds {@link ItemUseOnBlockInstance} to REGISTERED_USE_ON_BLOCK_ACTIONS under selected id. Many use instances can be registered under the same id, but only one of the three maps can hold this id.
     *
     * @param id {@link Identifier} that has to be unique among the three maps (REGISTERED_USE_ACTIONS, REGISTERED_USE_ON_BLOCK_ACTIONS, REGISTERED_USE_ON_ENTITY_ACTIONS). Many use instances can be registered under the same id, but only one of the three maps can hold this id.
     * @param useInstance object holding {@link ItemUseOnBlockInstance.ItemUseOnBlock} implementation.
     * @throws IllegalArgumentException when given id is present in 'REGISTERED_USE_ACTIONS' or 'REGISTERED_USE_ON_ENTITY_ACTIONS'
     * @throws IllegalStateException when item registry is frozen
     */
    public static void registerUseOnBlockAction(Identifier id, ItemUseOnBlockInstance useInstance) throws IllegalArgumentException {
        throwIfItemRegistryFrozen();
        if(REGISTERED_USE_ACTIONS.containsKey(id) || REGISTERED_USE_ON_ENTITY_ACTIONS.containsKey(id)){
            throw new IllegalArgumentException("The following id: " + id.toString() + " cannot be used for use on block action, because it is already used by " + getCollectionNameThatContainsId(id));
        }
        REGISTERED_USE_ON_BLOCK_ACTIONS.putIfAbsent(id, new ArrayList<>());
        REGISTERED_USE_ON_BLOCK_ACTIONS.get(id).add(useInstance);
    }

    /**
     * Adds {@link ItemUseOnEntityInstance} to REGISTERED_USE_ON_ENTITY_ACTIONS under selected id. Many use instances can be registered under the same id, but only one of the three maps can hold this id.
     *
     * @param id {@link Identifier} that has to be unique among the three maps (REGISTERED_USE_ACTIONS, REGISTERED_USE_ON_BLOCK_ACTIONS, REGISTERED_USE_ON_ENTITY_ACTIONS). Many use instances can be registered under the same id, but only one of the three maps can hold this id.
     * @param useInstance object holding {@link ItemUseOnEntityInstance.ItemUseOnEntity} implementation.
     * @throws IllegalArgumentException when given id is present in 'REGISTERED_USE_ACTIONS' or 'REGISTERED_USE_ON_BLOCK_ACTIONS'
     * @throws IllegalStateException when item registry is frozen
     */
    public static void registerUseOnEntityAction(Identifier id, ItemUseOnEntityInstance useInstance) throws IllegalArgumentException {
        throwIfItemRegistryFrozen();
        if(REGISTERED_USE_ACTIONS.containsKey(id) || REGISTERED_USE_ON_BLOCK_ACTIONS.containsKey(id)){
            throw new IllegalArgumentException("The following id: " + id.toString() + " cannot be used for use on entity action, because it is already used by " + getCollectionNameThatContainsId(id));
        }
        REGISTERED_USE_ON_ENTITY_ACTIONS.putIfAbsent(id, new ArrayList<>());
        REGISTERED_USE_ON_ENTITY_ACTIONS.get(id).add(useInstance);
    }

    public static Map<Identifier, List<ItemUseInstance>> getRegisteredUseActions() {
        if(CACHED_REGISTERED_USE_ACTIONS.isEmpty()) {
            Map<Identifier, List<ItemUseInstance>> map = Map.copyOf(REGISTERED_USE_ACTIONS);
            tryToCacheMap(map);

            return map;
        }

        return CACHED_REGISTERED_USE_ACTIONS;
    }

    public static Map<Identifier, List<ItemUseOnBlockInstance>> getRegisteredUseOnBlockActions() {
        if(CACHED_REGISTERED_USE_ON_BLOCK_ACTIONS.isEmpty()) {
            Map<Identifier, List<ItemUseOnBlockInstance>> map = Map.copyOf(REGISTERED_USE_ON_BLOCK_ACTIONS);
            tryToCacheMap(map);

            return map;
        }

        return CACHED_REGISTERED_USE_ON_BLOCK_ACTIONS;
    }

    public static Map<Identifier, List<ItemUseOnEntityInstance>> getRegisteredUseOnEntityActions() {
        if(CACHED_REGISTERED_USE_ON_ENTITY_ACTIONS.isEmpty()) {
            Map<Identifier, List<ItemUseOnEntityInstance>> map = Map.copyOf(REGISTERED_USE_ON_ENTITY_ACTIONS);
            tryToCacheMap(map);

            return map;
        }

        return CACHED_REGISTERED_USE_ON_ENTITY_ACTIONS;
    }

    private static void throwIfItemRegistryFrozen() {
        ((FrozenCheckable) Registries.ITEM).throwIfFrozen();
    }

    private static void tryToCacheMap(Map<Identifier, ? extends List<? extends UseInstance>> map) {
        if(((FrozenCheckable) Registries.ITEM).isAlreadyFrozen()) {
            map.values().stream().filter(list -> !list.isEmpty()).findAny().ifPresent(list -> {
                UseInstance instance = list.stream().findFirst().get();
                switch (instance) {
                    case ItemUseInstance ignored ->
                            CACHED_REGISTERED_USE_ACTIONS = (Map<Identifier, List<ItemUseInstance>>) map;
                    case ItemUseOnBlockInstance ignored ->
                            CACHED_REGISTERED_USE_ON_BLOCK_ACTIONS = (Map<Identifier, List<ItemUseOnBlockInstance>>) map;
                    case ItemUseOnEntityInstance ignored ->
                            CACHED_REGISTERED_USE_ON_ENTITY_ACTIONS = (Map<Identifier, List<ItemUseOnEntityInstance>>) map;
                    default -> {}
                }
            });
        }
    }

    private static String getCollectionNameThatContainsId(Identifier id){
        if(REGISTERED_USE_ACTIONS.containsKey(id)) return "REGISTERED_USE_ACTIONS";
        if(REGISTERED_USE_ON_BLOCK_ACTIONS.containsKey(id)) return "REGISTERED_USE_ON_BLOCK_ACTIONS";
        if(REGISTERED_USE_ON_ENTITY_ACTIONS.containsKey(id)) return "REGISTERED_USE_ON_ENTITY_ACTIONS";

        return "none";
    }
}
