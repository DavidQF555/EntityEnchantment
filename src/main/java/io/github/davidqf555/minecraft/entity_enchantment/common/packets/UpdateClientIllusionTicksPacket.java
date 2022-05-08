package io.github.davidqf555.minecraft.entity_enchantment.common.packets;

import io.github.davidqf555.minecraft.entity_enchantment.client.ClientReference;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateClientIllusionTicksPacket {

    private static final BiConsumer<UpdateClientIllusionTicksPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.id);
        buffer.writeInt(message.ticks);
        buffer.writeInt(message.total);
        buffer.writeInt(message.offsets.length);
        for (Vector3d offset : message.offsets) {
            buffer.writeDouble(offset.x());
            buffer.writeDouble(offset.y());
            buffer.writeDouble(offset.z());
        }
    };
    private static final Function<PacketBuffer, UpdateClientIllusionTicksPacket> DECODER = buffer -> {
        int id = buffer.readInt();
        int ticks = buffer.readInt();
        int total = buffer.readInt();
        int length = buffer.readInt();
        Vector3d[] offsets = new Vector3d[length];
        for (int i = 0; i < length; i++) {
            offsets[i] = new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }
        return new UpdateClientIllusionTicksPacket(id, ticks, total, offsets);
    };
    private static final BiConsumer<UpdateClientIllusionTicksPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final int id, ticks, total;
    private final Vector3d[] offsets;

    public UpdateClientIllusionTicksPacket(int id, int ticks, int total, Vector3d[] offsets) {
        this.id = id;
        this.total = total;
        this.ticks = ticks;
        this.offsets = offsets;
    }

    public static void register(int index) {
        Main.CHANNEL.registerMessage(index, UpdateClientIllusionTicksPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }


    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientReference.updateIllusion(id, ticks, total, offsets));
        context.setPacketHandled(true);
    }
}
