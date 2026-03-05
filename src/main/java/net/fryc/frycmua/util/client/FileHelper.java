package net.fryc.frycmua.util.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fryc.frycmua.MultipleUseActions;
import net.fryc.frycmua.item.action.client.ActionPriority;
import net.fryc.frycmua.item.action.client.PriorityManager;
import net.fryc.frycmua.keybind.ModKeybinds;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class FileHelper {

    public static HashMap<String, PriorityManager> getAdditionalUseKeysEntries() {
        File file = new File(FabricLoader.getInstance().getConfigDir().toString() + "/" + MultipleUseActions.MOD_ID + "/client/additional_use_keys.json");
        if(file.exists()){
            try(FileReader reader = new FileReader(file)){
                Gson gson = new Gson();
                TypeToken<HashMap<String, PriorityManager>> mapToken = new TypeToken<>(){};

                return gson.fromJson(reader, mapToken);
            } catch (Exception e) {
                MultipleUseActions.LOGGER.error("An error occurred while reading the following file: '" + file.getPath() + "'", e);
            }
        }

        return getDefaultAdditionalUseKeysEntries();
    }

    public static void saveAdditionalUseKeysEntries() {
        File file = new File(FabricLoader.getInstance().getConfigDir().toString() + "/" + MultipleUseActions.MOD_ID + "/client/additional_use_keys.json");
        if(!file.exists()){
            try{
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                MultipleUseActions.LOGGER.error("An error occurred while creating the following file: " + file.getPath(), e);
            }
        }
        try(FileWriter writer = new FileWriter(file)){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            HashMap<String, PriorityManager> map = new HashMap<>();
            ModKeybinds.MUA_ADDITIONAL_USE_KEYS.forEach((key, value) -> {
                map.put(key.getTranslationKey(), value);
            });
            writer.write(gson.toJson(map));

        } catch (Exception e) {
            MultipleUseActions.LOGGER.error("An error occurred while writing to the following file: " + file.getPath(), e);
        }
    }

    public static HashMap<String, PriorityManager> getDefaultAdditionalUseKeysEntries() {
        HashMap<String, PriorityManager> map = new HashMap<>();
        map.put("key.frycmua.key_additional_use_key_1", new PriorityManager(ActionPriority.MIXED));
        map.put("key.frycmua.key_additional_use_key_2", new PriorityManager(ActionPriority.MIXED));

        return map;
    }
}
