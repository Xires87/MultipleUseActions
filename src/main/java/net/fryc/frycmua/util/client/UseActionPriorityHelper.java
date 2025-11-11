package net.fryc.frycmua.util.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fryc.frycmua.MultipleUseActionsClient;
import net.fryc.frycmua.item.action.DefaultUseActions;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class UseActionPriorityHelper {


    public static int getUseActionPriority(Identifier id) {
        if(MultipleUseActionsClient.USE_ACTION_PRIORITIES.containsKey(id)){
            return MultipleUseActionsClient.USE_ACTION_PRIORITIES.get(id);
        }

        return 0;
    }

    public static void initUseActionPriorities() {
        // TODO odczyt i zapis do pliku (i nie wiem jak z tym defaultem zrobic, czy 3 defaulty czy 1 ktory oddaje sterowanie vanilli
        MultipleUseActionsClient.USE_ACTION_PRIORITIES.put(DefaultUseActions.USE_DEFAULT, 50);
        MultipleUseActionsClient.USE_ACTION_PRIORITIES.put(Identifier.ofVanilla("bow_pull"), 20);// TODO priority jest odwrotnie niz chcialem (luk tera ma wiekszy priorytet)
    }
}
