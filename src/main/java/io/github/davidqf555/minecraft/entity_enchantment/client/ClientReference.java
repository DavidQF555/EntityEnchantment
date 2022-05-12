package io.github.davidqf555.minecraft.entity_enchantment.client;

import io.github.davidqf555.minecraft.entity_enchantment.common.EntityEnchantments;
import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.EntityEnchantment;
import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.IllusionEnchantment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public final class ClientReference {

    private ClientReference() {
    }

    public static void updateEnchantments(int id, Map<EntityEnchantment, Integer> enchantments) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            Entity entity = world.getEntity(id);
            if (entity != null) {
                EntityEnchantments.setEnchantmentsData(entity, enchantments);
            }
        }
    }

    public static void updateIllusion(int id, int ticks, int total, Vec3[] offsets) {
        ClientLevel world = Minecraft.getInstance().level;
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
