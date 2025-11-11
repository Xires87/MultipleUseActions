package net.fryc.frycmua;

import net.fabricmc.api.ClientModInitializer;
import net.fryc.frycmua.keybind.ModKeybinds;
import net.fryc.frycmua.util.client.UseActionPriorityHelper;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class MultipleUseActionsClient implements ClientModInitializer {

	public static final Map<Identifier, Integer> USE_ACTION_PRIORITIES = new HashMap<>();

	@Override
	public void onInitializeClient() {
		ModKeybinds.register();

		UseActionPriorityHelper.initUseActionPriorities();
	}
}