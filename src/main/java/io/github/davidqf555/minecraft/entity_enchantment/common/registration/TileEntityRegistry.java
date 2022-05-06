package io.github.davidqf555.minecraft.entity_enchantment.common.registration;

import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.blocks.ScrollTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class TileEntityRegistry {

    public static final DeferredRegister<TileEntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Main.MOD_ID);

    private TileEntityRegistry() {
    }

    private static <T extends TileEntityType<?>> RegistryObject<T> register(String name, Supplier<T> type) {
        return TYPES.register(name, type);
    }

    public static final RegistryObject<TileEntityType<ScrollTileEntity>> SCROLL = register("enchantment_scroll", () -> TileEntityType.Builder.of(ScrollTileEntity::new, BlockRegistry.ENCHANTMENT_INFUSER.get()).build(null));


}
