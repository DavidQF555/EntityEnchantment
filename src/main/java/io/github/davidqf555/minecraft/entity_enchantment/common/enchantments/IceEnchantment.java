package io.github.davidqf555.minecraft.entity_enchantment.common.enchantments;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;

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
        if (entity.isOnGround()) {
            BlockState ice = Blocks.FROSTED_ICE.defaultBlockState();
            float range = level + 3;
            BlockPos center = entity.blockPosition();
            for (BlockPos pos : BlockPos.betweenClosed(center.offset(-range, -1, -range), center.offset(range, -1.0D, range))) {
                if (pos.closerThan(entity.position(), range)) {
                    if (entity.level.isEmptyBlock(pos.above())) {
                        BlockState state = entity.level.getBlockState(pos);
                        boolean isFull = state.hasProperty(FlowingFluidBlock.LEVEL) && state.getValue(FlowingFluidBlock.LEVEL) == 0;
                        if (state.getMaterial() == Material.WATER && isFull && ice.canSurvive(entity.level, pos) && entity.level.isUnobstructed(ice, pos, ISelectionContext.empty())) {
                            entity.level.setBlockAndUpdate(pos, ice);
                            entity.level.getBlockTicks().scheduleTick(pos, Blocks.FROSTED_ICE, MathHelper.nextInt(entity.getRandom(), 60, 120));
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
        target.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, duration, amplitude));
        target.addEffect(new EffectInstance(Effects.DIG_SLOWDOWN, duration, amplitude));
    }

}
