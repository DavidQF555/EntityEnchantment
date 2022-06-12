package io.github.davidqf555.minecraft.entity_enchantment.common.items;

import io.github.davidqf555.minecraft.entity_enchantment.common.EntityEnchantments;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.EntityEnchantment;
import io.github.davidqf555.minecraft.entity_enchantment.common.registration.EntityEnchantmentRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SimpleFoiledItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantedScrollItem extends SimpleFoiledItem {

    public EnchantedScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag tooltip) {
        super.appendHoverText(stack, world, text, tooltip);
        getEnchantments(stack).forEach((enchantment, level) -> text.add(enchantment.getDisplayName(level)));
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (group.equals(category)) {
            items.addAll(getAll());
        }
    }

    public List<ItemStack> getAll() {
        List<ItemStack> all = new ArrayList<>();
        for (EntityEnchantment enchantment : EntityEnchantmentRegistry.getRegistry()) {
            int max = enchantment.getNaturalMax();
            for (int level = 1; level <= max; level++) {
                Map<EntityEnchantment, Integer> enchantments = new HashMap<>();
                enchantments.put(enchantment, level);
                ItemStack scroll = getDefaultInstance();
                setEnchantments(scroll, enchantments);
                all.add(scroll);
            }
        }
        return all;
    }

    public Map<EntityEnchantment, Integer> getEnchantments(ItemStack stack) {
        Map<EntityEnchantment, Integer> enchantments = new HashMap<>();
        CompoundTag tag = stack.getOrCreateTagElement(Main.MOD_ID);
        if (tag.contains("Enchantments", Tag.TAG_COMPOUND)) {
            CompoundTag nbt = tag.getCompound("Enchantments");
            for (String key : nbt.getAllKeys()) {
                if (nbt.contains(key, Tag.TAG_INT)) {
                    enchantments.put(EntityEnchantmentRegistry.getRegistry().getValue(new ResourceLocation(key)), nbt.getInt(key));
                }
            }
        }
        return enchantments;
    }

    public void setEnchantments(ItemStack stack, Map<EntityEnchantment, Integer> enchantments) {
        CompoundTag tag = new CompoundTag();
        IForgeRegistry<EntityEnchantment> registry = EntityEnchantmentRegistry.getRegistry();
        enchantments.forEach((enchantment, level) -> {
            tag.putInt(registry.getKey(enchantment).toString(), level);
        });
        stack.getOrCreateTagElement(Main.MOD_ID).put("Enchantments", tag);
    }

    public boolean applyTo(ItemStack stack, LivingEntity target) {
        boolean result = false;
        EntityEnchantments enchantments = EntityEnchantments.get(target);
        for (Map.Entry<EntityEnchantment, Integer> entry : getEnchantments(stack).entrySet()) {
            EntityEnchantment enchantment = entry.getKey();
            int level = entry.getValue();
            if (enchantments.getLevel(enchantment) < level && EntityEnchantments.setEnchantment(target, enchantment, level)) {
                result = true;
            }
        }
        return result;
    }
}
