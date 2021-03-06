package io.github.davidqf555.minecraft.entity_enchantment.registration;

import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.packets.UpdateClientEntityEnchantmentsPacket;
import io.github.davidqf555.minecraft.entity_enchantment.common.packets.UpdateClientIllusionTicksPacket;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class PacketRegistry {

    private PacketRegistry() {
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            UpdateClientEntityEnchantmentsPacket.register(0);
            UpdateClientIllusionTicksPacket.register(1);
        });
    }
}
