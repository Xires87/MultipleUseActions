package net.fryc.frycmua;

import net.fabricmc.api.ModInitializer;
import net.fryc.frycmua.item.action.DefaultUseActions;
import net.fryc.frycmua.item.action.registry.UseActionRegistries;
import net.fryc.frycmua.network.ModPackets;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultipleUseActions implements ModInitializer {
	public static final String MOD_ID = "frycmua";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModPackets.registerC2SPackets();

		UseActionRegistries.registerUseAction(Identifier.ofVanilla("bow_pull"), DefaultUseActions.BOW_PULL);
		UseActionRegistries.registerUseAction(Identifier.ofVanilla("crossbow_reload"), DefaultUseActions.CROSSBOW_RELOAD);
		UseActionRegistries.registerUseAction(Identifier.ofVanilla("crossbow_shoot"), DefaultUseActions.CROSSBOW_SHOOT);
		UseActionRegistries.registerUseAction(Identifier.ofVanilla("food_eat"), DefaultUseActions.FOOD_EAT);
	}
}