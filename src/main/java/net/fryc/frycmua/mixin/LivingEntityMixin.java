package net.fryc.frycmua.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fryc.frycmua.item.action.DefaultUseActions;
import net.fryc.frycmua.item.action.UseInstance;
import net.fryc.frycmua.util.interfaces.item.HoldsUseActions;
import net.fryc.frycmua.util.interfaces.player.SelectsUseAction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin implements SelectsUseAction {

    private Identifier selectedUseAction = DefaultUseActions.DEFAULT;

    @Nullable
    private UseInstance activeUseInstance = null;

    @Shadow
    protected ItemStack activeItemStack;




    @WrapOperation(
            method = "setCurrentHand(Lnet/minecraft/util/Hand;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxUseTime(Lnet/minecraft/entity/LivingEntity;)I")
    )
    private int saveUseInstance(ItemStack instance, LivingEntity user, Operation<Integer> original) {
        UseInstance useInstance = ((HoldsUseActions) instance.getItem()).getUseInstance(((SelectsUseAction) user).getSelectedUseAction());
        ((SelectsUseAction) user).setActiveUseInstance(useInstance);

        return original.call(instance, user);
    }

    /*

    @Inject(method = "setCurrentHand(Lnet/minecraft/util/Hand;)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getMaxUseTime(Lnet/minecraft/entity/LivingEntity;)V",
            shift = At.Shift.BEFORE
    ))
    private void saveUseInstanceOnLivingEntity(LivingEntity user, CallbackInfo info) {
        UseInstance useInstance = ((HoldsUseActions) this.activeItemStack.getItem()).getUseInstance(((SelectsUseAction) user).getSelectedUseAction());
        ((SelectsUseAction) user).setActiveUseInstance(useInstance);
    }
 */

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
}
