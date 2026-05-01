package net.fryc.frycmua.network.c2s;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fryc.frycmua.network.payload.CancelInteractionPayload;
import net.fryc.frycmua.util.mixin_interfaces.player.SelectsUseAction;

public class CancelInteractionC2SPacket {

    public static void receive(CancelInteractionPayload payload, ServerPlayNetworking.Context context){
        if(context.player() != null) {
            ((SelectsUseAction) context.player()).setShouldBlockInteractionBeCancelled(payload.shouldCancel());
        }
    }
}
