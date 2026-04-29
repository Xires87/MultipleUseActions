package net.fryc.frycmua.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.fryc.frycmua.util.UseActionHelper;
import net.fryc.frycmua.util.interfaces.item.ExecutesUseActions;
import net.fryc.frycmua.util.interfaces.player.SelectsUseAction;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
abstract class ItemStackMixin implements ComponentHolder, FabricItemStack {

    @Shadow
    public abstract Item getItem();

    @Nullable
    private Entity muaHolder = null;


    @Inject(method = "use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;", at = @At("RETURN"), cancellable = true)
    private void replaceVanillaUseWithMuaUse_ItemStack(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> ret) {
        if(UseActionHelper.shouldUseSelectedUseAction(((SelectsUseAction) user).getSelectedUseAction())){
            ret.setReturnValue(((ExecutesUseActions) this.getItem()).muaUseItem(((SelectsUseAction) user).getSelectedUseAction(), world, user, hand));
        }
    }

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

    @Inject(method = "getMaxUseTime(Lnet/minecraft/entity/LivingEntity;)I", at = @At("RETURN"), cancellable = true)
    private void replaceVanillaGetMaxUseTimeWithMuaGetMaxUseTime_ItemStack(LivingEntity user, CallbackInfoReturnable<Integer> ret) {
        if(((SelectsUseAction) user).getActiveUseInstance() != null){
            ret.setReturnValue(((SelectsUseAction) user).getActiveUseInstance().getMaxUseTime().getMaxItemUseTime(((ItemStack)(Object) this), user));
        }
    }

    @Inject(method = "finishUsing(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/item/ItemStack;", at = @At("RETURN"), cancellable = true)
    private void replaceVanillaFinishUsingWIthMuaFinishUsing_ItemStack(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> ret) {
        if(((SelectsUseAction) user).getActiveUseInstance() != null){
            ret.setReturnValue(((SelectsUseAction) user).getActiveUseInstance().getFinishUsing().finishUsingItem(((ItemStack)(Object) this), world, user));
        }
    }


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

    @Inject(method = "getUseAction()Lnet/minecraft/util/UseAction;", at = @At("RETURN"), cancellable = true)
    private void replaceVanillaGetUseActionWithMuaGetUseAction_ItemStack(CallbackInfoReturnable<UseAction> ret) {
        // TODO holder jest null, musze albo ustawiac ownera na itemstackach albo podmienic metode zeby co innego sie odpalalo (chyba podmienie metode)
        if(this.muaHolder != null){
            if(this.muaHolder instanceof LivingEntity user) {
                if(((SelectsUseAction) user).getActiveUseInstance() != null){
                    ret.setReturnValue(((SelectsUseAction) user).getActiveUseInstance().getUseAction().getItemUseAction(((ItemStack)(Object) this)));
                }
            }
        }
    }

    @Inject(method = "isUsedOnRelease()Z", at = @At("RETURN"), cancellable = true)
    private void replaceVanillaIsUsedOnReleaseWithMuaIsUsedOnRelease_ItemStack(CallbackInfoReturnable<Boolean> ret) {
        // TODO holder jest null, musze albo ustawiac ownera na itemstackach albo podmienic metode zeby co innego sie odpalalo (chyba podmienie metode)
        if(this.muaHolder != null){
            if(this.muaHolder instanceof LivingEntity user) {
                if(((SelectsUseAction) user).getActiveUseInstance() != null){
                    ret.setReturnValue(((SelectsUseAction) user).getActiveUseInstance().getUseOnRelease().isItemUsedOnRelease(((ItemStack)(Object) this)));
                }
            }
        }
    }

    @Inject(method = "inventoryTick(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;IZ)V", at = @At("TAIL"))
    private void saveEntityThatIsHoldingStack(World world, Entity entity, int slot, boolean selected, CallbackInfo info) {
        this.muaHolder = entity;
    }
}
