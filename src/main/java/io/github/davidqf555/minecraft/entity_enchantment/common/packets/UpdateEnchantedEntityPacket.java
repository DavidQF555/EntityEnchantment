package io.github.davidqf555.minecraft.entity_enchantment.common.packets;

import io.github.davidqf555.minecraft.entity_enchantment.client.ClientReference;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateEnchantedEntityPacket {

    private static final BiConsumer<UpdateEnchantedEntityPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.id);
        buffer.writeBoolean(message.enchanted);
    };
    private static final Function<PacketBuffer, UpdateEnchantedEntityPacket> DECODER = buffer -> new UpdateEnchantedEntityPacket(buffer.readInt(), buffer.readBoolean());
    private static final BiConsumer<UpdateEnchantedEntityPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final int id;
    private final boolean enchanted;

    public UpdateEnchantedEntityPacket(int id, boolean enchanted) {
        this.id = id;
        this.enchanted = enchanted;
    }

    public static void register(int index) {
        Main.CHANNEL.registerMessage(index, UpdateEnchantedEntityPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }


    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientReference.setEnchanted(id, enchanted));
        context.setPacketHandled(true);
    }
}
