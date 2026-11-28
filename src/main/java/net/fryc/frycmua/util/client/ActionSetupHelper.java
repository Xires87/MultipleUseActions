package net.fryc.frycmua.util.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fryc.frycmua.MultipleUseActions;
import net.fryc.frycmua.action.client.ActionDemand;
import net.fryc.frycmua.action.client.ActionPriority;
import net.fryc.frycmua.action.client.PriorityManager;
import net.fryc.frycmua.keybind.ModKeybinds;
import net.fryc.frycmua.util.UseActionHelper;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ActionSetupHelper {

    public static final ArrayList<Identifier> ACTIONS_TO_SETUP = new ArrayList<>();
    private static final List<Identifier> IGNORED_ACTIONS = FileHelper.getIgnoredUseActions();

    public static Map<Identifier, List<String>> getActionsThatRequireSetup() {
        Map<Identifier, List<String>> requiredActions = new HashMap<>();

        ActionDemand.getDemandedActions().forEach((id, list) -> {
            if(UseActionHelper.useActionExists(id)){
                if(ModKeybinds.MUA_ADDITIONAL_USE_KEYS.values().stream().noneMatch(priorityManager -> {
                    PriorityManager.Entry entry = priorityManager.getEntryById(id);
                    return entry != null && (
                            (entry.isActive() && priorityManager.getActionPriority().equals(ActionPriority.MIXED)) ||
                                    (entry.isSwitchable() && priorityManager.isSwitchable())
                    );
                })) {
                    if(!IGNORED_ACTIONS.contains(id)) {
                        requiredActions.put(id, list);
                    }
                }

                PriorityManager.Entry entry = PriorityManager.DEFAULT_PRIORITY_MANAGER.getEntryById(id);
                if(entry != null && (
                        (entry.isActive() && PriorityManager.DEFAULT_PRIORITY_MANAGER.getActionPriority().equals(ActionPriority.MIXED)) ||
                                (entry.isSwitchable() && PriorityManager.DEFAULT_PRIORITY_MANAGER.isSwitchable())
                )) {
                    if(!IGNORED_ACTIONS.contains(id)) {
                        requiredActions.put(id, list);
                    }
                }
            }
            else {
                MultipleUseActions.LOGGER.error("The following mods demand non-existing '" + id + "' use action: " + list.stream().reduce((s1, s2) -> {
                    return s1 + ", " + s2;
                }).orElse("null"));
            }
        });

        return Map.copyOf(requiredActions);
    }
}
