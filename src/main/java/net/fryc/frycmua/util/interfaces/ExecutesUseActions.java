package net.fryc.frycmua.util.interfaces;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public interface ExecutesUseActions {
// TODO jeszcze finishUsing musze tu wsadzic i cos tam na tick usage i maxusetime itd
    TypedActionResult<ItemStack> muaUseItem(Identifier selectedAction, World world, PlayerEntity user, Hand hand);

    ActionResult muaUseItemOnBlock(Identifier selectedAction, ItemUsageContext context);

    ActionResult muaUseItemOnEntity(Identifier selectedAction, ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand);
}
