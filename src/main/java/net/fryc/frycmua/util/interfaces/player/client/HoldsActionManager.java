package net.fryc.frycmua.util.interfaces.player.client;

import net.fryc.frycmua.item.action.client.ActionManager;
import net.minecraft.client.option.KeyBinding;

import java.util.List;

// for ClientPlayerEntity
public interface HoldsActionManager {

    ActionManager getCurrentActionManager();

    void setCurrentActionManager(ActionManager manager);

    ActionManager getActionManager(KeyBinding key);

    List<ActionManager> getAllActionManagers();
}
