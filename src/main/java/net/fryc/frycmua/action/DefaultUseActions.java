package net.fryc.frycmua.action;

import com.google.common.collect.BiMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fryc.frycmua.MultipleUseActions;
import net.fryc.frycmua.network.payload.CancelInteractionPayload;
import net.fryc.frycmua.util.mixin_interfaces.item.AxeItemAccessor;
import net.fryc.frycmua.util.mixin_interfaces.player.SelectsUseAction;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Optional;
import java.util.Set;

public class DefaultUseActions {

    public static final Identifier DEFAULT = Identifier.ofVanilla("default");
    public static final Identifier USE_DEFAULT = Identifier.ofVanilla("use_default");
    public static final Identifier USE_ON_BLOCK_DEFAULT = Identifier.ofVanilla("use_on_block_default");
    public static final Identifier USE_ON_ENTITY_DEFAULT = Identifier.ofVanilla("use_on_entity_default");

    @Deprecated
    public static final Set<Identifier> DEFAULT_IDS = Set.of(DEFAULT/*, USE_DEFAULT, USE_ON_BLOCK_DEFAULT, USE_ON_ENTITY_DEFAULT*/);

    public static final ItemUseOnEntityInstance USE_ON_ENTITY_DEFAULT_INSTANCE = new ItemUseOnEntityInstance(
            Item.class,
            true,
            DefaultUseActions::defaultUseOnEntity,
            DefaultUseActions::defaultItemUsageTick,
            DefaultUseActions::defaultOnStoppedUsingItem,
            DefaultUseActions::defaultFinishUsingItem,
            DefaultUseActions::defaultGetMaxItemUseTime,
            DefaultUseActions::defaultGetItemUseAction,
            DefaultUseActions::defaultIsUsedOnRelease,
            (player, entity, entityHitResult, hand) -> true
    );

    public static final ItemUseOnBlockInstance USE_ON_BLOCK_DEFAULT_INSTANCE = new ItemUseOnBlockInstance(
            Item.class,
            true,
            DefaultUseActions::defaultUseOnBlock,
            DefaultUseActions::defaultItemUsageTick,
            DefaultUseActions::defaultOnStoppedUsingItem,
            DefaultUseActions::defaultFinishUsingItem,
            DefaultUseActions::defaultGetMaxItemUseTime,
            DefaultUseActions::defaultGetItemUseAction,
            DefaultUseActions::defaultIsUsedOnRelease,
            (player, blockHitResult, hand) -> true
    );

    public static final ItemUseInstance USE_DEFAULT_INSTANCE = new ItemUseInstance(
            Item.class,
            true,
            DefaultUseActions::defaultUseItem,
            DefaultUseActions::defaultItemUsageTick,
            DefaultUseActions::defaultOnStoppedUsingItem,
            DefaultUseActions::defaultFinishUsingItem,
            DefaultUseActions::defaultGetMaxItemUseTime,
            DefaultUseActions::defaultGetItemUseAction,
            DefaultUseActions::defaultIsUsedOnRelease,
            (player, hand) -> true
    );

    public static final ItemUseOnBlockInstance BLOCK_STRIP = new ItemUseOnBlockInstance(
            AxeItem.class,
            true,
            context -> {
                try {
                    // useOnBlock = method_7884
                    if(context.getStack().getItem().getClass().getMethod("method_7884", ItemUsageContext.class).getDeclaringClass().equals(AxeItem.class)) {
                        World world = context.getWorld();
                        BlockPos blockPos = context.getBlockPos();
                        PlayerEntity playerEntity = context.getPlayer();
                        Optional<BlockState> optional = ((AxeItemAccessor) context.getStack().getItem()).frycmua$tryStrip(world, blockPos, playerEntity, world.getBlockState(blockPos));
                        if (optional.isEmpty()) {
                            return ActionResult.PASS;
                        } else {
                            ItemStack itemStack = context.getStack();
                            if (playerEntity instanceof ServerPlayerEntity) {
                                Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
                            }

                            world.setBlockState(blockPos, (BlockState)optional.get(), 11);
                            world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, (BlockState)optional.get()));
                            if (playerEntity != null) {
                                itemStack.damage(1, playerEntity, LivingEntity.getSlotForHand(context.getHand()));
                            }

                            return ActionResult.success(world.isClient);
                        }
                    }
                } catch (NoSuchMethodException e) {
                    MultipleUseActions.LOGGER.error("Failed to get useOnBlock method", e);
                }

