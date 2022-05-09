package io.github.davidqf555.minecraft.entity_enchantment.common.events;

import io.github.davidqf555.minecraft.entity_enchantment.common.EntityEnchantments;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
            EntityEnchantments.get(entity).onDamaged(entity, event.getSource(), event.getAmount());
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
    public static void onPlayerClone(PlayerEvent.Clone event) {
        PlayerEntity clone = event.getPlayer();
        EntityEnchantments.get(event.getOriginal()).getAllEnchantments().forEach((enchantment, level) -> {
            EntityEnchantments.setEnchantment(clone, enchantment, level);
        });
    }
}
