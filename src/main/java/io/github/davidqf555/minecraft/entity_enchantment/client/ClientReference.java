package io.github.davidqf555.minecraft.entity_enchantment.client;

import io.github.davidqf555.minecraft.entity_enchantment.common.EntityEnchantments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public final class ClientReference {

    private ClientReference() {
    }

    public static void setEnchanted(int id, boolean enchanted) {
        ClientWorld world = Minecraft.getInstance().level;
        if (world != null) {
            Entity entity = world.getEntity(id);
            if (entity != null) {
                EntityEnchantments.setEnchanted(entity, enchanted);
            }
        }
    }
}
