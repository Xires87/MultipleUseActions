package net.fryc.frycmua.network.payload;

import net.fryc.frycmua.network.ModPackets;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record CancelInteractionPayload(boolean shouldCancel) implements CustomPayload {

    public static final CustomPayload.Id<CancelInteractionPayload> ID = new CustomPayload.Id<>(ModPackets.CANCEL_INTERACTION_ID);
    public static final PacketCodec<RegistryByteBuf, CancelInteractionPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, CancelInteractionPayload::shouldCancel, CancelInteractionPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
