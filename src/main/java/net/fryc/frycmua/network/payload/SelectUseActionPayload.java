package net.fryc.frycmua.network.payload;

import net.fryc.frycmua.network.ModPackets;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SelectUseActionPayload(Identifier id) implements CustomPayload {

    public static final Id<SelectUseActionPayload> ID = new Id<>(ModPackets.SELECT_USE_ACTION_ID);
    public static final PacketCodec<RegistryByteBuf, SelectUseActionPayload> CODEC = PacketCodec.tuple(Identifier.PACKET_CODEC, SelectUseActionPayload::id, SelectUseActionPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
