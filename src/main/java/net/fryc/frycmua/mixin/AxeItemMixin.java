package net.fryc.frycmua.mixin;

import net.fryc.frycmua.util.mixin_interfaces.item.AxeItemAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Optional;

@Mixin(AxeItem.class)
abstract class AxeItemMixin implements AxeItemAccessor {

    @Shadow
    protected static @Final Map<Block, Block> STRIPPED_BLOCKS;

    @Shadow
    abstract Optional<BlockState> tryStrip(World world, BlockPos pos, @Nullable PlayerEntity player, BlockState state);

    public Optional<BlockState> frycmua$tryStrip(World world, BlockPos pos, @Nullable PlayerEntity player, BlockState state) {
        return this.tryStrip(world, pos, player, state);
    }

    public Map<Block, Block> frycmua$getStrippedLogsMap() {
        return STRIPPED_BLOCKS;
    }
}
