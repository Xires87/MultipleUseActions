package net.fryc.frycmua.mixin;


import net.fryc.frycmua.item.action.SelectsUseAction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin implements SelectsUseAction {

    private Identifier selectedUseAction = null;

    @Override
    @Nullable
    public Identifier getSelectedUseAction() {
        return this.selectedUseAction;
    }

    @Override
    public void setSelectedUseAction(@Nullable Identifier id) {
        this.selectedUseAction = id;
    }
}
