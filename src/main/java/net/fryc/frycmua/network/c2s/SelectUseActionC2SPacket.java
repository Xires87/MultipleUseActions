package net.fryc.frycmua.network.c2s;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fryc.frycmua.item.action.SelectsUseAction;
import net.fryc.frycmua.network.payload.SelectUseActionPayload;
import net.minecraft.text.Text;

public class SelectUseActionC2SPacket {

    public static void receive(SelectUseActionPayload payload, ServerPlayNetworking.Context context){
        if(context.player() != null) {
            ((SelectsUseAction) context.player()).setSelectedUseAction(payload.id());
        }
    }
}
