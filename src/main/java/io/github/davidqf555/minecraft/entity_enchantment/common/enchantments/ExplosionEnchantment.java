package io.github.davidqf555.minecraft.entity_enchantment.common.enchantments;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.function.Function;
import java.util.function.Predicate;

public class ExplosionEnchantment extends EntityEnchantment {

    private final float minDamage;
    private final Function<Integer, Float> radius;
    private final Predicate<Integer> fire;

    public ExplosionEnchantment(int max, int weight, float minDamage, Function<Integer, Float> radius, Predicate<Integer> fire) {
        super(max, weight);
        this.minDamage = minDamage;
        this.radius = radius;
        this.fire = fire;
    }

    @Override
    public void onDamaged(LivingEntity entity, int level, DamageSource source, float damage) {
        if (damage >= minDamage && !source.is(DamageTypes.EXPLOSION)) {
            boolean fire = this.fire.test(level);
            float radius = this.radius.apply(level);
            entity.level.explode(entity, null, null, entity.getX(), entity.getEyeY(), entity.getZ(), radius, fire, Level.ExplosionInteraction.MOB);
        }
    }
}
