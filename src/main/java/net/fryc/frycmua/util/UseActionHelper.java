package net.fryc.frycmua.util;

import net.fryc.frycmua.item.action.DefaultUseActions;
import net.minecraft.util.Identifier;

public class UseActionHelper {

    public static boolean shouldUseSelectedUseAction(Identifier selectedAction){
        return !DefaultUseActions.DEFAULT_IDS.contains(selectedAction);
    }
}
