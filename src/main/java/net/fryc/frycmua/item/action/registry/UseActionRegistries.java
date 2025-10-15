package net.fryc.frycmua.item.action.registry;

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

    public static void registerUseAction(Identifier id, ItemUseInstance useInstance) {
        REGISTERED_USE_ACTIONS.putIfAbsent(id, new ArrayList<>());
        REGISTERED_USE_ACTIONS.get(id).add(useInstance);
    }

    public static void registerUseOnBlockAction(Identifier id, ItemUseOnBlockInstance useInstance) {
        REGISTERED_USE_ON_BLOCK_ACTIONS.putIfAbsent(id, new ArrayList<>());
        REGISTERED_USE_ON_BLOCK_ACTIONS.get(id).add(useInstance);
    }

    public static void registerUseOnEntityAction(Identifier id, ItemUseOnEntityInstance useInstance) {
        REGISTERED_USE_ON_ENTITY_ACTIONS.putIfAbsent(id, new ArrayList<>());
        REGISTERED_USE_ON_ENTITY_ACTIONS.get(id).add(useInstance);
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
