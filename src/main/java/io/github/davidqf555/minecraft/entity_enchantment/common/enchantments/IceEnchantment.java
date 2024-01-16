package io.github.davidqf555.minecraft.entity_enchantment.common.enchantments;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.function.Function;

public class IceEnchantment extends EntityEnchantment {

    private final Function<Integer, Integer> ticks, amp;

    public IceEnchantment(int max, int weight, Function<Integer, Integer> ticks, Function<Integer, Integer> amp) {
        super(max, weight);
        this.ticks = ticks;
        this.amp = amp;
    }

    @Override
    public void onTick(LivingEntity entity, int level) {
        if (entity.onGround()) {
            BlockState ice = Blocks.FROSTED_ICE.defaultBlockState();
            int range = level + 3;
            BlockPos center = entity.blockPosition();
            for (BlockPos pos : BlockPos.betweenClosed(center.offset(-range, -1, -range), center.offset(range, -1, range))) {
                if (pos.closerToCenterThan(entity.position(), range)) {
                    Level world = entity.level();
                    if (world.isEmptyBlock(pos.above())) {
                        BlockState state = world.getBlockState(pos);
                        boolean isFull = state.hasProperty(LiquidBlock.LEVEL) && state.getValue(LiquidBlock.LEVEL) == 0;
                        if (state == FrostedIceBlock.meltsInto() && isFull && ice.canSurvive(world, pos) && world.isUnobstructed(ice, pos, CollisionContext.empty())) {
                            world.setBlockAndUpdate(pos, ice);
                            world.scheduleTick(pos, Blocks.FROSTED_ICE, Mth.nextInt(entity.getRandom(), 60, 120));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDamaged(LivingEntity entity, int level, DamageSource source, float damage) {
        Entity target = source.getEntity();
        if (target instanceof LivingEntity) {
            addEffects((LivingEntity) target, level);
        }
    }

    @Override
    public void onAttack(LivingEntity entity, int level, LivingEntity target, float damage) {
        addEffects(target, level);
    }

    private void addEffects(LivingEntity target, int level) {
        int amplitude = amp.apply(level);
        int duration = ticks.apply(level);
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, amplitude));
        target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration, amplitude));
    }

}
