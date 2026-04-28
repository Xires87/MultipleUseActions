package net.fryc.frycmua.mixin;

import net.fryc.frycmua.item.action.registry.UseActionRegistries;
import net.fryc.frycmua.util.interfaces.item.UseActionRegistry;
import net.fryc.frycmua.util.interfaces.registry.FrozenCheckable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.SimpleRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleRegistry.class)
abstract class SimpleRegistryMixin implements FrozenCheckable {

    @Shadow
    private boolean frozen;

    @Shadow
    abstract void assertNotFrozen();

    @Inject(method = "freeze()Lnet/minecraft/registry/Registry;", at = @At("TAIL"))
    private void addRegisteredUseActionsToItems(CallbackInfoReturnable<Registry<?>> ret) {
        if(this.equals(Registries.ITEM)){
            Registries.ITEM.forEach(item -> {
                UseActionRegistries.getRegisteredUseActions().forEach((key, value) -> {
                    value.stream().filter(useInstance -> {
                        return useInstance.getClassLevel() == item.getClass() || (useInstance.isAllowSubclasses() && useInstance.getClassLevel().isAssignableFrom(item.getClass()));
                    }).forEach(useInstance -> {
                        ((UseActionRegistry) item).addUseAction(key, useInstance);
                    });
                });

                UseActionRegistries.getRegisteredUseOnBlockActions().forEach((key, value) -> {
                    value.stream().filter(useInstance -> {
                        return useInstance.getClassLevel() == item.getClass() || (useInstance.isAllowSubclasses() && useInstance.getClassLevel().isAssignableFrom(item.getClass()));
                    }).forEach(useInstance -> {
                        ((UseActionRegistry) item).addUseOnBlockAction(key, useInstance);
                    });
                });

                UseActionRegistries.getRegisteredUseOnEntityActions().forEach((key, value) -> {
                    value.stream().filter(useInstance -> {
                        return useInstance.getClassLevel() == item.getClass() || (useInstance.isAllowSubclasses() && useInstance.getClassLevel().isAssignableFrom(item.getClass()));
                    }).forEach(useInstance -> {
                        ((UseActionRegistry) item).addUseOnEntityAction(key, useInstance);
                    });
                });
            });
        }
    }

    public boolean isAlreadyFrozen() {
        return this.frozen;
    }

    public void throwIfFrozen() {
        this.assertNotFrozen();
    }
}
