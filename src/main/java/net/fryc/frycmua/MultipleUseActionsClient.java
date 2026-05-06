package net.fryc.frycmua;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fryc.frycmua.action.client.ActionManager;
import net.fryc.frycmua.keybind.ModKeybinds;
import net.fryc.frycmua.util.client.FileHelper;
import net.fryc.frycmua.util.mixin_interfaces.player.client.HoldsActionManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class MultipleUseActionsClient implements ClientModInitializer {

	private static final Identifier HOTBAR_OFFHAND_LEFT_TEXTURE = Identifier.ofVanilla("hud/hotbar_offhand_left");

	private static final int HOTBAR_TEXTURE_WIDTH = 29;
	private static final int HOTBAR_TEXTURE_HEIGHT = 24;
	private static final int HOTBAR_TEXTURE_PADDING = 7;

	private static final int SMALL_TEXTURE_WIDTH = 16;
	private static final int SMALL_TEXTURE_HEIGHT = 16;

	@Override
	public void onInitializeClient() {
		ModKeybinds.register();

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			FileHelper.saveAdditionalUseKeysEntries();
			FileHelper.saveMainPriorityManager();
		});

		HudRenderCallback.EVENT.register((context, tickDelta) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			ClientPlayerEntity player = client.player;
			if(player != null){
				int x = (int) (context.getScaledWindowWidth() * 0.8F);
				int y = context.getScaledWindowHeight() - 22;

				for(ActionManager manager : ((HoldsActionManager) player).getAllActionManagers().stream().filter(ActionManager::shouldShowCurrentAction).toList()) {
					context.drawText(client.textRenderer, getShortenedKeybindName(manager.getKeyBinding()), x + 2, y - 8, 0xFFFFFFFF, true);
					context.drawGuiTexture(HOTBAR_OFFHAND_LEFT_TEXTURE, x, y, HOTBAR_TEXTURE_WIDTH, HOTBAR_TEXTURE_HEIGHT);
					Optional<Identifier> optional = manager.getAvailableActions(true).stream().findFirst();
					if(optional.isPresent()) {
						context.drawGuiTexture(optional.get(), x + 3, y + 4, SMALL_TEXTURE_WIDTH, SMALL_TEXTURE_HEIGHT);
					}

					x += HOTBAR_TEXTURE_WIDTH - HOTBAR_TEXTURE_PADDING;
				}
			}
		});
	}

	private static Text getShortenedKeybindName(KeyBinding key) {
		if(key.getBoundKeyTranslationKey().contains("mouse")) {
			return switch (key.getBoundKeyTranslationKey()) {
				case "key.mouse.left" -> Text.of("LMB");
				case "key.mouse.right" -> Text.of("RMB");
				case "key.mouse.middle" -> Text.of("MMB");
				default -> Text.of("MB");
			};
		}

		return key.getBoundKeyLocalizedText();
	}
}