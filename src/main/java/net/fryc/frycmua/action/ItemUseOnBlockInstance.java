package net.fryc.frycmua.action;

import net.fryc.frycmua.util.function.TriPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public class ItemUseOnBlockInstance extends UseInstance {

    private final ItemUseOnBlock useOnBlock;
    private final TriPredicate<PlayerEntity, BlockHitResult, Hand> available;

    public ItemUseOnBlockInstance(
            Class<?> classLevel,
            boolean allowSubclasses,
            ItemUseOnBlock useOnBlock,
            ItemTickUsage tickUsage,
            ItemOnStoppedUsing onStoppedUsing,
            ItemFinishUsing finishUsing,
            ItemMaxUseTime maxUseTime,
            ItemUseAction useAction,
            ItemUseOnRelease useOnRelease,
            TriPredicate<PlayerEntity, BlockHitResult, Hand> available
    ) {
        super(classLevel, allowSubclasses, tickUsage, onStoppedUsing, finishUsing, maxUseTime, useAction, useOnRelease);
        this.useOnBlock = useOnBlock;
        this.available = available;
    }


    public ItemUseOnBlock getUseOnBlock() {
        return this.useOnBlock;
    }

    public TriPredicate<PlayerEntity, BlockHitResult, Hand> isAvailable() {
        return this.available;
    }


    @FunctionalInterface
    public interface ItemUseOnBlock {
        /**
         * This function serves as a replacement for {@link Item#useOnBlock(ItemUsageContext)} and is called from {@link ItemStack#useOnBlock(ItemUsageContext)} via mixin.
         * <p></p>
         * If you want to call the default method inside this function, do not use {@link ItemStack#useOnBlock(ItemUsageContext)}. Use {@link Item#useOnBlock(ItemUsageContext)} instead.
         */
        ActionResult useItemOnBlock(ItemUsageContext context);
    }
}
