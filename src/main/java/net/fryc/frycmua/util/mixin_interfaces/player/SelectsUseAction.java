package net.fryc.frycmua.util.mixin_interfaces.player;

import net.fryc.frycmua.action.UseInstance;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

// for LivingEntity
public interface SelectsUseAction {

    Identifier getSelectedUseAction();

    void setSelectedUseAction(Identifier id);

    @Nullable UseInstance getActiveUseInstance();

    void setActiveUseInstance(@Nullable UseInstance useInstance);
}
