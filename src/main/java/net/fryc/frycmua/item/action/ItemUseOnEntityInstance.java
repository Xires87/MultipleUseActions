package net.fryc.frycmua.item.action;

import net.fryc.frycmua.util.function.QuadPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

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
            QuadPredicate<PlayerEntity, Entity, EntityHitResult, Hand> available
    ) {
        super(classLevel, allowSubclasses, tickUsage, onStoppedUsing, finishUsing, maxUseTime, useAction);
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
        ActionResult useItemOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand);
    }
}
