package net.fryc.frycmua.compatibility;

import net.fabricmc.loader.api.FabricLoader;
import net.fryc.frycmua.MultipleUseActions;
import net.fryc.frycmua.action.ItemUseInstance;
import net.fryc.frycmua.action.registry.UseActionRegistries;
import net.fryc.frycmua.util.mixin_interfaces.player.SelectsUseAction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class ItemPhysicCompatibility {

    private static final String ITEM_PHYSIC_MOD_ID = "itemphysic";

    public static boolean IS_ITEM_PHYSIC_LOADED = FabricLoader.getInstance().isModLoaded(ITEM_PHYSIC_MOD_ID);

    public static Class<?> ITEM_PHYSIC_CLIENT_CLASS = IS_ITEM_PHYSIC_LOADED ? getClassForName("team.creative.itemphysic.client.ItemPhysicClient") : null;

    public static Method INTERACT_METHOD = ITEM_PHYSIC_CLIENT_CLASS != null ? getMethodForName(ITEM_PHYSIC_CLIENT_CLASS, "onPlayerInteract", PlayerEntity.class) : null;


    public static void registerItemPickupAction() {
        if(IS_ITEM_PHYSIC_LOADED) {
            ItemUseInstance ITEM_PICKUP_USE_INSTANCE = new ItemUseInstance(
                    Item.class /* <-- targeted class */,
                    true /* <-- allow subclasses */,
                    (item, world, user, hand) -> {
                        // Item#use equivalent
                        return TypedActionResult.success(user.getStackInHand(Hand.MAIN_HAND));
                    },
                    (world, user, stack, remainingUseTicks) -> {
                        // Item#tickUsage equivalent
                    },
                    (stack, world, user, remainingUseTicks) -> {
                        // Item#onStoppedUsing equivalent
                    },
                    (stack, world, user) -> {
                        // Item#finishUsing equivalent
                        return stack;
                    },
                    (stack, user) -> {
                        // Item#getMaxUseTime equivalent
                        return 0;
                    },
                    stack -> {
                        // Item#getUseAction equivalent
                        return UseAction.NONE;
                    },
                    stack -> {
                        // Item#isUsedOnRelease equivalent
                        return false;
                    },
                    (player, hand) -> {
                        // this predicate is run on client side before launching the action
                        // the action will be skipped if it returns false
                        if(player.getWorld().isClient()) {
                            if(INTERACT_METHOD != null) {
                                try {
                                    boolean bl = (boolean) INTERACT_METHOD.invoke(null, player);
                                    if(bl) {
                                        ((SelectsUseAction) player).setShouldItemUsageBeCancelled(true);
                                    }

                                    return bl;
                                } catch (Exception e) {
                                    MultipleUseActions.LOGGER.error("ItemPhysic compatibility failed!");
                                    MultipleUseActions.LOGGER.error("An error occurred while trying to execute 'team.creative.itemphysic.client.ItemPhysicClient#onPlayerInteract' method!", e);
                                }
                            }
                        }

                        return false;
                    }
            );

            UseActionRegistries.registerUseAction(Identifier.of(ITEM_PHYSIC_MOD_ID, "item_pickup"), ITEM_PICKUP_USE_INSTANCE);
        }
    }

    private static @Nullable Class<?> getClassForName(String name) {
        try {
            return Class.forName(name);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static @Nullable Method getMethodForName(Class<?> clazz, String name, Class<?>... parameters){
        try{
            return clazz.getMethod(name, parameters);
        } catch (Exception e) {
            return null;
        }
    }
}
