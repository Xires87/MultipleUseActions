package net.fryc.frycmua.util.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fryc.frycmua.MultipleUseActions;
import net.fryc.frycmua.action.client.ActionPriority;
import net.fryc.frycmua.action.client.PriorityManager;
import net.fryc.frycmua.keybind.ModKeybinds;
import net.fryc.frycmua.util.client.json.IdentifierAdapter;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.List;

@Environment(EnvType.CLIENT)
public class FileHelper {

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Identifier.class, new IdentifierAdapter()).setPrettyPrinting().create();
    private static final String CONFIG_PATH = FabricLoader.getInstance().getConfigDir().toString() + "/" + MultipleUseActions.MOD_ID;
    private static final String ADDITIONAL_USE_KEYS_PATH = CONFIG_PATH + "/client/additional_use_keys.json";
    private static final String MAIN_USE_KEY_PATH = CONFIG_PATH + "/client/main_use_key.json";

    public static LinkedHashMap<String, PriorityManager> getAdditionalUseKeysEntries() {
        File file = new File(ADDITIONAL_USE_KEYS_PATH);
        if(file.exists()){
            try(FileReader reader = new FileReader(file)){
                TypeToken<LinkedHashMap<String, PriorityManager>> mapToken = new TypeToken<>(){};

                return GSON.fromJson(reader, mapToken);
            } catch (Exception e) {
                MultipleUseActions.LOGGER.error("An error occurred while reading the following file: '" + file.getPath() + "'", e);
            }
        }

        return getDefaultAdditionalUseKeysEntries();
    }

    public static void saveAdditionalUseKeysEntries() {
        File file = new File(ADDITIONAL_USE_KEYS_PATH);
        tryToMakeFile(file);
        try(FileWriter writer = new FileWriter(file)){
            LinkedHashMap<String, PriorityManager> map = new LinkedHashMap<>();
            ModKeybinds.MUA_ADDITIONAL_USE_KEYS.forEach((key, value) -> {
                map.put(key.getTranslationKey(), value);
            });

            writer.write(GSON.toJson(map));

        } catch (Exception e) {
            MultipleUseActions.LOGGER.error("An error occurred while writing to the following file: " + file.getPath(), e);
        }
    }

    public static PriorityManager getMainPriorityManager() {
        File file = new File(MAIN_USE_KEY_PATH);
        if(file.exists()){
            try(FileReader reader = new FileReader(file)){
                return GSON.fromJson(reader, PriorityManager.class);
            } catch (Exception e) {
                MultipleUseActions.LOGGER.error("An error occurred while reading the following file: '" + file.getPath() + "'", e);
            }
        }

        return getDefaultPriorityManager();
    }

    public static void saveMainPriorityManager() {
        File file = new File(MAIN_USE_KEY_PATH);
        tryToMakeFile(file);
        try(FileWriter writer = new FileWriter(file)){
            writer.write(GSON.toJson(PriorityManager.DEFAULT_PRIORITY_MANAGER));
        } catch (Exception e) {
            MultipleUseActions.LOGGER.error("An error occurred while writing to the following file: " + file.getPath(), e);
        }
    }

    public static LinkedHashMap<String, PriorityManager> getDefaultAdditionalUseKeysEntries() {
        LinkedHashMap<String, PriorityManager> map = new LinkedHashMap<>();
        map.put("key.frycmua.key_additional_use_key_1", PriorityManager.createEmptyManager());
        map.put("key.frycmua.key_additional_use_key_2", PriorityManager.createEmptyManager());

        return map;
    }

    public static PriorityManager getDefaultPriorityManager() {
        return new PriorityManager(ActionPriority.DEFAULT);
    }

    public static void saveIgnoredUseActions(List<Identifier> actions) {
        // TODO
    }

    public static List<Identifier> getIgnoredUseActions() {
        return List.of(); // TODO
    }

    private static void tryToMakeFile(File file) {
        if(!file.exists()){
            try{
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                MultipleUseActions.LOGGER.error("An error occurred while creating the following file: " + file.getPath(), e);
            }
        }
    }
}
