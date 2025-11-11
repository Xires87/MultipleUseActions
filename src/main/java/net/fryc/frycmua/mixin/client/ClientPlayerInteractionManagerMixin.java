package net.fryc.frycmua.mixin.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fryc.frycmua.util.interfaces.SelectsUseAction;
import net.fryc.frycmua.network.payload.SelectUseActionPayload;
import net.fryc.frycmua.util.interfaces.ExecutesUseActions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
abstract class ClientPlayerInteractionManagerMixin {

    @Shadow
    private @Final MinecraftClient client;


    @Shadow
    abstract void sendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator);

    /*
    @Inject(method = "interactItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", at = @At(value = "HEAD"))
    private void sendSelectedUseActionToServer(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> ret) {
        ClientPlayNetworking.send(new SelectUseActionPayload(((SelectsUseAction) player).getSelectedUseAction()));
    }

     */

    @Inject(
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;syncSelectedSlot()V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )},
            method = "interactItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
            cancellable = true
    )
    public void useSelectedUseAction(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> ret) {
        ClientPlayNetworking.send(new SelectUseActionPayload(((SelectsUseAction) player).getSelectedUseAction()));

        MutableObject<ActionResult> mutableObject = new MutableObject();
        this.sendSequencedPacket(this.client.world, (sequence) -> {
            PlayerInteractItemC2SPacket playerInteractItemC2SPacket = new PlayerInteractItemC2SPacket(hand, sequence, player.getYaw(), player.getPitch());
            ItemStack itemStack = player.getStackInHand(hand);
            if (player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
                mutableObject.setValue(ActionResult.PASS);
                return playerInteractItemC2SPacket;
            } else {
                TypedActionResult<ItemStack> typedActionResult = ((ExecutesUseActions) itemStack.getItem()).muaUseItem(((SelectsUseAction) player).getSelectedUseAction(), this.client.world, player, hand);
                ItemStack itemStack2 = (ItemStack)typedActionResult.getValue();
                if (itemStack2 != itemStack) {
                    player.setStackInHand(hand, itemStack2);
                }

                mutableObject.setValue(typedActionResult.getResult());
                return playerInteractItemC2SPacket;
            }
        });

        ret.setReturnValue((ActionResult)mutableObject.getValue());
    }

/*
    @ModifyVariable(method = "interactItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", at = @At(value = "HEAD"), ordinal = 0)
    private Hand selectInteractionHand(Hand hand) {
        return Hand.OFF_HAND;
    }

 */


}
