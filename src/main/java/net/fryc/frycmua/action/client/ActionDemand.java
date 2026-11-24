package net.fryc.frycmua.action.client;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ActionDemand {

    private static final Map<Identifier, List<String>> ACTIONS_REQUIRE_ALLOCATION = new HashMap<>();

    /**
     * (Client sided - call inside {@link ClientModInitializer}) Informs player that your action requires setup (unless it is already set up or ignored)
     *
     * @param useActionId id of the use action required by your mod
     * @param modId your mod id (informs player which mod requires that action)
     */
    public static void demandUseAction(Identifier useActionId, String modId) {
        ACTIONS_REQUIRE_ALLOCATION.putIfAbsent(useActionId, new ArrayList<>());
        ACTIONS_REQUIRE_ALLOCATION.get(useActionId).add(modId);
    }

    public static Map<Identifier, List<String>> getDemandedActions() {
        ImmutableMap.Builder<Identifier, List<String>> builder = ImmutableMap.builder();
        ACTIONS_REQUIRE_ALLOCATION.forEach((id, list) -> builder.put(id, List.copyOf(list)));

        return builder.build();
    }
}
