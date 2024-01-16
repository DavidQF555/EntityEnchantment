package io.github.davidqf555.minecraft.entity_enchantment.common.packets;

import io.github.davidqf555.minecraft.entity_enchantment.client.ClientReference;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.EntityEnchantment;
import io.github.davidqf555.minecraft.entity_enchantment.registration.EntityEnchantmentRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateClientEntityEnchantmentsPacket {

    private static final BiConsumer<UpdateClientEntityEnchantmentsPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.id);
        buffer.writeInt(message.enchantments.size());
        IForgeRegistry<EntityEnchantment> registry = EntityEnchantmentRegistry.getRegistry();
        message.enchantments.forEach((enchantment, level) -> {
            buffer.writeResourceLocation(registry.getKey(enchantment));
            buffer.writeInt(level);
        });
    };
    private static final Function<FriendlyByteBuf, UpdateClientEntityEnchantmentsPacket> DECODER = buffer -> {
        int id = buffer.readInt();
        Map<EntityEnchantment, Integer> enchantments = new HashMap<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            enchantments.put(EntityEnchantmentRegistry.getRegistry().getValue(buffer.readResourceLocation()), buffer.readInt());
        }
        return new UpdateClientEntityEnchantmentsPacket(id, enchantments);
    };
    private static final BiConsumer<UpdateClientEntityEnchantmentsPacket, Supplier<CustomPayloadEvent.Context>> CONSUMER = (message, context) -> {
        CustomPayloadEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final int id;
    private final Map<EntityEnchantment, Integer> enchantments;

    public UpdateClientEntityEnchantmentsPacket(int id, Map<EntityEnchantment, Integer> enchantments) {
        this.id = id;
        this.enchantments = enchantments;
    }

    public static void register(int index) {
        Main.CHANNEL.messageBuilder(UpdateClientEntityEnchantmentsPacket.class, index, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ENCODER)
                .decoder(DECODER)
                .consumerMainThread(UpdateClientEntityEnchantmentsPacket::handle)
                .add();
    }

    private void handle(CustomPayloadEvent.Context context) {
        ClientReference.updateEnchantments(id, enchantments);
    }

}
