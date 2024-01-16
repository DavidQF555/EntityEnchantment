package io.github.davidqf555.minecraft.entity_enchantment.common.enchantments;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class FireEnchantment extends EntityEnchantment {

    private final Function<Integer, Integer> seconds;

    public FireEnchantment(int max, int weight, Function<Integer, Integer> seconds) {
        super(max, weight);
        this.seconds = seconds;
    }

    @Override
    public void onTick(LivingEntity entity, int level) {
        entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 2, 0, true, false, true));
        BlockPos pos = entity.blockPosition();
        if ((!(entity instanceof Player) || entity.isCrouching()) && entity.level().isEmptyBlock(pos)) {
            BlockState fire = Blocks.FIRE.defaultBlockState();
            if (((FireBlock) Blocks.FIRE).canSurvive(fire, entity.level(), pos)) {
                entity.level().setBlockAndUpdate(pos, fire);
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
