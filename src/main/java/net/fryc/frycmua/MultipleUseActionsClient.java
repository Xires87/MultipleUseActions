package net.fryc.frycmua;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fryc.frycmua.keybind.ModKeybinds;
import net.fryc.frycmua.util.client.FileHelper;

public class MultipleUseActionsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ModKeybinds.register();

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			FileHelper.saveAdditionalUseKeysEntries();
		});
	}
}