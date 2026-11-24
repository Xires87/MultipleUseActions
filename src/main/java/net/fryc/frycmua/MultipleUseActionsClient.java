package net.fryc.frycmua;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fryc.frycmua.action.client.ActionManager;
import net.fryc.frycmua.keybind.ModKeybinds;
import net.fryc.frycmua.screen.QuickSetupScreen;
import net.fryc.frycmua.util.client.FileHelper;
import net.fryc.frycmua.util.client.UseActionHelperClient;
import net.fryc.frycmua.util.mixin_interfaces.player.client.HoldsActionManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MultipleUseActionsClient implements ClientModInitializer {

	private static final Identifier HOTBAR_OFFHAND_LEFT_TEXTURE = Identifier.ofVanilla("hud/hotbar_offhand_left");

	private static final int HOTBAR_TEXTURE_WIDTH = 29;
	private static final int HOTBAR_TEXTURE_HEIGHT = 24;
	private static final int HOTBAR_TEXTURE_PADDING = 7;

	private static final int SMALL_TEXTURE_WIDTH = 16;
	private static final int SMALL_TEXTURE_HEIGHT = 16;

	private static boolean shouldAsk = true;

	@Override
	public void onInitializeClient() {
		ModKeybinds.register();

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			FileHelper.saveAdditionalUseKeysEntries();
			FileHelper.saveMainPriorityManager();
		});

		ScreenEvents.BEFORE_INIT.register((client, screen, i, j) -> {
			if(screen.getClass() == TitleScreen.class) {
				if(shouldAsk){
					Map<Identifier, List<String>> map = UseActionHelperClient.getActionsThatRequireSetup();
					//if(!map.isEmpty()) {
						client.setScreen(new QuickSetupScreen(screen, List.of(Identifier.ofVanilla("block"), Identifier.ofVanilla("bow_pull"))));// TODO dac info screena zamiast quick setupa
					//}

					shouldAsk = false;
				}
			}
		});

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			if(client.currentScreen != null && client.currentScreen.getClass() == TitleScreen.class) {
				MultipleUseActions.LOGGER.warn("jestem w menu!");// TODO usunac
			}
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
				case "key.mouse.4" -> Text.of("MB4");
				case "key.mouse.5" -> Text.of("MB5");
				default -> Text.of("MB");
			};
		}

		return key.getBoundKeyLocalizedText();
	}
}