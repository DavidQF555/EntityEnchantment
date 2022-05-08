package io.github.davidqf555.minecraft.entity_enchantment.common.registration;

import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.blocks.EnchantmentTransfuserBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MOD_ID);

    public static final RegistryObject<EnchantmentTransfuserBlock> ENCHANTMENT_TRANSFUSER = register("enchantment_transfuser", () -> new EnchantmentTransfuserBlock(AbstractBlock.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(5, 1200)));

    private BlockRegistry() {
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }
}
