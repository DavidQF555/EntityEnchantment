package io.github.davidqf555.minecraft.entity_enchantment.common.enchantments;

import io.github.davidqf555.minecraft.entity_enchantment.registration.EntityEnchantmentRegistry;
import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class EntityEnchantment extends ForgeRegistryEntry<EntityEnchantment> {

    private static int totalWeight = 0;
    private final int max, weight;
    private String id;

    public EntityEnchantment(int max, int weight) {
        this.max = max;
        this.weight = weight;
        totalWeight += weight;
    }

    public static int getTotalWeight() {
        return totalWeight;
    }

    public void onStart(LivingEntity entity, int level) {

    }

    public void onEnd(LivingEntity entity, int level) {

    }

    public void onTick(LivingEntity entity, int level) {

    }

    public void onDamaged(LivingEntity entity, int level, DamageSource source, float damage) {

    }

    public void onAttack(LivingEntity entity, int level, LivingEntity target, float damage) {

    }

    public int getNaturalMax() {
        return max;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isValid(LivingEntity entity, int level) {
        return true;
    }

    public String getID() {
        if (id == null) {
            id = Util.makeDescriptionId("entity_enchantment", EntityEnchantmentRegistry.getRegistry().getKey(this));
        }
        return id;
    }

    public TranslatableComponent getDisplayName(int level) {
        TranslatableComponent text = new TranslatableComponent(getID());
        if (level != 1 || getNaturalMax() != 1) {
            text.append(" ").append(new TranslatableComponent("enchantment.level." + level));
        }
        return text;
    }

}
