package io.github.davidqf555.minecraft.entity_enchantment.common.events;

import io.github.davidqf555.minecraft.entity_enchantment.common.EntityEnchantments;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public final class CapabilityEventSubscriber {

    private static final ResourceLocation ENTITY_ENCHANTMENTS = new ResourceLocation(Main.MOD_ID, "entity_enchantments");

    private CapabilityEventSubscriber() {
    }

    @Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class ForgeBus {

        private ForgeBus() {
        }

        @SubscribeEvent
        public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof LivingEntity) {
                event.addCapability(ENTITY_ENCHANTMENTS, new EntityEnchantments.Provider());
            }
        }
    }

    @Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModBus {

        private ModBus() {
        }

        @SubscribeEvent
        public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                CapabilityManager.INSTANCE.register(EntityEnchantments.class, new EntityEnchantments.Storage(), EntityEnchantments::new);
            });
        }
    }

}
