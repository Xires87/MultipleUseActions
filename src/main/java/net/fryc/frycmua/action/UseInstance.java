package net.fryc.frycmua.action;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
    private final ItemUseOnRelease useOnRelease;

    public UseInstance(
            Class<?> classLevel,
            boolean allowSubclasses,
            ItemTickUsage tickUsage,
            ItemOnStoppedUsing onStoppedUsing,
            ItemFinishUsing finishUsing,
            ItemMaxUseTime maxUseTime,
            ItemUseAction useAction,
            ItemUseOnRelease useOnRelease
    ){
        this.classLevel = classLevel;
        this.allowSubclasses = allowSubclasses;
        this.tickUsage = tickUsage;
        this.onStoppedUsing = onStoppedUsing;
        this.finishUsing = finishUsing;
        this.maxUseTime = maxUseTime;
        this.useAction = useAction;
        this.useOnRelease = useOnRelease;
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

    public ItemUseOnRelease getUseOnRelease() {
        return this.useOnRelease;
    }


    @FunctionalInterface
    public interface ItemFinishUsing {
        /**
         * This function serves as a replacement for {@link Item#finishUsing(ItemStack, World, LivingEntity)} and is called from {@link ItemStack#finishUsing(World, LivingEntity)} via mixin.
         * <p></p>
         * If you want to call the default method inside this function, do not use {@link ItemStack#finishUsing(World, LivingEntity)}. Use {@link Item#finishUsing(ItemStack, World, LivingEntity)} instead.
         */
        ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity user);
    }

    @FunctionalInterface
    public interface ItemOnStoppedUsing {
        /**
         * This function serves as a replacement for {@link Item#onStoppedUsing(ItemStack, World, LivingEntity, int)} and is called from {@link ItemStack#onStoppedUsing(World, LivingEntity, int)} via mixin.
         * <p></p>
         * If you want to call the default method inside this function, do not use {@link ItemStack#onStoppedUsing(World, LivingEntity, int)}. Use {@link Item#onStoppedUsing(ItemStack, World, LivingEntity, int)} instead.
         */
        void onStoppedUsingItem(ItemStack stack, World world, LivingEntity user, int remainingUseTicks);
    }

    @FunctionalInterface
    public interface ItemTickUsage {
        /**
         * This function serves as a replacement for {@link Item#usageTick(World, LivingEntity, ItemStack, int)} and is called from {@link ItemStack#usageTick(World, LivingEntity, int)} via mixin.
         * <p></p>
         * If you want to call the default method inside this function, do not use {@link ItemStack#usageTick(World, LivingEntity, int)}. Use {@link Item#usageTick(World, LivingEntity, ItemStack, int)} instead.
         */
        void itemUsageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks);
    }

    @FunctionalInterface
    public interface ItemMaxUseTime {
        /**
         * This function serves as a replacement for {@link Item#getMaxUseTime(ItemStack, LivingEntity)} and is called from {@link ItemStack#getMaxUseTime(LivingEntity)} via mixin.
         * <p></p>
         * If you want to call the default method inside this function, do not use {@link ItemStack#getMaxUseTime(LivingEntity)}. Use {@link Item#getMaxUseTime(ItemStack, LivingEntity)} instead.
         */
        int getMaxItemUseTime(ItemStack stack, LivingEntity user);
    }

    @FunctionalInterface
    public interface ItemUseAction {
        /**
         * This function serves as a replacement for {@link Item#getUseAction(ItemStack)} and is called from {@link ItemStack#getUseAction()} via mixin.
         * <p></p>
         * If you want to call the default method inside this function, do not use {@link ItemStack#getUseAction()}. Use {@link Item#getUseAction(ItemStack)} instead.
         */
        UseAction getItemUseAction(ItemStack stack);
    }

    @FunctionalInterface
    public interface ItemUseOnRelease {
        /**
         * This function serves as a replacement for {@link Item#isUsedOnRelease(ItemStack)} and is called from {@link ItemStack#isUsedOnRelease()} via mixin.
         * <p></p>
         * If you want to call the default method inside this function, do not use {@link ItemStack#isUsedOnRelease()}. Use {@link Item#isUsedOnRelease(ItemStack)} instead.
         */
        boolean isItemUsedOnRelease(ItemStack stack);
    }
}
