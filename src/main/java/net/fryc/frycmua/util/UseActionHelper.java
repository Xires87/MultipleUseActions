package net.fryc.frycmua.util;

import net.fryc.frycmua.item.action.DefaultUseActions;
import net.minecraft.util.Identifier;

public class UseActionHelper {

    public static boolean shouldUseSelectedUseAction(Identifier selectedAction){
        return !selectedAction.toString().equals(DefaultUseActions.USE_DEFAULT.toString()) &&
                !selectedAction.toString().equals(DefaultUseActions.USE_ON_BLOCK_DEFAULT.toString()) &&
                !selectedAction.toString().equals(DefaultUseActions.USE_ON_ENTITY_DEFAULT.toString()) &&
                !selectedAction.toString().equals(DefaultUseActions.DEFAULT.toString());
    }
}
