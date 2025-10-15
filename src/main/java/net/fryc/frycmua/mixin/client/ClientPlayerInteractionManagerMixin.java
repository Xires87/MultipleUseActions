package net.fryc.frycmua.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fryc.frycmua.item.action.SelectsUseAction;
import net.fryc.frycmua.item.action.UseAction;
import net.fryc.frycmua.network.payload.SelectUseActionPayload;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
abstract class ClientPlayerInteractionManagerMixin {


    @Inject(method = "interactItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", at = @At(value = "HEAD"))
    private void sendSelectedUseActionToServer(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> ret) {
        ClientPlayNetworking.send(new SelectUseActionPayload(((SelectsUseAction) player).getSelectedUseAction()));
    }



    @WrapOperation(
            method = "interactItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;")
    )
    private TypedActionResult<ItemStack> replaceVanillaUseWithMuaUse(ItemStack instance, World world, PlayerEntity player, Hand hand, Operation<TypedActionResult<ItemStack>> original) {
        return ((UseAction) instance.getItem()).muaUseItem(((SelectsUseAction) player).getSelectedUseAction(), world, player, hand);

        //return original.call(instance, world, player, hand);
    }


}
