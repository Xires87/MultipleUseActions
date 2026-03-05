package net.fryc.frycmua.mixin.client;

import com.mojang.authlib.GameProfile;
import net.fryc.frycmua.item.action.client.ActionManager;
import net.fryc.frycmua.item.action.client.ActionPriority;
import net.fryc.frycmua.item.action.client.PriorityManager;
import net.fryc.frycmua.keybind.ModKeybinds;
import net.fryc.frycmua.util.interfaces.player.client.HoldsActionManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.StatHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ClientPlayerEntity.class)
abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements HoldsActionManager {

    private ActionManager currentActionManager;
    private final Map<KeyBinding, ActionManager> actionManagers = new HashMap<>();

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/ClientPlayNetworkHandler;Lnet/minecraft/stat/StatHandler;Lnet/minecraft/client/recipebook/ClientRecipeBook;ZZ)V", at = @At("TAIL"))
    private void initActionManager(MinecraftClient client, ClientWorld world, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting, CallbackInfo info){
        ActionManager defaultManager = new ActionManager(client.options.useKey, new PriorityManager(ActionPriority.DEFAULT), ((ClientPlayerEntity)(Object)this));
        this.currentActionManager = defaultManager;
        this.actionManagers.put(client.options.useKey, defaultManager);
        ModKeybinds.MUA_ADDITIONAL_USE_KEYS.forEach((key, pManager) -> {
            this.actionManagers.put(key, new ActionManager(key, pManager, ((ClientPlayerEntity)(Object)this)));
        });
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void updateAvailableActionsEveryTick(CallbackInfo info){
        this.getAllActionManagers().forEach(ActionManager::updateAvailableActions);
    }


    public ActionManager getCurrentActionManager(){
        return this.currentActionManager;
    }

    public void setCurrentActionManager(ActionManager manager){
        if(!this.isUsingItem()){
            this.currentActionManager = manager;
        }
    }

    public List<ActionManager> getAllActionManagers(){
        return this.actionManagers.values().stream().toList();
    }

    @Nullable
    public ActionManager getActionManager(KeyBinding key){
        return this.actionManagers.get(key);
    }
}
