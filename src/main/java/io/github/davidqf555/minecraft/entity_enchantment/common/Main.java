package io.github.davidqf555.minecraft.entity_enchantment.common;

import io.github.davidqf555.minecraft.entity_enchantment.registration.BlockRegistry;
import io.github.davidqf555.minecraft.entity_enchantment.registration.EntityEnchantmentRegistry;
import io.github.davidqf555.minecraft.entity_enchantment.registration.ItemRegistry;
import io.github.davidqf555.minecraft.entity_enchantment.registration.TileEntityRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

@Mod("entity_enchantment")
public class Main {

    public static final String MOD_ID = "entity_enchantment";
    public static final SimpleChannel CHANNEL = ChannelBuilder.named(new ResourceLocation(MOD_ID, MOD_ID)).simpleChannel();

    public Main() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPEC);
        addRegistries(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private static void addRegistries(IEventBus bus) {
        BlockRegistry.BLOCKS.register(bus);
        TileEntityRegistry.TYPES.register(bus);
        ItemRegistry.ITEMS.register(bus);
        EntityEnchantmentRegistry.ENCHANTMENTS.register(bus);
    }
}
