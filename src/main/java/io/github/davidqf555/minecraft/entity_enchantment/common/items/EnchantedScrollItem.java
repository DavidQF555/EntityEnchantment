package io.github.davidqf555.minecraft.entity_enchantment.common.items;

import io.github.davidqf555.minecraft.entity_enchantment.common.EntityEnchantments;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.EntityEnchantment;
import io.github.davidqf555.minecraft.entity_enchantment.common.registration.EntityEnchantmentRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SimpleFoiledItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

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
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag tooltip) {
        super.appendHoverText(stack, world, text, tooltip);
        getEnchantments(stack).forEach((enchantment, level) -> text.add(enchantment.getDisplayName(level)));
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (allowdedIn(group)) {
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
        CompoundNBT tag = stack.getOrCreateTagElement(Main.MOD_ID);
        if (tag.contains("Enchantments", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT nbt = tag.getCompound("Enchantments");
            for (String key : nbt.getAllKeys()) {
                if (nbt.contains(key, Constants.NBT.TAG_INT)) {
                    enchantments.put(EntityEnchantmentRegistry.getRegistry().getValue(new ResourceLocation(key)), nbt.getInt(key));
                }
            }
        }
        return enchantments;
    }

    public void setEnchantments(ItemStack stack, Map<EntityEnchantment, Integer> enchantments) {
        CompoundNBT tag = new CompoundNBT();
        enchantments.forEach((enchantment, level) -> {
            tag.putInt(enchantment.getRegistryName().toString(), level);
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
