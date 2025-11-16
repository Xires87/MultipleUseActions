package net.fryc.frycmua.item.action;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.function.BiPredicate;

public class ItemUseInstance extends UseInstance {


    private final ItemUse use;
    private final BiPredicate<PlayerEntity, Hand> isAvailable;

    public ItemUseInstance(
            Class<?> classLevel,
            boolean allowSubclasses,
            ItemUse use,
            ItemTickUsage tickUsage,
            ItemOnStoppedUsing onStoppedUsing,
            ItemFinishUsing finishUsing,
            ItemMaxUseTime maxUseTime,
            ItemUseAction useAction,
            BiPredicate<PlayerEntity, Hand> isAvailable
    ){
        super(classLevel, allowSubclasses, tickUsage, onStoppedUsing, finishUsing, maxUseTime, useAction);
        this.use = use;
        this.isAvailable = isAvailable;
    }


    public ItemUse getUse() {
        return this.use;
    }

    public BiPredicate<PlayerEntity, Hand> isAvailable() {
        return this.isAvailable;
    }


    @FunctionalInterface
    public interface ItemUse {
        TypedActionResult<ItemStack> useItem(Item item, World world, PlayerEntity user, Hand hand);
    }
}
