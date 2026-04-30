package net.fryc.frycmua.network.c2s;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fryc.frycmua.network.payload.SelectUseActionPayload;
import net.fryc.frycmua.util.mixin_interfaces.player.SelectsUseAction;

public class SelectUseActionC2SPacket {

    public static void receive(SelectUseActionPayload payload, ServerPlayNetworking.Context context){
        if(context.player() != null) {
            ((SelectsUseAction) context.player()).setSelectedUseAction(payload.id());
        }
    }
}
