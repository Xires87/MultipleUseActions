package net.fryc.frycmua.item.action;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class ItemUseOnEntityInstance extends UseInstance {


    private final ItemUseOnEntity useOnEntity;

    public ItemUseOnEntityInstance(
            Class<?> classLevel,
            boolean allowSubclasses,
            ItemUseOnEntity useOnEntity,
            ItemTickUsage tickUsage,
            ItemOnStoppedUsing onStoppedUsing,
            ItemFinishUsing finishUsing,
            ItemMaxUseTime maxUseTime,
            ItemUseAction useAction
    ) {
        super(classLevel, allowSubclasses, tickUsage, onStoppedUsing, finishUsing, maxUseTime, useAction);
        this.useOnEntity = useOnEntity;
    }


    @FunctionalInterface
    public interface ItemUseOnEntity {
        ActionResult useItemOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand);
    }
}
