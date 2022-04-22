package io.github.davidqf555.minecraft.entity_enchantment.common.enchantments;

import io.github.davidqf555.minecraft.entity_enchantment.common.EntityEnchantments;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EnchantmentEventBusSubscriber {

    private EnchantmentEventBusSubscriber() {
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.world instanceof ServerWorld) {
            ((ServerWorld) event.world).getEntities()
                    .filter(entity -> entity instanceof LivingEntity)
                    .forEach(entity -> EntityEnchantments.get((LivingEntity) entity).onTick((LivingEntity) entity));
        }
    }
}
