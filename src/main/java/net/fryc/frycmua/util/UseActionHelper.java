package net.fryc.frycmua.util;

import net.fryc.frycmua.item.action.DefaultUseActions;
import net.fryc.frycmua.item.action.registry.UseActionRegistries;
import net.minecraft.util.Identifier;

public class UseActionHelper {

    public static boolean shouldUseSelectedUseAction(Identifier selectedAction){
        return selectedAction != null && !DefaultUseActions.DEFAULT_IDS.contains(selectedAction);
    }

    public static boolean useActionExists(Identifier useAction) {
        if(useAction == null) return false;

        return DefaultUseActions.DEFAULT_IDS.contains(useAction) ||
                UseActionRegistries.REGISTERED_USE_ACTIONS.containsKey(useAction) ||
                UseActionRegistries.REGISTERED_USE_ON_BLOCK_ACTIONS.containsKey(useAction) ||
                UseActionRegistries.REGISTERED_USE_ON_ENTITY_ACTIONS.containsKey(useAction);
    }
}
