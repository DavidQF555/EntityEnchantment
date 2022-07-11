package io.github.davidqf555.minecraft.entity_enchantment.common.events;

import io.github.davidqf555.minecraft.entity_enchantment.common.EntityEnchantments;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EnchantmentEventSubscriber {

    private EnchantmentEventSubscriber() {
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide()) {
            DamageSource source = event.getSource();
            Entity damage = source.getEntity();
            float amount = event.getAmount();
            if (damage instanceof LivingEntity) {
                EntityEnchantments.get((LivingEntity) damage).onAttack((LivingEntity) damage, entity, amount);
            }
            EntityEnchantments.get(entity).onDamaged(entity, source, amount);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.world instanceof ServerLevel) {
            for (Entity entity : ((ServerLevel) event.world).getEntities().getAll()) {
                if (entity instanceof LivingEntity) {
                    EntityEnchantments.get((LivingEntity) entity).onTick((LivingEntity) entity);
                }
            }
        }
    }

}
