package net.fryc.frycmua.item.action;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.Set;

public class DefaultUseActions {

    public static final Identifier DEFAULT = Identifier.ofVanilla("default");
    public static final Identifier USE_DEFAULT = Identifier.ofVanilla("use_default");
    public static final Identifier USE_ON_BLOCK_DEFAULT = Identifier.ofVanilla("use_on_block_default");
    public static final Identifier USE_ON_ENTITY_DEFAULT = Identifier.ofVanilla("use_on_entity_default");

    public static final Set<Identifier> DEFAULT_IDS = Set.of(DEFAULT, USE_DEFAULT, USE_ON_BLOCK_DEFAULT, USE_ON_ENTITY_DEFAULT);

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

    private static TypedActionResult<ItemStack> defaultUseItem(Item item, World world, PlayerEntity user, Hand hand) {
        return item.use(world, user, hand);
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
        return stack.getItem().getMaxUseTime(stack, user);// TODO posprawdzac loggerami czy wszystko jest zsynchronizowane
    }

    private static UseAction defaultGetItemUseAction(ItemStack stack) {
        //return stack.getItem().getUseAction(stack);
        return UseAction.BRUSH;// TODO nie dziala to
    }

    private static boolean defaultIsUsedOnRelease(ItemStack stack) {
        return stack.getItem().isUsedOnRelease(stack);
    }
}
