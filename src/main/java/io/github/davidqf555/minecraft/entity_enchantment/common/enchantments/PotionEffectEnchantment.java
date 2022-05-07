package io.github.davidqf555.minecraft.entity_enchantment.common.enchantments;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

import java.util.Map;

public abstract class PotionEffectEnchantment extends EntityEnchantment {

    public PotionEffectEnchantment(int max, int weight) {
        super(max, weight);
    }

    @Override
    public void onTick(LivingEntity entity, int level) {
        super.onTick(entity, level);
        for (EffectInstance effect : createEffects(entity, level)) {
            entity.addEffect(effect);
        }
    }

    protected EffectInstance[] createEffects(LivingEntity entity, int level) {
        Map<Effect, Integer> map = getEffectsMap(entity, level);
        EffectInstance[] effects = new EffectInstance[map.size()];
        int index = 0;
        for (Map.Entry<Effect, Integer> entry : map.entrySet()) {
            effects[index] = new EffectInstance(entry.getKey(), 2, entry.getValue(), true, false, false);
            index++;
        }
        return effects;
    }

    protected abstract Map<Effect, Integer> getEffectsMap(LivingEntity entity, int level);
}
