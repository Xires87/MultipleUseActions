package net.fryc.frycmua;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fryc.frycmua.item.action.DefaultUseActions;
import net.fryc.frycmua.item.action.ItemUseInstance;
import net.fryc.frycmua.item.action.registry.UseActionRegistries;
import net.fryc.frycmua.network.ModPackets;
import net.minecraft.item.BowItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
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
// TODO chyba bede na playerze zapisywal identifier wybranej akcji i przed wywolaniem interactItem bede sprawdzal ja a po wykonaniu akcji bede resetowal wybrana akcje
		LOGGER.info("Hello Fabric world!");

		ModPackets.registerC2SPackets();

		UseActionRegistries.registerUseAction(Identifier.ofVanilla("bow_pull"), DefaultUseActions.BOW_PULL);
	}
}