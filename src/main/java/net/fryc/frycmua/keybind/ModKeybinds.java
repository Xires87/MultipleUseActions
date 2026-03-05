package net.fryc.frycmua.keybind;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fryc.frycmua.item.action.client.PriorityManager;
import net.fryc.frycmua.screen.ActionManagerScreen;
import net.fryc.frycmua.util.client.FileHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ModKeybinds {

    public static final String KEY_CATEGORY_FRYCMUA = "key.category.frycmua.key_category_frycmua";
    public static final String KEY_ACTION_MANAGER_SCREEN = "key.frycmua.key_open_action_manager_screen";

    public static final Map<KeyBinding, PriorityManager> MUA_ADDITIONAL_USE_KEYS = new HashMap<>();

    public static KeyBinding actionManagerScreenKey;




    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(actionManagerScreenKey.wasPressed()){
                if(client.currentScreen == null){
                    client.setScreen(new ActionManagerScreen(Text.empty()));
                }
            }
        });

        /*
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            for(KeyBinding key : MUA_ADDITIONAL_USE_KEYS.keySet()){
                if(key.isPressed()){
                    //MultipleUseActions.LOGGER.warn("Mua key is pressed!");

                    break;
                }
            }
        });

         */
    }

    public static void register() {
        actionManagerScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_ACTION_MANAGER_SCREEN,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_APOSTROPHE,
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

        // TODO tutaj odczyt z pliku w configu zeby te keybindy zrobic zamiast recznego dodadwania
        /*
        MUA_ADDITIONAL_USE_KEYS.put(KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.frycmua.key_additional_use_key_1",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEY_CATEGORY_FRYCMUA
        )), );
        MUA_ADDITIONAL_USE_KEYS.put(KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.frycmua.key_additional_use_key_2",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEY_CATEGORY_FRYCMUA
        )), );

         */

        registerKeyInputs();
    }
}
