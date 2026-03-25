package net.fryc.frycmua.util.interfaces.player.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fryc.frycmua.item.action.client.ActionManager;
import net.minecraft.client.option.KeyBinding;

import java.util.List;

// for ClientPlayerEntity
@Environment(EnvType.CLIENT)
public interface HoldsActionManager {

    ActionManager getCurrentActionManager();

    void setCurrentActionManager(ActionManager manager);

    ActionManager getActionManager(KeyBinding key);

    List<ActionManager> getAllActionManagers();
}
