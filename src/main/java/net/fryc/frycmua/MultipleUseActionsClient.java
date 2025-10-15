package net.fryc.frycmua;

import net.fabricmc.api.ClientModInitializer;
import net.fryc.frycmua.keybind.ModKeybinds;

public class MultipleUseActionsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ModKeybinds.register();
	}
}