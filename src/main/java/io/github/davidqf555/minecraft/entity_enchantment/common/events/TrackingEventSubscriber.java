package io.github.davidqf555.minecraft.entity_enchantment.common.events;

import io.github.davidqf555.minecraft.entity_enchantment.common.EntityEnchantments;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.packets.UpdateEnchantedEntityPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class TrackingEventSubscriber {

    private TrackingEventSubscriber() {
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        PlayerEntity player = event.getPlayer();
        Entity target = event.getTarget();
        if (player instanceof ServerPlayerEntity && target instanceof LivingEntity) {
            Main.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new UpdateEnchantedEntityPacket(target.getId(), EntityEnchantments.isEnchanted((LivingEntity) target)));
        }
    }
}
