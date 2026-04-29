package net.fryc.frycmua.util;

import net.fryc.frycmua.action.DefaultUseActions;
import net.fryc.frycmua.action.registry.UseActionRegistries;
import net.minecraft.util.Identifier;

public class UseActionHelper {

    public static boolean shouldUseSelectedUseAction(Identifier selectedAction){
        return selectedAction != null && !DefaultUseActions.DEFAULT_IDS.contains(selectedAction);
    }

    public static boolean useActionExists(Identifier useAction) {
        if(useAction == null) return false;

        return DefaultUseActions.DEFAULT_IDS.contains(useAction) ||
                UseActionRegistries.getRegisteredUseActions().containsKey(useAction) ||
                UseActionRegistries.getRegisteredUseOnBlockActions().containsKey(useAction) ||
                UseActionRegistries.getRegisteredUseOnEntityActions().containsKey(useAction);
    }
}
