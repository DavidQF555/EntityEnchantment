package io.github.davidqf555.minecraft.entity_enchantment.common.enchantments;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

public class ReflectEnchantment extends EntityEnchantment {

    private final Function<Integer, Float> ratio;

    public ReflectEnchantment(int max, int weight, Function<Integer, Float> ratio) {
        super(max, weight);
        this.ratio = ratio;
    }

    @Override
    public void onDamaged(LivingEntity entity, int level, DamageSource source, float damage) {
        Entity target = source.getEntity();
        if (target != null) {
            target.hurt(source, damage * ratio.apply(level));
        }
    }

}
