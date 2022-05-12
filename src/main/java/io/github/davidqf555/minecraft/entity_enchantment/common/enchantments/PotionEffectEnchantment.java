package io.github.davidqf555.minecraft.entity_enchantment.common.enchantments;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

public abstract class PotionEffectEnchantment extends EntityEnchantment {

    public PotionEffectEnchantment(int max, int weight) {
        super(max, weight);
    }

    @Override
    public void onTick(LivingEntity entity, int level) {
        super.onTick(entity, level);
        for (MobEffectInstance effect : createEffects(entity, level)) {
            entity.addEffect(effect);
        }
    }

    protected MobEffectInstance[] createEffects(LivingEntity entity, int level) {
        Map<MobEffect, Integer> map = getEffectsMap(entity, level);
        MobEffectInstance[] effects = new MobEffectInstance[map.size()];
        int index = 0;
        for (Map.Entry<MobEffect, Integer> entry : map.entrySet()) {
            effects[index] = new MobEffectInstance(entry.getKey(), 2, entry.getValue(), true, false, false);
            index++;
        }
        return effects;
    }

    protected abstract Map<MobEffect, Integer> getEffectsMap(LivingEntity entity, int level);
}
