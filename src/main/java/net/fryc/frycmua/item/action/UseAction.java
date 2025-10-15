package net.fryc.frycmua.item.action;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface UseAction {
// TODO jeszcze finishUsing musze tu wsadzic i cos tam na tick usage i maxusetime itd
    TypedActionResult<ItemStack> muaUseItem(@Nullable Identifier selectedAction, World world, PlayerEntity user, Hand hand);

    ActionResult muaUseItemOnBlock(@Nullable Identifier selectedAction, ItemUsageContext context);

    ActionResult muaUseItemOnEntity(@Nullable Identifier selectedAction, ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand);
}
