package net.fryc.frycmua.item.action.registry;

import net.fryc.frycmua.item.action.DefaultUseActions;
import net.fryc.frycmua.item.action.ItemUseInstance;
import net.fryc.frycmua.item.action.ItemUseOnBlockInstance;
import net.fryc.frycmua.item.action.ItemUseOnEntityInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UseActionRegistries {

    public static final Map<Identifier, List<ItemUseInstance>> REGISTERED_USE_ACTIONS = new HashMap<>();
    public static final Map<Identifier, List<ItemUseOnBlockInstance>> REGISTERED_USE_ON_BLOCK_ACTIONS = new HashMap<>();
    public static final Map<Identifier, List<ItemUseOnEntityInstance>> REGISTERED_USE_ON_ENTITY_ACTIONS = new HashMap<>();

    /**
     * Adds {@link ItemUseInstance} to REGISTERED_USE_ACTIONS under selected id. Many use instances can be registered under the same id, but only one of the three maps can hold this id.
     *
     * @param id {@link Identifier} that has to be unique among the three maps (REGISTERED_USE_ACTIONS, REGISTERED_USE_ON_BLOCK_ACTIONS, REGISTERED_USE_ON_ENTITY_ACTIONS).
     * @param useInstance object holding {@link ItemUse} implementation.
     * @throws IllegalArgumentException when given id is present in 'REGISTERED_USE_ON_BLOCK_ACTIONS' or 'REGISTERED_USE_ON_ENTITY_ACTIONS'
     */
    public static void registerUseAction(Identifier id, ItemUseInstance useInstance) throws IllegalArgumentException {
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
     * @param useInstance object holding {@link ItemUseOnBlock} implementation.
     * @throws IllegalArgumentException when given id is present in 'REGISTERED_USE_ACTIONS' or 'REGISTERED_USE_ON_ENTITY_ACTIONS'
     */
    public static void registerUseOnBlockAction(Identifier id, ItemUseOnBlockInstance useInstance) throws IllegalArgumentException {
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
     * @param useInstance object holding {@link ItemUseOnEntity} implementation.
     * @throws IllegalArgumentException when given id is present in 'REGISTERED_USE_ACTIONS' or 'REGISTERED_USE_ON_BLOCK_ACTIONS'
     */
    public static void registerUseOnEntityAction(Identifier id, ItemUseOnEntityInstance useInstance) throws IllegalArgumentException {
        if(REGISTERED_USE_ACTIONS.containsKey(id) || REGISTERED_USE_ON_BLOCK_ACTIONS.containsKey(id)){
            throw new IllegalArgumentException("The following id: " + id.toString() + " cannot be used for use on entity action, because it is already used by " + getCollectionNameThatContainsId(id));
        }
        REGISTERED_USE_ON_ENTITY_ACTIONS.putIfAbsent(id, new ArrayList<>());
        REGISTERED_USE_ON_ENTITY_ACTIONS.get(id).add(useInstance);
    }

    private static String getCollectionNameThatContainsId(Identifier id){
        if(REGISTERED_USE_ACTIONS.containsKey(id)) return "REGISTERED_USE_ACTIONS";
        if(REGISTERED_USE_ON_BLOCK_ACTIONS.containsKey(id)) return "REGISTERED_USE_ON_BLOCK_ACTIONS";
        if(REGISTERED_USE_ON_ENTITY_ACTIONS.containsKey(id)) return "REGISTERED_USE_ON_ENTITY_ACTIONS";

        return "none";
    }


    @FunctionalInterface
    public interface ItemUse {
        TypedActionResult<ItemStack> useItem(Item item, World world, PlayerEntity user, Hand hand);
    }

    @FunctionalInterface
    public interface ItemUseOnBlock {
        ActionResult useItemOnBlock(ItemUsageContext context);
    }

    @FunctionalInterface
    public interface ItemUseOnEntity {
        ActionResult useItemOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand);
    }

    @FunctionalInterface
    public interface ItemFinishUsing {
        ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity user);
    }

    @FunctionalInterface
    public interface ItemOnStoppedUsing {
        void onStoppedUsingItem(ItemStack stack, World world, LivingEntity user, int remainingUseTicks);
    }

    @FunctionalInterface
    public interface ItemTickUsage {
        void itemUsageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks);
    }

    @FunctionalInterface
    public interface ItemMaxUseTime {
        int getMaxItemUseTime(ItemStack stack, LivingEntity user);
    }

    @FunctionalInterface
    public interface ItemUseAction {
        UseAction getItemUseAction(ItemStack stack);
    }
}
