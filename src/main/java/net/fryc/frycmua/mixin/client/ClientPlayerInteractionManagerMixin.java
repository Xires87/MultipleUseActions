package net.fryc.frycmua.mixin.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fryc.frycmua.network.payload.SelectUseActionPayload;
import net.fryc.frycmua.util.interfaces.SelectsUseAction;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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

}
