package net.fryc.frycmua.item.action;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class DefaultUseActions {

    public static final ItemUseInstance BOW_PULL = new ItemUseInstance(
            BowItem.class,
            true,
            DefaultUseActions::bowPullUseItem,
            DefaultUseActions::bowPullItemUsageTick,
            DefaultUseActions::bowPullOnStoppedUsingItem,
            DefaultUseActions::bowPullFinishUsingItem,
            DefaultUseActions::bowPullGetMaxItemUseTime,
            DefaultUseActions::bowPullGetItemUseAction
    );

    private static TypedActionResult<ItemStack> bowPullUseItem(Item item, World world, PlayerEntity user, Hand hand) {
        return item.use(world, user, hand);
    }

    private static ItemStack bowPullFinishUsingItem(ItemStack stack, World world, LivingEntity user) {
        return stack.finishUsing(world, user);
    }

    private static void bowPullOnStoppedUsingItem(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        stack.onStoppedUsing(world, user, remainingUseTicks);
    }

    private static void bowPullItemUsageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        stack.usageTick(world, user, remainingUseTicks);
    }

    private static int bowPullGetMaxItemUseTime(ItemStack stack, LivingEntity user) {
        return stack.getMaxUseTime(user);
    }

    private static UseAction bowPullGetItemUseAction(ItemStack stack) {
        return stack.getUseAction();
    }
}
