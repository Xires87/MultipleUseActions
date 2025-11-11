package net.fryc.frycmua.mixin.client;

import com.mojang.authlib.GameProfile;
import net.fryc.frycmua.item.action.client.ActionManager;
import net.fryc.frycmua.item.action.client.ActionPriority;
import net.fryc.frycmua.util.interfaces.HoldsActionManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.StatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements HoldsActionManager {

    private ActionManager actionManager;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/ClientPlayNetworkHandler;Lnet/minecraft/stat/StatHandler;Lnet/minecraft/client/recipebook/ClientRecipeBook;ZZ)V", at = @At("TAIL"))
    private void initActionManager(MinecraftClient client, ClientWorld world, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting, CallbackInfo info){
        this.actionManager = new ActionManager(client.options.useKey, ActionPriority.MIXED, ((ClientPlayerEntity)(Object)this));
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void updateAvailableActionsEveryTick(CallbackInfo info){
        this.actionManager.updateAvailableActions();
    }


    public ActionManager getActionManager(){
        return this.actionManager;
    }
}
