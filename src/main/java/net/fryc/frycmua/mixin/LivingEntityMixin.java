package net.fryc.frycmua.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fryc.frycmua.action.DefaultUseActions;
import net.fryc.frycmua.action.UseInstance;
import net.fryc.frycmua.util.mixin_interfaces.item.HoldsUseActions;
import net.fryc.frycmua.util.mixin_interfaces.player.SelectsUseAction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LivingEntity.class, priority = 500)
abstract class LivingEntityMixin implements SelectsUseAction {

    @Unique
    private Identifier selectedUseAction = DefaultUseActions.DEFAULT;

    @Unique
    @Nullable
    private UseInstance activeUseInstance = null;

    @Unique
    private boolean shouldCancelInteraction = false;

    @Unique
    private boolean shouldCancelItemUsage = false;


    @WrapOperation(
            method = "setCurrentHand(Lnet/minecraft/util/Hand;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxUseTime(Lnet/minecraft/entity/LivingEntity;)I")
    )
    private int saveUseInstance(ItemStack instance, LivingEntity user, Operation<Integer> original) {
        UseInstance useInstance = ((HoldsUseActions) instance.getItem()).getUseInstance(((SelectsUseAction) user).getSelectedUseAction());
        ((SelectsUseAction) user).setActiveUseInstance(useInstance);

        return original.call(instance, user);
    }

    @WrapOperation(
            method = "isBlocking()Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getUseAction(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/util/UseAction;")
    )
    private UseAction replaceItemGetUseActionWithStackGetUseAction_isBlocking(Item instance, ItemStack stack, Operation<UseAction> original) {
        return stack.getUseAction();
    }

    @WrapOperation(
            method = "isBlocking()Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getMaxUseTime(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)I")
    )
    private int replaceItemGetMaxUseTimeWithStackGetMaxUseTime_isBlocking(Item instance, ItemStack stack, LivingEntity user, Operation<Integer> original) {
        return stack.getMaxUseTime(user);
    }

    @Override
    public Identifier getSelectedUseAction() {
        return this.selectedUseAction;
    }

    @Override
    public void setSelectedUseAction(@Nullable Identifier id) {
        this.selectedUseAction = id;
    }

    @Override
    @Nullable
    public UseInstance getActiveUseInstance() {
        return this.activeUseInstance;
    }

    @Override
    public void setActiveUseInstance(@Nullable UseInstance useInstance) {
        this.activeUseInstance = useInstance;
    }

    @Override
    public boolean shouldBlockInteractionBeCancelled() {
        return this.shouldCancelInteraction;
    }

    @Override
    public void setShouldBlockInteractionBeCancelled(boolean shouldCancel) {
        this.shouldCancelInteraction = shouldCancel;
    }

    @Override
    public boolean shouldItemUsageBeCancelled() {
        return this.shouldCancelItemUsage;
    }

    @Override
    public void setShouldItemUsageBeCancelled(boolean shouldCancel) {
        this.shouldCancelItemUsage = shouldCancel;
    }
}
