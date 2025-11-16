package net.fryc.frycmua.item.action;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public abstract class UseInstance {

    private final Class<?> classLevel;
    private final boolean allowSubclasses;
    private final ItemTickUsage tickUsage;
    private final ItemOnStoppedUsing onStoppedUsing;
    private final ItemFinishUsing finishUsing;
    private final ItemMaxUseTime maxUseTime;
    private final ItemUseAction useAction;

    public UseInstance(
            Class<?> classLevel,
            boolean allowSubclasses,
            ItemTickUsage tickUsage,
            ItemOnStoppedUsing onStoppedUsing,
            ItemFinishUsing finishUsing,
            ItemMaxUseTime maxUseTime,
            ItemUseAction useAction
    ){
        this.classLevel = classLevel;
        this.allowSubclasses = allowSubclasses;
        this.tickUsage = tickUsage;
        this.onStoppedUsing = onStoppedUsing;
        this.finishUsing = finishUsing;
        this.maxUseTime = maxUseTime;
        this.useAction = useAction;
    }


    public Class<?> getClassLevel() {
        return this.classLevel;
    }

    public boolean isAllowSubclasses() {
        return this.allowSubclasses;
    }

    public ItemTickUsage getTickUsage() {
        return this.tickUsage;
    }

    public ItemOnStoppedUsing getOnStoppedUsing() {
        return this.onStoppedUsing;
    }

    public ItemFinishUsing getFinishUsing() {
        return this.finishUsing;
    }

    public ItemMaxUseTime getMaxUseTime() {
        return this.maxUseTime;
    }

    public ItemUseAction getUseAction() {
        return this.useAction;
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
