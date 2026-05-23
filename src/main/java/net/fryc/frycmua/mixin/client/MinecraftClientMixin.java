package net.fryc.frycmua.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fryc.frycmua.MultipleUseActions;
import net.fryc.frycmua.action.*;
import net.fryc.frycmua.keybind.ModKeybinds;
import net.fryc.frycmua.util.mixin_interfaces.item.HoldsUseActions;
import net.fryc.frycmua.util.mixin_interfaces.player.SelectsUseAction;
import net.fryc.frycmua.util.mixin_interfaces.player.client.HoldsActionManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(MinecraftClient.class)
abstract class MinecraftClientMixin {

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public ClientPlayerInteractionManager interactionManager;

    @Shadow
    public @Final GameRenderer gameRenderer;

    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Shadow
    @Nullable
    public ClientWorld world;

    //targeted useKey.wasPressed():
    //
    //            while(this.options.useKey.wasPressed()) {
    //            }
    @ModifyExpressionValue(
            method = "handleInputEvents()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 11)
    )
    private boolean useKeyOrMuaKeyPressed_11thWasPressed(boolean original) {
        return original || ModKeybinds.MUA_ADDITIONAL_USE_KEYS.keySet().stream().anyMatch(KeyBinding::wasPressed);
    }

    //targeted useKey.wasPressed():
    //
    //            while(this.options.useKey.wasPressed()) {
    //                this.doItemUse();
    //            }
    @ModifyExpressionValue(
            method = "handleInputEvents()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 14)
    )
    private boolean useKeyOrMuaKeyPressed_14thWasPressed(boolean original) {
        Optional<KeyBinding> pressedKey = ModKeybinds.MUA_ADDITIONAL_USE_KEYS.keySet().stream().filter(KeyBinding::wasPressed).findFirst();

        return this.tryToUpdateCurrentActionManager(original ? MinecraftClient.getInstance().options.useKey : pressedKey.orElse(null));
    }

    //targeted useKey.isPressed():
    //
    //        if (!this.options.useKey.isPressed()) {
    //            this.interactionManager.stopUsingItem(this.player);
    //        }
    @ModifyExpressionValue(
            method = "handleInputEvents()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 2)
    )
    private boolean useKeyOrMuaKeyPressed_2thIsPressed(boolean original) {
        return original || ModKeybinds.MUA_ADDITIONAL_USE_KEYS.keySet().stream().anyMatch(KeyBinding::isPressed);
    }

    //targeted useKey.isPressed():
    //
    //        if (this.options.useKey.isPressed() && this.itemUseCooldown == 0 && !this.player.isUsingItem()) {
    //            this.doItemUse();
    //        }
    @ModifyExpressionValue(
            method = "handleInputEvents()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 3)
    )
    private boolean useKeyOrMuaKeyPressed_3thIsPressed(boolean original) {
        Optional<KeyBinding> pressedKey = ModKeybinds.MUA_ADDITIONAL_USE_KEYS.keySet().stream().filter(KeyBinding::isPressed).findFirst();

        return this.tryToUpdateCurrentActionManager(original ? MinecraftClient.getInstance().options.useKey : pressedKey.orElse(null));
    }