                return DefaultUseActions.defaultUseOnBlock(context);
            },
            DefaultUseActions::defaultItemUsageTick,
            DefaultUseActions::defaultOnStoppedUsingItem,
            DefaultUseActions::defaultFinishUsingItem,
            DefaultUseActions::defaultGetMaxItemUseTime,
            DefaultUseActions::defaultGetItemUseAction,
            DefaultUseActions::defaultIsUsedOnRelease,
            (player, blockHitResult, hand) -> {
                if(!(player.getStackInHand(hand).getItem() instanceof AxeItem)) return false;

                BlockState state = player.getWorld().getBlockState(blockHitResult.getBlockPos());
                boolean isLog = ((AxeItemAccessor) player.getStackInHand(hand).getItem()).frycmua$getStrippedLogsMap().containsKey(state.getBlock());

                return isLog || Oxidizable.getDecreasedOxidationState(state).isPresent() || Optional.ofNullable(
                        (Block)((BiMap<?, ?>)HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get()).get(state.getBlock())
                ).map((block) -> block.getStateWithProperties(state)).isPresent();
            }
    );

    public static final ItemUseOnBlockInstance BLOCK_PLACE = new ItemUseOnBlockInstance(
            BlockItem.class,
            true,
            context -> {
                ActionResult result = ((BlockItem) context.getStack().getItem()).place(new ItemPlacementContext(context));
                ((SelectsUseAction) context.getPlayer()).setShouldBlockInteractionBeCancelled(false);
                return result;
            },
            DefaultUseActions::defaultItemUsageTick,
            DefaultUseActions::defaultOnStoppedUsingItem,
            DefaultUseActions::defaultFinishUsingItem,
            DefaultUseActions::defaultGetMaxItemUseTime,
            DefaultUseActions::defaultGetItemUseAction,
            DefaultUseActions::defaultIsUsedOnRelease,
            (player, blockHitResult, hand) -> {
                if(player != null && player.getWorld().isClient()) {
                    ((SelectsUseAction) player).setShouldBlockInteractionBeCancelled(true);
                    ClientPlayNetworking.send(new CancelInteractionPayload(true));
                    return true;
                }

                return false;
            }
    );

    public static final ItemUseInstance BOW_PULL = new ItemUseInstance(
            BowItem.class,
            true,
            DefaultUseActions::defaultUseItem,
            DefaultUseActions::defaultItemUsageTick,
            DefaultUseActions::defaultOnStoppedUsingItem,
            DefaultUseActions::defaultFinishUsingItem,
            DefaultUseActions::defaultGetMaxItemUseTime,
            DefaultUseActions::defaultGetItemUseAction,
            DefaultUseActions::defaultIsUsedOnRelease,
            (player, hand) -> !player.getProjectileType(player.getStackInHand(hand)).isEmpty()
    );

    public static final ItemUseInstance CROSSBOW_RELOAD = new ItemUseInstance(
            CrossbowItem.class,
            true,
            DefaultUseActions::defaultUseItem,
            DefaultUseActions::defaultItemUsageTick,
            DefaultUseActions::defaultOnStoppedUsingItem,
            DefaultUseActions::defaultFinishUsingItem,
            DefaultUseActions::defaultGetMaxItemUseTime,
            DefaultUseActions::defaultGetItemUseAction,
            DefaultUseActions::defaultIsUsedOnRelease,
            (player, hand) -> {
                return !player.getProjectileType(player.getStackInHand(hand)).isEmpty() && !CrossbowItem.isCharged(player.getStackInHand(hand));
            }
    );

    public static final ItemUseInstance CROSSBOW_SHOOT = new ItemUseInstance(
            CrossbowItem.class,
            true,
            DefaultUseActions::defaultUseItem,
            DefaultUseActions::defaultItemUsageTick,
            DefaultUseActions::defaultOnStoppedUsingItem,
            DefaultUseActions::defaultFinishUsingItem,
            DefaultUseActions::defaultGetMaxItemUseTime,
            DefaultUseActions::defaultGetItemUseAction,
            DefaultUseActions::defaultIsUsedOnRelease,
            (player, hand) -> CrossbowItem.isCharged(player.getStackInHand(hand))
    );

    public static final ItemUseInstance FOOD_EAT = new ItemUseInstance(
            Item.class,
            true,
            DefaultUseActions::defaultUseItem,
            DefaultUseActions::defaultItemUsageTick,
            DefaultUseActions::defaultOnStoppedUsingItem,
            DefaultUseActions::defaultFinishUsingItem,
            DefaultUseActions::defaultGetMaxItemUseTime,
            DefaultUseActions::defaultGetItemUseAction,
            DefaultUseActions::defaultIsUsedOnRelease,
            (player, hand) -> {
                FoodComponent foodComponent = player.getStackInHand(hand).get(DataComponentTypes.FOOD);
                return foodComponent != null && player.canConsume(foodComponent.canAlwaysEat());
            }
    );

    public static final ItemUseInstance BLOCK = new ItemUseInstance(
            ShieldItem.class,
            true,
            DefaultUseActions::defaultUseItem,
            DefaultUseActions::defaultItemUsageTick,
            DefaultUseActions::defaultOnStoppedUsingItem,
            DefaultUseActions::defaultFinishUsingItem,
            DefaultUseActions::defaultGetMaxItemUseTime,
            DefaultUseActions::defaultGetItemUseAction,
            DefaultUseActions::defaultIsUsedOnRelease,
            (player, hand) -> player.getStackInHand(hand).getItem() instanceof ShieldItem
    );

    private static TypedActionResult<ItemStack> defaultUseItem(Item item, World world, PlayerEntity user, Hand hand) {
        return item.use(world, user, hand);
    }

    private static ActionResult defaultUseOnBlock(ItemUsageContext context) {
        return context.getStack().getItem().useOnBlock(context);
    }

    private static ActionResult defaultUseOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        return stack.getItem().useOnEntity(stack, user, entity, hand);
    }

    private static ItemStack defaultFinishUsingItem(ItemStack stack, World world, LivingEntity user) {
        return stack.getItem().finishUsing(stack, world, user);
    }

    private static void defaultOnStoppedUsingItem(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        stack.getItem().onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    private static void defaultItemUsageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        stack.getItem().usageTick(world, user, stack, remainingUseTicks);
    }

    private static int defaultGetMaxItemUseTime(ItemStack stack, LivingEntity user) {
        return stack.getItem().getMaxUseTime(stack, user);
    }

    private static UseAction defaultGetItemUseAction(ItemStack stack) {
        return stack.getItem().getUseAction(stack);
    }

    private static boolean defaultIsUsedOnRelease(ItemStack stack) {
        return stack.getItem().isUsedOnRelease(stack);
    }
}
