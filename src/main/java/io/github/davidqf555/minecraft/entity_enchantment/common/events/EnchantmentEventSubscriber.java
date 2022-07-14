package io.github.davidqf555.minecraft.entity_enchantment.common.events;

import io.github.davidqf555.minecraft.entity_enchantment.common.EntityEnchantments;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.items.EnchantedScrollItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AnvilUpdateEvent;
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
        if (event.phase == TickEvent.Phase.START && event.world instanceof ServerWorld) {
            ((ServerWorld) event.world).getEntities()
                    .filter(entity -> entity instanceof LivingEntity)
                    .forEach(entity -> EntityEnchantments.get((LivingEntity) entity).onTick((LivingEntity) entity));
        }
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (left.getItem() instanceof EnchantedScrollItem && right.getItem() instanceof EnchantedScrollItem) {
            ItemStack out = EnchantedScrollItem.getMergeResult(left, right);
            if (!out.isEmpty()) {
                event.setOutput(out);
                event.setCost(EnchantedScrollItem.getMergeCost(left, right));
                event.setMaterialCost(1);
            }
        }
    }

}