    @ModifyExpressionValue(
            method = "doItemUse()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isRiding()Z")
    )
    private boolean controlItemUse(boolean original) {
        if(!((HoldsActionManager) this.player).getCurrentActionManager().shouldUseVanillaSystem() && !original){
            if (this.crosshairTarget == null) {
                MultipleUseActions.LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
            }

            for(Identifier id : ((HoldsActionManager) this.player).getCurrentActionManager().getAvailableActions()){

                for(Hand hand : Hand.values()) {
                    ItemStack stack = this.player.getStackInHand(hand);

                    if (!canUseItem(stack, this.world, id)) {
                        continue;
                    }

                    UseInstance instance = ((HoldsUseActions) stack.getItem()).getUseInstance(id);

                    // checking for use
                    switch (instance) {
                        case null -> {
                            /*
                            if(id.equals(DefaultUseActions.USE_DEFAULT)) {
                                if(this.performUseAction(id, hand)) {
                                    return true;
                                }
                            }
                            else if(id.equals(DefaultUseActions.USE_ON_BLOCK_DEFAULT)) {
                                if(this.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                                    BlockHitResult blockHitResult = (BlockHitResult) this.crosshairTarget;

                                    if(this.performUseOnBlockAction(id, hand, blockHitResult, stack)) {
                                        return true;
                                    }
                                }
                            }
                            else if(id.equals(DefaultUseActions.USE_ON_ENTITY_DEFAULT)) {
                                if(this.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                                    EntityHitResult entityHitResult = (EntityHitResult) this.crosshairTarget;
                                    Entity entity = entityHitResult.getEntity();

                                    if(performUseOnEntityAction(id, hand, entityHitResult, entity)) {
                                        return true;
                                    }
                                }
                            }
                            else {
                                ((SelectsUseAction) this.player).setSelectedUseAction(DefaultUseActions.DEFAULT);
                                return false;
                            }

                             */
                            ((SelectsUseAction) this.player).setSelectedUseAction(DefaultUseActions.DEFAULT);
                            return false;
                        }
                        case ItemUseInstance useInstance -> {
                            if(useInstance.isAvailable().test(this.player, hand)) {
                                if(this.performUseAction(id, hand)) {
                                    return true;
                                }
                            }
                        }
                        case ItemUseOnBlockInstance useInstance -> {
                            if(this.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                                BlockHitResult blockHitResult = (BlockHitResult) this.crosshairTarget;

                                if(useInstance.isAvailable().test(this.player, blockHitResult, hand)) {
                                    if(this.performUseOnBlockAction(id, hand, blockHitResult, stack)) {
                                        return true;
                                    }
                                }
                            }
                        }
                        case ItemUseOnEntityInstance useInstance -> {
                            if(this.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                                EntityHitResult entityHitResult = (EntityHitResult) this.crosshairTarget;
                                Entity entity = entityHitResult.getEntity();

                                if(useInstance.isAvailable().test(this.player, entity, entityHitResult, hand)) {
                                    if(performUseOnEntityAction(id, hand, entityHitResult, entity)) {
                                        return true;
                                    }
                                }
                            }
                        }
                        default -> {}
                    }
                }
            }

            return true;
        }

        ((SelectsUseAction) this.player).setSelectedUseAction(DefaultUseActions.DEFAULT);
        return original;
    }

    private static boolean canUseItem(ItemStack stack, World world, Identifier useInstanceId){
        return stack.isItemEnabled(world.getEnabledFeatures()) && (
                ((HoldsUseActions) stack.getItem()).getUseInstance(useInstanceId) != null || DefaultUseActions.DEFAULT_IDS.contains(useInstanceId)
        );
    }

    private boolean tryToUpdateCurrentActionManager(@Nullable KeyBinding pressedKey){
        if(pressedKey != null) {
            ((HoldsActionManager) this.player).setCurrentActionManager(((HoldsActionManager) this.player).getActionManager(pressedKey));

            return true;
        }

        return false;
    }

    private boolean performUseAction(Identifier selectedAction, Hand hand) {
        ((SelectsUseAction) this.player).setSelectedUseAction(selectedAction);
        ActionResult result = this.interactionManager.interactItem(this.player, hand);
        if (result.isAccepted()) {
            if (result.shouldSwingHand()) {
                this.player.swingHand(hand);
            }
            this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);

            return true;
        }

        return false;
    }

    private boolean performUseOnBlockAction(Identifier selectedAction, Hand hand, BlockHitResult blockHitResult, ItemStack stack) {
        ((SelectsUseAction) this.player).setSelectedUseAction(selectedAction);
        int i = stack.getCount();
        ActionResult actionResult2 = this.interactionManager.interactBlock(this.player, hand, blockHitResult);
        if (actionResult2.isAccepted()) {
            if (actionResult2.shouldSwingHand()) {
                this.player.swingHand(hand);
                if (!stack.isEmpty() && (stack.getCount() != i || this.interactionManager.hasCreativeInventory())) {
                    this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                }
            }

            return true;
        }

        return false;
    }

    private boolean performUseOnEntityAction(Identifier selectedAction, Hand hand, EntityHitResult entityHitResult, Entity entity) {
        ((SelectsUseAction) this.player).setSelectedUseAction(selectedAction);
        if (!this.world.getWorldBorder().contains(entity.getBlockPos())) {
            return true;
        }

        ActionResult actionResult = this.interactionManager.interactEntityAtLocation(this.player, entity, entityHitResult, hand);
        if (!actionResult.isAccepted()) {
            actionResult = this.interactionManager.interactEntity(this.player, entity, hand);
        }

        if (actionResult.isAccepted()) {
            if (actionResult.shouldSwingHand()) {
                this.player.swingHand(hand);
            }

            return true;
        }

        return false;
    }

}
