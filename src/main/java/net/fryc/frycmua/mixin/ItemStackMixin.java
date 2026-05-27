package net.fryc.frycmua.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.fryc.frycmua.util.UseActionHelper;
import net.fryc.frycmua.util.mixin_interfaces.item.ExecutesUseActions;
import net.fryc.frycmua.util.mixin_interfaces.player.SelectsUseAction;
import net.minecraft.component.ComponentHolder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
abstract class ItemStackMixin implements ComponentHolder, FabricItemStack {

    @Shadow
    public abstract Item getItem();

    @Unique
    @Nullable
    private Entity muaHolder = null;

    // I want to avoid injecting into HEAD and early returning to let other mods inject
    @WrapOperation(
            method = "use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;")
    )
    private TypedActionResult<ItemStack> replaceVanillaUseWithMuaUse_ItemStack(Item instance, World world, PlayerEntity user, Hand hand, Operation<TypedActionResult<ItemStack>> original) {
        if (UseActionHelper.shouldUseSelectedUseAction(((SelectsUseAction) user).getSelectedUseAction())) {
            return ((ExecutesUseActions) this.getItem()).muaUseItem(((SelectsUseAction) user).getSelectedUseAction(), world, user, hand);
        } else {
            return original.call(instance, world, user, hand);
        }
    }

    /*
    @Inject(method = "use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;", at = @At("RETURN"), cancellable = true)
    private void replaceVanillaUseWithMuaUse_ItemStack(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> ret) {
        if(UseActionHelper.shouldUseSelectedUseAction(((SelectsUseAction) user).getSelectedUseAction())){
            ret.setReturnValue(((ExecutesUseActions) this.getItem()).muaUseItem(((SelectsUseAction) user).getSelectedUseAction(), world, user, hand));
        }
    }
     */

    @WrapOperation(
            method = "useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;")
    )
    private ActionResult replaceVanillaUseWithMuaUseOnBlock_ItemStack(Item instance, ItemUsageContext context, Operation<ActionResult> original) {
        if(context.getPlayer() != null){
            if(UseActionHelper.shouldUseSelectedUseAction(((SelectsUseAction) context.getPlayer()).getSelectedUseAction())){
                return ((ExecutesUseActions) instance).muaUseItemOnBlock(((SelectsUseAction) context.getPlayer()).getSelectedUseAction(), context);
            }
        }

        return original.call(instance, context);
    }

    @WrapOperation(
            method = "useOnEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;useOnEntity(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;")
    )
    private ActionResult replaceVanillaUseOnEntityWithMuaUseOnEntity_ItemStack(Item instance, ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, Operation<ActionResult> original) {
        if (UseActionHelper.shouldUseSelectedUseAction(((SelectsUseAction) user).getSelectedUseAction())) {
            return ((ExecutesUseActions) this.getItem()).muaUseItemOnEntity(
                    ((SelectsUseAction) user).getSelectedUseAction(),
                    ((ItemStack)(Object)this),
                    user,
                    entity,
                    hand
            );
        } else {
            return original.call(instance, stack, user, entity, hand);
        }
    }

    /*
    @Inject(
            method = "useOnEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void replaceVanillaUseOnEntityWithMuaUseOnEntity_ItemStack(PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> ret) {
        if(UseActionHelper.shouldUseSelectedUseAction(((SelectsUseAction) user).getSelectedUseAction())){
            ret.setReturnValue(((ExecutesUseActions) this.getItem()).muaUseItemOnEntity(
                    ((SelectsUseAction) user).getSelectedUseAction(),
                    ((ItemStack)(Object)this),
                    user,
                    entity,
                    hand
            ));
        }
    }

     */

    @WrapOperation(
            method = "onStoppedUsing(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;onStoppedUsing(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V")
    )
    private void replaceVanillaOnStoppedUsingWithMuaOnStoppedUsing_ItemStack(Item instance, ItemStack stack, World world, LivingEntity user, int remainingUseTicks, Operation<Void> original) {
        if (((SelectsUseAction) user).getActiveUseInstance() != null) {
            ((SelectsUseAction) user).getActiveUseInstance().getOnStoppedUsing().onStoppedUsingItem(((ItemStack)(Object) this), world, user, remainingUseTicks);
        } else {
            original.call(instance, stack, world, user, remainingUseTicks);
        }
    }

    /*
    @Inject(method = "onStoppedUsing(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/Item;onStoppedUsing(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V",
            shift = At.Shift.BEFORE
    ), cancellable = true)
    private void replaceVanillaOnStoppedUsingWithMuaOnStoppedUsing_ItemStack(World world, LivingEntity user, int remainingUseTicks, CallbackInfo info) {
            if(((SelectsUseAction) user).getActiveUseInstance() != null){
                ((SelectsUseAction) user).getActiveUseInstance().getOnStoppedUsing().onStoppedUsingItem(((ItemStack)(Object) this), world, user, remainingUseTicks);

                info.cancel();
            }
    }

     */

    @WrapOperation(
            method = "getMaxUseTime(Lnet/minecraft/entity/LivingEntity;)I",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getMaxUseTime(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)I")
    )
    private int replaceVanillaGetMaxUseTimeWithMuaGetMaxUseTime_ItemStack(Item instance, ItemStack stack, LivingEntity user, Operation<Integer> original) {
        if(((SelectsUseAction) user).getActiveUseInstance() != null){
            return ((SelectsUseAction) user).getActiveUseInstance().getMaxUseTime().getMaxItemUseTime(((ItemStack)(Object) this), user);
        } else {
            return original.call(instance, stack, user);
        }
    }

    /*
    @Inject(method = "getMaxUseTime(Lnet/minecraft/entity/LivingEntity;)I", at = @At("RETURN"), cancellable = true)
    private void replaceVanillaGetMaxUseTimeWithMuaGetMaxUseTime_ItemStack(LivingEntity user, CallbackInfoReturnable<Integer> ret) {
        if(((SelectsUseAction) user).getActiveUseInstance() != null){
            ret.setReturnValue(((SelectsUseAction) user).getActiveUseInstance().getMaxUseTime().getMaxItemUseTime(((ItemStack)(Object) this), user));
        }
    }
    */

    @WrapOperation(
            method = "finishUsing(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/item/ItemStack;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;finishUsing(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/item/ItemStack;")
    )
    private ItemStack replaceVanillaFinishUsingWIthMuaFinishUsing_ItemStack(Item instance, ItemStack stack, World world, LivingEntity user, Operation<ItemStack> original) {
        if(((SelectsUseAction) user).getActiveUseInstance() != null){
            return ((SelectsUseAction) user).getActiveUseInstance().getFinishUsing().finishUsingItem(((ItemStack)(Object) this), world, user);
        } else {
            return original.call(instance, stack, world, user);
        }
    }

    /*
    @Inject(method = "finishUsing(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/item/ItemStack;", at = @At("RETURN"), cancellable = true)
    private void replaceVanillaFinishUsingWIthMuaFinishUsing_ItemStack(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> ret) {
        if(((SelectsUseAction) user).getActiveUseInstance() != null){
            ret.setReturnValue(((SelectsUseAction) user).getActiveUseInstance().getFinishUsing().finishUsingItem(((ItemStack)(Object) this), world, user));
        }
    }
     */

    @WrapOperation(
            method = "usageTick(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;usageTick(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;I)V")
    )
    private void replaceVanillaUsageTickUsingWithMuaUsageTick_ItemStack(Item instance, World world, LivingEntity user, ItemStack stack, int remainingUseTicks, Operation<Void> original) {
        if(((SelectsUseAction) user).getActiveUseInstance() != null){
            ((SelectsUseAction) user).getActiveUseInstance().getTickUsage().itemUsageTick(world, user, ((ItemStack)(Object) this), remainingUseTicks);
        } else {
            original.call(instance, world, user, stack, remainingUseTicks);
        }
    }

    /*
    @Inject(method = "usageTick(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/Item;usageTick(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;I)V",
            shift = At.Shift.BEFORE
    ), cancellable = true)
    private void replaceVanillaUsageTickUsingWithMuaUsageTick_ItemStack(World world, LivingEntity user, int remainingUseTicks, CallbackInfo info) {
        if(((SelectsUseAction) user).getActiveUseInstance() != null){
            ((SelectsUseAction) user).getActiveUseInstance().getTickUsage().itemUsageTick(world, user, ((ItemStack)(Object) this), remainingUseTicks);

            info.cancel();
        }
    }

     */

    @WrapOperation(
            method = "getUseAction()Lnet/minecraft/util/UseAction;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getUseAction(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/util/UseAction;")
    )
    private UseAction replaceVanillaGetUseActionWithMuaGetUseAction_ItemStack(Item instance, ItemStack stack, Operation<UseAction> original) {
        if(this.muaHolder != null){
            if(this.muaHolder instanceof LivingEntity user) {
                if(((SelectsUseAction) user).getActiveUseInstance() != null){
                    return ((SelectsUseAction) user).getActiveUseInstance().getUseAction().getItemUseAction(((ItemStack)(Object) this));
                }
            }
        }

        return original.call(instance, stack);
    }

    /*
    @Inject(method = "getUseAction()Lnet/minecraft/util/UseAction;", at = @At("RETURN"), cancellable = true)
    private void replaceVanillaGetUseActionWithMuaGetUseAction_ItemStack(CallbackInfoReturnable<UseAction> ret) {
        if(this.muaHolder != null){
            if(this.muaHolder instanceof LivingEntity user) {
                if(((SelectsUseAction) user).getActiveUseInstance() != null){
                    ret.setReturnValue(((SelectsUseAction) user).getActiveUseInstance().getUseAction().getItemUseAction(((ItemStack)(Object) this)));
                }
            }
        }
    }

     */

    @WrapOperation(
            method = "isUsedOnRelease()Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isUsedOnRelease(Lnet/minecraft/item/ItemStack;)Z")
    )
    private boolean replaceVanillaIsUsedOnReleaseWithMuaIsUsedOnRelease_ItemStack(Item instance, ItemStack stack, Operation<Boolean> original) {
        if(this.muaHolder != null){
            if(this.muaHolder instanceof LivingEntity user) {
                if(((SelectsUseAction) user).getActiveUseInstance() != null){
                    return ((SelectsUseAction) user).getActiveUseInstance().getUseOnRelease().isItemUsedOnRelease(((ItemStack)(Object) this));
                }
            }
        }

        return original.call(instance, stack);
    }

    /*
    @Inject(method = "isUsedOnRelease()Z", at = @At("RETURN"), cancellable = true)
    private void replaceVanillaIsUsedOnReleaseWithMuaIsUsedOnRelease_ItemStack(CallbackInfoReturnable<Boolean> ret) {
        if(this.muaHolder != null){
            if(this.muaHolder instanceof LivingEntity user) {
                if(((SelectsUseAction) user).getActiveUseInstance() != null){
                    ret.setReturnValue(((SelectsUseAction) user).getActiveUseInstance().getUseOnRelease().isItemUsedOnRelease(((ItemStack)(Object) this)));
                }
            }
        }
    }
     */

    @Inject(method = "inventoryTick(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;IZ)V", at = @At("TAIL"))
    private void saveEntityThatIsHoldingStack(World world, Entity entity, int slot, boolean selected, CallbackInfo info) {
        this.muaHolder = entity;
    }
}
