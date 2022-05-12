package io.github.davidqf555.minecraft.entity_enchantment.common.registration;

import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.items.EnchantedScrollItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MOD_ID);

    public static final RegistryObject<EnchantedScrollItem> ENCHANTED_SCROLL = register("enchanted_scroll", () -> new EnchantedScrollItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> SCROLL = register("scroll", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockItem> ENCHANTMENT_TRANSFUSER = register("enchantment_transfuser", () -> new BlockItem(BlockRegistry.ENCHANTMENT_TRANSFUSER.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC).rarity(Rarity.EPIC)));

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }

}
