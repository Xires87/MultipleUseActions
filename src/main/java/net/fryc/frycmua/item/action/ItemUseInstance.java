package net.fryc.frycmua.item.action;

import net.fryc.frycmua.item.action.registry.UseActionRegistries;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

import java.util.function.BiPredicate;

public class ItemUseInstance extends UseInstance {


    private final UseActionRegistries.ItemUse use;
    private final BiPredicate<PlayerEntity, Hand> isAvailable;

    public ItemUseInstance(
            Class<?> classLevel,
            boolean allowSubclasses,
            UseActionRegistries.ItemUse use,
            UseActionRegistries.ItemTickUsage tickUsage,
            UseActionRegistries.ItemOnStoppedUsing onStoppedUsing,
            UseActionRegistries.ItemFinishUsing finishUsing,
            UseActionRegistries.ItemMaxUseTime maxUseTime,
            UseActionRegistries.ItemUseAction useAction,
            BiPredicate<PlayerEntity, Hand> isAvailable
    ){
        super(classLevel, allowSubclasses, tickUsage, onStoppedUsing, finishUsing, maxUseTime, useAction);
        this.use = use;
        this.isAvailable = isAvailable;
    }


    public UseActionRegistries.ItemUse getUse() {
        return this.use;
    }

    public BiPredicate<PlayerEntity, Hand> isAvailable() {
        return this.isAvailable;
    }
}
