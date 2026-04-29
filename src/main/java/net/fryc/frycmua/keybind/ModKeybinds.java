package net.fryc.frycmua.keybind;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fryc.frycmua.item.action.client.ActionPriority;
import net.fryc.frycmua.item.action.client.PriorityManager;
import net.fryc.frycmua.screen.ActionManagerScreen;
import net.fryc.frycmua.util.client.FileHelper;
import net.fryc.frycmua.util.mixin_interfaces.player.client.HoldsActionManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ModKeybinds {

    public static final String KEY_CATEGORY_FRYCMUA = "key.category.frycmua.key_category_frycmua";
    public static final String KEY_ACTION_MANAGER_SCREEN = "key.frycmua.key_open_action_manager_screen";
    public static final String KEY_NEXT_USE_ACTION = "key.frycmua.key_next_use_action";
    public static final String KEY_PREVIOUS_USE_ACTION = "key.frycmua.key_previous_use_action";

    public static final Map<KeyBinding, PriorityManager> MUA_ADDITIONAL_USE_KEYS = new LinkedHashMap<>();

    public static KeyBinding actionManagerScreenKey;
    public static KeyBinding nextUseActionKey;
    public static KeyBinding previousUseActionKey;




    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(actionManagerScreenKey.wasPressed()){
                client.setScreen(new ActionManagerScreen(client.currentScreen));
            }
            else if(client.player != null){
                if(nextUseActionKey.wasPressed()) {
                    performActionSwitch(((HoldsActionManager) client.player), true);
                }
                else if(previousUseActionKey.wasPressed()) {
                    performActionSwitch(((HoldsActionManager) client.player), false);
                }
            }
        });
    }

    public static void register() {
        actionManagerScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_ACTION_MANAGER_SCREEN,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_APOSTROPHE,
                KEY_CATEGORY_FRYCMUA
        ));

        nextUseActionKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_NEXT_USE_ACTION,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEY_CATEGORY_FRYCMUA
        ));

        previousUseActionKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_PREVIOUS_USE_ACTION,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEY_CATEGORY_FRYCMUA
        ));

        FileHelper.getAdditionalUseKeysEntries().forEach((key, manager) -> {
            MUA_ADDITIONAL_USE_KEYS.put(KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    key,
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    KEY_CATEGORY_FRYCMUA
            )), manager);
        });

        registerKeyInputs();
    }

    public static void addNewAdditionalUseKeyAndQuit(int keysToAdd) {
        for(int i = 0; i < keysToAdd; i++) {
            ModKeybinds.MUA_ADDITIONAL_USE_KEYS.put(new KeyBinding(
                    "key.frycmua.key_additional_use_key_" + (ModKeybinds.MUA_ADDITIONAL_USE_KEYS.size() + 1),
                    GLFW.GLFW_KEY_UNKNOWN,
                    ModKeybinds.KEY_CATEGORY_FRYCMUA
            ), new PriorityManager(ActionPriority.DEFAULT));
        }

        MinecraftClient.getInstance().scheduleStop();
    }

    private static void performActionSwitch(HoldsActionManager player, boolean forward) {
        player.getAllActionManagers().stream().filter(actionManager -> {
            return actionManager.getPriorityManager().isSwitchable();
        }).forEach(actionManager -> {
            actionManager.switchChosenAction(forward);
            actionManager.forceUpdateAvailableActions();
        });
    }
}
