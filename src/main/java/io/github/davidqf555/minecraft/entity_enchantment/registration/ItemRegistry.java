package io.github.davidqf555.minecraft.entity_enchantment.registration;

import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.items.EnchantedScrollItem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MOD_ID);
    public static final Map<RegistryObject<? extends Item>, Set<ResourceKey<CreativeModeTab>>> TABS = new HashMap<>();

    public static final RegistryObject<EnchantedScrollItem> ENCHANTED_SCROLL = register("enchanted_scroll", Set.of(CreativeModeTabs.INGREDIENTS, CreativeModeTabs.SEARCH), () -> new EnchantedScrollItem(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));
    public static final RegistryObject<Item> SCROLL = register("scroll", Set.of(CreativeModeTabs.INGREDIENTS, CreativeModeTabs.SEARCH), () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockItem> ENCHANTMENT_TRANSFUSER = register("enchantment_transfuser", Set.of(CreativeModeTabs.FUNCTIONAL_BLOCKS, CreativeModeTabs.SEARCH), () -> new BlockItem(BlockRegistry.ENCHANTMENT_TRANSFUSER.get(), new Item.Properties().rarity(Rarity.EPIC)));

    private static <T extends Item> RegistryObject<T> register(String name, Set<ResourceKey<CreativeModeTab>> tabs, Supplier<T> item) {
        RegistryObject<T> obj = ITEMS.register(name, item);
        TABS.put(obj, tabs);
        return obj;
    }

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        TABS.forEach((item, tabs) -> {
            if (tabs.contains(event.getTabKey())) {
                Item val = item.get();
                if (val instanceof EnchantedScrollItem) {
                    event.acceptAll(((EnchantedScrollItem) val).getAll());
                } else {
                    event.accept(val);
                }
            }
        });
    }

}
