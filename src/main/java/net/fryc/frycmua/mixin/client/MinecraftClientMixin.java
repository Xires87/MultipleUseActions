package net.fryc.frycmua.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fryc.frycmua.item.action.*;
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
                if(id.toString().equals(DefaultUseActions.USE_DEFAULT.toString())){
                    ((SelectsUseAction) this.player).setSelectedUseAction(id);
                    break;
                }

                Hand hand = Hand.MAIN_HAND;// TODO wybieranie priorytetowej reki
                UseInstance instance = ((HoldsUseActions) this.player.getMainHandStack().getItem()).getUseInstance(id);
                if(instance == null){
                    instance = ((HoldsUseActions) this.player.getOffHandStack().getItem()).getUseInstance(id);
                    hand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
                }

                // checking for use
                if(instance instanceof ItemUseInstance useInstance){
                    if(useInstance.isAvailable().test(this.player, hand)){
                        ((SelectsUseAction) this.player).setSelectedUseAction(id);
                        this.interactionManager.interactItem(this.player, hand);

                        return true;// TODO musze returnowac tylko w przypoadku jak action result jest success albo fail, na passie musze leciec dalej petla
                    }
                }
                else if(instance instanceof ItemUseOnBlockInstance useInstance){
                    // TODO do przerobienia tu
                    /*
                    if(useInstance.isAvailable().test(this.player, hand)){
                        ((SelectsUseAction) this.player).setSelectedUseAction(id);
                        this.interactionManager.interactBlock(this.player, hand);

                        return true;
                    }

                     */
                }
                else if(instance instanceof ItemUseOnEntityInstance useInstance){
                    // TODO tu tez do przerobienia
                    /*
                    if(useInstance.isAvailable().test(this.player, hand)){
                        ((SelectsUseAction) this.player).setSelectedUseAction(id);
                        this.interactionManager.interactEntity(this.player, hand);

                        return true;
                    }

                     */
                }
            }
        }

        return original;
    }

}
