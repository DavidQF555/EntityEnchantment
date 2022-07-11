package io.github.davidqf555.minecraft.entity_enchantment.common.enchantments;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;

import java.util.function.Function;

public class FireEnchantment extends EntityEnchantment {

    private final Function<Integer, Integer> seconds;

    public FireEnchantment(int max, int weight, Function<Integer, Integer> seconds) {
        super(max, weight);
        this.seconds = seconds;
    }

    @Override
    public void onTick(LivingEntity entity, int level) {
        entity.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 2, 0, true, false, true));
        BlockPos pos = entity.blockPosition();
        if ((!(entity instanceof PlayerEntity) || entity.isCrouching()) && entity.level.isEmptyBlock(pos)) {
            BlockState fire = Blocks.FIRE.defaultBlockState();
            if (((FireBlock) Blocks.FIRE).canSurvive(fire, entity.level, pos)) {
                entity.level.setBlockAndUpdate(pos, fire);
            }
        }
    }

    @Override
    public void onDamaged(LivingEntity entity, int level, DamageSource source, float damage) {
        Entity attacker = source.getDirectEntity();
        if (attacker != null) {
            attacker.setSecondsOnFire(seconds.apply(level));
        }
    }

    @Override
    public void onAttack(LivingEntity entity, int level, LivingEntity target, float damage) {
        target.setSecondsOnFire(seconds.apply(level));
    }
}
