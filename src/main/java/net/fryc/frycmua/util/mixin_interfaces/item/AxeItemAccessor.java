package net.fryc.frycmua.util.mixin_interfaces.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public interface AxeItemAccessor {

    Optional<BlockState> frycmua$tryStrip(World world, BlockPos pos, @Nullable PlayerEntity player, BlockState state);

    Map<Block, Block> frycmua$getStrippedLogsMap();
}
