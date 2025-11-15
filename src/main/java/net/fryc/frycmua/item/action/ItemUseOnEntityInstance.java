package net.fryc.frycmua.item.action;

import net.fryc.frycmua.item.action.registry.UseActionRegistries;

public class ItemUseOnEntityInstance extends UseInstance {


    private final UseActionRegistries.ItemUseOnEntity useOnEntity;

    public ItemUseOnEntityInstance(
            Class<?> classLevel,
            boolean allowSubclasses,
            UseActionRegistries.ItemUseOnEntity useOnEntity,
            UseActionRegistries.ItemTickUsage tickUsage,
            UseActionRegistries.ItemOnStoppedUsing onStoppedUsing,
            UseActionRegistries.ItemFinishUsing finishUsing,
            UseActionRegistries.ItemMaxUseTime maxUseTime,
            UseActionRegistries.ItemUseAction useAction
    ) {
        super(classLevel, allowSubclasses, tickUsage, onStoppedUsing, finishUsing, maxUseTime, useAction);
        this.useOnEntity = useOnEntity;
    }


}
