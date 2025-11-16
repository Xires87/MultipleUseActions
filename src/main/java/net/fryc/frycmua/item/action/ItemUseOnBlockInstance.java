package net.fryc.frycmua.item.action;

import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class ItemUseOnBlockInstance extends UseInstance {

    private final ItemUseOnBlock useOnBlock;

    public ItemUseOnBlockInstance(
            Class<?> classLevel,
            boolean allowSubclasses,
            ItemUseOnBlock useOnBlock,
            ItemTickUsage tickUsage,
            ItemOnStoppedUsing onStoppedUsing,
            ItemFinishUsing finishUsing,
            ItemMaxUseTime maxUseTime,
            ItemUseAction useAction
    ) {
        super(classLevel, allowSubclasses, tickUsage, onStoppedUsing, finishUsing, maxUseTime, useAction);
        this.useOnBlock = useOnBlock;
    }


    @FunctionalInterface
    public interface ItemUseOnBlock {
        ActionResult useItemOnBlock(ItemUsageContext context);
    }
}
