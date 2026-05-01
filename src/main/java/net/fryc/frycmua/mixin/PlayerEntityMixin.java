package net.fryc.frycmua.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fryc.frycmua.util.mixin_interfaces.player.SelectsUseAction;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin implements SelectsUseAction {

    @ModifyReturnValue(
            method = "shouldCancelInteraction()Z",
            at = @At("RETURN")
    )
    private boolean cancelBlockInteractionWhenNeeded(boolean original) {
        return original || this.shouldBlockInteractionBeCancelled();
    }

}
