package net.fryc.frycmua.mixin;


import net.fryc.frycmua.item.action.DefaultUseActions;
import net.fryc.frycmua.util.interfaces.SelectsUseAction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin implements SelectsUseAction {

    private Identifier selectedUseAction = DefaultUseActions.USE_DEFAULT;

    @Override
    public Identifier getSelectedUseAction() {
        return this.selectedUseAction;
    }

    @Override
    public void setSelectedUseAction(@Nullable Identifier id) {
        this.selectedUseAction = id;
    }
}
