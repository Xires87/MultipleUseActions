package net.fryc.frycmua.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fryc.frycmua.item.action.DefaultUseActions;
import net.fryc.frycmua.item.action.ItemUseInstance;
import net.fryc.frycmua.item.action.UseInstance;
import net.fryc.frycmua.util.interfaces.HoldsActionManager;
import net.fryc.frycmua.util.interfaces.HoldsUseActions;
import net.fryc.frycmua.util.interfaces.SelectsUseAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
abstract class MinecraftClientMixin {

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public ClientPlayerInteractionManager interactionManager;

/*
    @ModifyExpressionValue(
            method = "doItemUse()V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;", opcode = Opcodes.GETFIELD, ordinal = 1)
    )
    private HitResult controlItemUse(HitResult original) {
        if(original != null){
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if(player != null) {
                if(!((SelectsUseAction) player).getSelectedUseAction().toString().equals(DefaultUseActions.USE_DEFAULT.toString())) {
                    // TODO dziala (po kliknieciu przycisku nie mozna interactowac z blokami, tylko uzywa luku)
                    return null;
                }
            }
            // TODO sprawdzanie czy powinno z vanilli korzystac czy z mojego
            // TODO to trzeba bedzie w wiele miejsc zinjectowac, nie tylko tutaj (interactItem, interactBlock itd.)
            // TODO jesli chodzi o interakcje z blokami to bedzie do wyboru: useOnBlock, interactWithBlock i interactWithBlockUsingItem
        }

        return original;
    }

 */

    @ModifyExpressionValue(
            method = "doItemUse()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isRiding()Z")
    )
    private boolean controlItemUse(boolean original) {
        if(!original){
            for(Identifier id : ((HoldsActionManager) this.player).getActionManager().getAvailableActions()){
                // TODO musze tutaj odpalic wlasne interactItem dla wybranej akcji
                if(id.toString().equals(DefaultUseActions.USE_DEFAULT.toString())){
                    ((SelectsUseAction) this.player).setSelectedUseAction(id);
                    break;
                }

                Hand hand = Hand.MAIN_HAND;
                UseInstance instance = ((HoldsUseActions) this.player.getMainHandStack().getItem()).getUseInstance(id);
                if(instance == null){
                    instance = ((HoldsUseActions) this.player.getOffHandStack().getItem()).getUseInstance(id);
                    hand = Hand.OFF_HAND;
                }

                // checking for use
                if(instance instanceof ItemUseInstance useInstance){
                    if(useInstance.isAvailable().test(this.player, hand)){
                        ((SelectsUseAction) this.player).setSelectedUseAction(id);
                        this.interactionManager.interactItem(this.player, hand);

                        return true;
                    }
                }

                // TODO checking for use on block
                // TODO checking for use on entity
                // TODO tymczasowo reszte tu dac (ale musze najpierw inheritance zrobic bo tamte nie extenduja bo recordami sa)
            }
        }

        return original;
    }

}
