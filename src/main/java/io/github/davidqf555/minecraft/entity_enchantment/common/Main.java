package io.github.davidqf555.minecraft.entity_enchantment.common;

import io.github.davidqf555.minecraft.entity_enchantment.common.registration.BlockRegistry;
import io.github.davidqf555.minecraft.entity_enchantment.common.registration.EntityEnchantmentRegistry;
import io.github.davidqf555.minecraft.entity_enchantment.common.registration.ItemRegistry;
import io.github.davidqf555.minecraft.entity_enchantment.common.registration.TileEntityRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod("entity_enchantment")
public class Main {

    public static final String MOD_ID = "entity_enchantment";
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MOD_ID, MOD_ID),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public Main() {
        addRegistries(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static void addRegistries(IEventBus bus) {
        BlockRegistry.BLOCKS.register(bus);
        TileEntityRegistry.TYPES.register(bus);
        ItemRegistry.ITEMS.register(bus);
        EntityEnchantmentRegistry.ENCHANTMENTS.register(bus);
    }
}
