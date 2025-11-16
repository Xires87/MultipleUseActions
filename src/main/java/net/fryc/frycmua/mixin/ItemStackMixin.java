package net.fryc.frycmua.mixin;

import net.fryc.frycmua.util.UseActionHelper;
import net.fryc.frycmua.util.interfaces.ExecutesUseActions;
import net.fryc.frycmua.util.interfaces.SelectsUseAction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
abstract class ItemStackMixin {

    @Shadow
    public abstract Item getItem();


    @Inject(method = "use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;", at = @At("RETURN"), cancellable = true)
    private void replaceVanillaUseWithMuaUseOnItemStack(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> ret) {
        if(UseActionHelper.shouldUseSelectedUseAction(((SelectsUseAction) user).getSelectedUseAction())){
            ret.setReturnValue(((ExecutesUseActions) this.getItem()).muaUseItem(((SelectsUseAction) user).getSelectedUseAction(), world, user, hand));
        }
    }

}
