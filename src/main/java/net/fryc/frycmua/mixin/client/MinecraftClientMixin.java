package net.fryc.frycmua.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fryc.frycmua.item.action.SelectsUseAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.HitResult;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
abstract class MinecraftClientMixin {

    @ModifyExpressionValue(
            method = "doItemUse()V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;", opcode = Opcodes.GETFIELD, ordinal = 1)
    )
    private HitResult controlItemUse(HitResult original) {
        if(original != null){
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if(player != null) {
                if(((SelectsUseAction) player).getSelectedUseAction() != null) {
                    return null;
                }
            }
            // TODO sprawdzanie czy powinno z vanilli korzystac czy z mojego
            // TODO to trzeba bedzie w wiele miejsc zinjectowac, nie tylko tutaj (interactItem, interactBlock itd.)
            // TODO jesli chodzi o interakcje z blokami to bedzie do wyboru: useOnBlock, interactWithBlock i interactWithBlockUsingItem
        }

        return original;
    }

}
