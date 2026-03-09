package net.fryc.frycmua.item.action;

import net.fryc.frycmua.util.function.QuadPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class ItemUseOnEntityInstance extends UseInstance {


    private final ItemUseOnEntity useOnEntity;
    private final QuadPredicate<PlayerEntity, Entity, EntityHitResult, Hand> available;

    public ItemUseOnEntityInstance(
            Class<?> classLevel,
            boolean allowSubclasses,
            ItemUseOnEntity useOnEntity,
            ItemTickUsage tickUsage,
            ItemOnStoppedUsing onStoppedUsing,
            ItemFinishUsing finishUsing,
            ItemMaxUseTime maxUseTime,
            ItemUseAction useAction,
            ItemUseOnRelease useOnRelease,
            QuadPredicate<PlayerEntity, Entity, EntityHitResult, Hand> available
    ) {
        super(classLevel, allowSubclasses, tickUsage, onStoppedUsing, finishUsing, maxUseTime, useAction, useOnRelease);
        this.useOnEntity = useOnEntity;
        this.available = available;
    }


    public ItemUseOnEntity getUseOnEntity() {
        return this.useOnEntity;
    }

    public QuadPredicate<PlayerEntity, Entity, EntityHitResult, Hand> isAvailable() {
        return this.available;
    }


    @FunctionalInterface
    public interface ItemUseOnEntity {
        /**
         * This function serves as a replacement for {@link Item#useOnEntity(ItemStack, PlayerEntity, LivingEntity, Hand)} and is called from {@link ItemStack#useOnEntity(PlayerEntity, LivingEntity, Hand)} via mixin.
         * <p></p>
         * If you want to call the default method inside this function, do not use {@link ItemStack#useOnEntity(PlayerEntity, LivingEntity, Hand)}. Use {@link Item#useOnEntity(ItemStack, PlayerEntity, LivingEntity, Hand)} instead.
         */
        ActionResult useItemOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand);
    }
}
