package io.github.davidqf555.minecraft.entity_enchantment.common.events;

import io.github.davidqf555.minecraft.entity_enchantment.common.EntityEnchantments;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.ServerConfigs;
import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.EntityEnchantment;
import io.github.davidqf555.minecraft.entity_enchantment.registration.EntityEnchantmentRegistry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SpawnEventSubscriber {

    private SpawnEventSubscriber() {
    }

    @SubscribeEvent
    public static void onLivingSpecialSpawn(LivingSpawnEvent.SpecialSpawn event) {
        Mob entity = event.getEntity();
        RandomSource rand = entity.getRandom();
        if (!entity.level.isClientSide() && rand.nextDouble() < ServerConfigs.INSTANCE.naturalRate.get()) {
            int selected = rand.nextInt(EntityEnchantment.getTotalWeight());
            int current = 0;
            for (EntityEnchantment enchantment : EntityEnchantmentRegistry.getRegistry()) {
                current += enchantment.getWeight();
                if (current >= selected) {
                    int level = getLevel(rand, enchantment.getNaturalMax());
                    EntityEnchantments.setEnchantment(entity, enchantment, level);
                    return;
                }
            }
        }
    }

    private static int getLevel(RandomSource random, int max) {
        double selected = random.nextDouble();
        double current = 0;
        double rate = ServerConfigs.INSTANCE.naturalLevelRate.get();
        double choose = 1;
        double success = 1;
        double fail = Math.pow(1 - rate, max - 1);
        for (int i = 0; i < max; i++) {
            current += choose * success * fail;
            if (selected < current) {
                return i + 1;
            }
            choose *= (max - i - 1.0) / (i + 1);
            success *= rate;
            fail /= 1 - rate;
        }
        throw new RuntimeException();
    }
}
