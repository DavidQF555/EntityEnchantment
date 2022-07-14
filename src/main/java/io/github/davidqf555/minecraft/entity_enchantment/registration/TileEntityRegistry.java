package io.github.davidqf555.minecraft.entity_enchantment.registration;

import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.blocks.ScrollTileEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class TileEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Main.MOD_ID);

    private TileEntityRegistry() {
    }

    private static <T extends BlockEntityType<?>> RegistryObject<T> register(String name, Supplier<T> type) {
        return TYPES.register(name, type);
    }

    public static final RegistryObject<BlockEntityType<ScrollTileEntity>> SCROLL = register("scroll", () -> BlockEntityType.Builder.of(ScrollTileEntity::new, BlockRegistry.ENCHANTMENT_TRANSFUSER.get()).build(null));


}
