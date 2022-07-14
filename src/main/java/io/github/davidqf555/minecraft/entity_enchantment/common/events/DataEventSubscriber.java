package io.github.davidqf555.minecraft.entity_enchantment.common.events;

import io.github.davidqf555.minecraft.entity_enchantment.common.EntityEnchantments;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.packets.UpdateClientEntityEnchantmentsPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DataEventSubscriber {

    private DataEventSubscriber() {
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        Player player = event.getEntity();
        Entity target = event.getTarget();
        if (player instanceof ServerPlayer && target instanceof LivingEntity) {
            Main.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new UpdateClientEntityEnchantmentsPacket(target.getId(), EntityEnchantments.get((LivingEntity) target).getAllEnchantments()));
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        updateSelfData(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        updateSelfData(event.getEntity());
    }

    private static void updateSelfData(Player player) {
        if (player instanceof ServerPlayer) {
            Main.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new UpdateClientEntityEnchantmentsPacket(player.getId(), EntityEnchantments.get(player).getAllEnchantments()));
        }
    }
}
