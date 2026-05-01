package net.fryc.frycmua.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fryc.frycmua.MultipleUseActions;
import net.fryc.frycmua.network.c2s.CancelInteractionC2SPacket;
import net.fryc.frycmua.network.c2s.SelectUseActionC2SPacket;
import net.fryc.frycmua.network.payload.CancelInteractionPayload;
import net.fryc.frycmua.network.payload.SelectUseActionPayload;
import net.minecraft.util.Identifier;

public class ModPackets {

    public static final Identifier SELECT_USE_ACTION_ID = Identifier.of(MultipleUseActions.MOD_ID, "select_use_action_id");
    public static final Identifier CANCEL_INTERACTION_ID = Identifier.of(MultipleUseActions.MOD_ID, "cancel_interaction_id");


    public static void registerC2SPackets(){
        //payloads
        PayloadTypeRegistry.playC2S().register(SelectUseActionPayload.ID, SelectUseActionPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(CancelInteractionPayload.ID, CancelInteractionPayload.CODEC);

        //receivers
        ServerPlayNetworking.registerGlobalReceiver(SelectUseActionPayload.ID, SelectUseActionC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CancelInteractionPayload.ID, CancelInteractionC2SPacket::receive);
    }
}
