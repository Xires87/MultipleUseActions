package net.fryc.frycmua.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fryc.frycmua.MultipleUseActions;
import net.fryc.frycmua.item.action.*;
import net.fryc.frycmua.util.interfaces.player.client.HoldsActionManager;
import net.fryc.frycmua.util.interfaces.item.HoldsUseActions;
import net.fryc.frycmua.util.interfaces.player.SelectsUseAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
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



    @ModifyExpressionValue(
            method = "doItemUse()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isRiding()Z")
    )
    private boolean controlItemUse(boolean original) {
        if(!original){
            if (this.crosshairTarget == null) {
                MultipleUseActions.LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
            }

            for(Identifier id : ((HoldsActionManager) this.player).getActionManager().getAvailableActions()){
                if(id.toString().equals(DefaultUseActions.USE_DEFAULT.toString())){
                    ((SelectsUseAction) this.player).setSelectedUseAction(id);// TODO rozroznianie defaultowych akcji (block i entity powinno puszczac, use powinno odpalac)
                    break;
                }

                Hand hand = Hand.MAIN_HAND;// TODO wybieranie priorytetowej reki
                ItemStack stack = this.player.getStackInHand(hand);

                if (!canUseItem(stack, this.world, id)) {
                    hand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
                    stack = this.player.getStackInHand(hand);
                    if(!canUseItem(stack, this.world, id)){
                        return false;
                    }
                }

                UseInstance instance = ((HoldsUseActions) stack.getItem()).getUseInstance(id);

                // checking for use
                if(instance instanceof ItemUseInstance useInstance){
                    if(useInstance.isAvailable().test(this.player, hand)){
                        ((SelectsUseAction) this.player).setSelectedUseAction(id);
                        ActionResult result = this.interactionManager.interactItem(this.player, hand);
                        if (result.isAccepted()) {
                            if (result.shouldSwingHand()) {
                                this.player.swingHand(hand);
                            }
                            this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);

                            return true;
                        }

                        //return true;// TODO musze returnowac tylko w przypoadku jak action result jest success albo fail, na passie musze leciec dalej petla
                    }
                }
                else if(instance instanceof ItemUseOnBlockInstance useInstance){
                    if(this.crosshairTarget.getType() == HitResult.Type.BLOCK){
                        // TODO jesli chodzi o interakcje z blokami to bedzie do wyboru: useOnBlock, interactWithBlock i interactWithBlockUsingItem
                        BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
                        if(useInstance.isAvailable().test(this.player, blockHitResult, hand)){
                            ((SelectsUseAction) this.player).setSelectedUseAction(id);
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

                            if (actionResult2 == ActionResult.FAIL) {
                                return true;
                            }
                        }
                    }
                }
                else if(instance instanceof ItemUseOnEntityInstance useInstance){
                    if(this.crosshairTarget.getType() == HitResult.Type.ENTITY){
                        EntityHitResult entityHitResult = (EntityHitResult)this.crosshairTarget;
                        Entity entity = entityHitResult.getEntity();
                        if(useInstance.isAvailable().test(this.player, entity, entityHitResult, hand)){
                            ((SelectsUseAction) this.player).setSelectedUseAction(id);
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
                        }
                    }
                }
            }
        }

        ((SelectsUseAction) this.player).setSelectedUseAction(DefaultUseActions.DEFAULT);
        return original;
    }

    private static boolean canUseItem(ItemStack stack, World world, Identifier useInstanceId){
        return stack.isItemEnabled(world.getEnabledFeatures()) && ((HoldsUseActions) stack.getItem()).getUseInstance(useInstanceId) != null;
    }

}
