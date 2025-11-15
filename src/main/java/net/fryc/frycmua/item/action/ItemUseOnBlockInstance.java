package net.fryc.frycmua.item.action;

import net.fryc.frycmua.item.action.registry.UseActionRegistries;

public class ItemUseOnBlockInstance extends UseInstance {

    private final UseActionRegistries.ItemUseOnBlock useOnBlock;

    public ItemUseOnBlockInstance(
            Class<?> classLevel,
            boolean allowSubclasses,
            UseActionRegistries.ItemUseOnBlock useOnBlock,
            UseActionRegistries.ItemTickUsage tickUsage,
            UseActionRegistries.ItemOnStoppedUsing onStoppedUsing,
            UseActionRegistries.ItemFinishUsing finishUsing,
            UseActionRegistries.ItemMaxUseTime maxUseTime,
            UseActionRegistries.ItemUseAction useAction
    ) {
        super(classLevel, allowSubclasses, tickUsage, onStoppedUsing, finishUsing, maxUseTime, useAction);
        this.useOnBlock = useOnBlock;
    }

}
