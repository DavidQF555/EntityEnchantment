package io.github.davidqf555.minecraft.entity_enchantment.client;

import io.github.davidqf555.minecraft.entity_enchantment.common.EntityEnchantments;
import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.EntityEnchantment;
import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.IllusionEnchantment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Map;

public final class ClientReference {

    private ClientReference() {
    }

    public static void updateEnchantments(int id, Map<EntityEnchantment, Integer> enchantments) {
        ClientWorld world = Minecraft.getInstance().level;
        if (world != null) {
            Entity entity = world.getEntity(id);
            if (entity != null) {
                EntityEnchantments.setEnchantmentsData(entity, enchantments);
            }
        }
    }

    public static void updateIllusion(int id, int ticks, int total, Vector3d[] offsets) {
        ClientWorld world = Minecraft.getInstance().level;
        if (world != null) {
            Entity entity = world.getEntity(id);
            if (entity != null) {
                IllusionEnchantment.setTotalIllusionDuration(entity, total);
                IllusionEnchantment.setIllusionDuration(entity, ticks);
                IllusionEnchantment.setIllusionOffsets(entity, offsets);
            }
        }
    }

}
