package net.fryc.frycmua.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fryc.frycmua.item.action.DefaultUseActions;
import net.fryc.frycmua.util.interfaces.ExecutesUseActions;
import net.fryc.frycmua.util.interfaces.SelectsUseAction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerInteractionManager.class)
abstract class ServerPlayerInteractionManagerMixin {

    @WrapOperation(
            method = "interactItem(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;")
    )
    private TypedActionResult<ItemStack> replaceUseWithMuaUse(ItemStack instance, World world, PlayerEntity player, Hand hand, Operation<TypedActionResult<ItemStack>> original) {
        if(((SelectsUseAction) player).getSelectedUseAction().toString().equals(DefaultUseActions.USE_DEFAULT.toString())){
            return original.call(instance, world, player, hand);
        }

        return ((ExecutesUseActions) instance.getItem()).muaUseItem(((SelectsUseAction) player).getSelectedUseAction(), world, player, hand);
    }
}
