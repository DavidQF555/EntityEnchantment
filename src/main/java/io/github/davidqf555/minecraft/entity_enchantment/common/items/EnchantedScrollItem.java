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
import net.minecraft.util.text.TextFormatting;
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

    public static ItemStack getMergeResult(ItemStack original, ItemStack addition) {
        ItemStack out = original.getItem().getDefaultInstance();
        Map<EntityEnchantment, Integer> oLevels = ((EnchantedScrollItem) original.getItem()).getEnchantments(original);
        Map<EntityEnchantment, Integer> aLevels = ((EnchantedScrollItem) addition.getItem()).getEnchantments(addition);
        for (EntityEnchantment enchantment : aLevels.keySet()) {
            int a = aLevels.get(enchantment);
            if (oLevels.containsKey(enchantment)) {
                int o = oLevels.get(enchantment);
                oLevels.put(enchantment, a == o && a < enchantment.getNaturalMax() ? o + 1 : Math.max(a, o));
            } else {
                oLevels.put(enchantment, a);
            }
        }
        ((EnchantedScrollItem) out.getItem()).setEnchantments(out, oLevels);
        return out;
    }

    public static int getMergeCost(ItemStack original, ItemStack addition) {
        int cost = 0;
        Map<EntityEnchantment, Integer> oLevels = ((EnchantedScrollItem) original.getItem()).getEnchantments(original);
        Map<EntityEnchantment, Integer> aLevels = ((EnchantedScrollItem) addition.getItem()).getEnchantments(addition);
        for (EntityEnchantment enchantment : aLevels.keySet()) {
            int a = aLevels.get(enchantment);
            int costLevel = 0;
            if (oLevels.containsKey(enchantment)) {
                int o = oLevels.get(enchantment);
                if (a == o && a < enchantment.getNaturalMax()) {
                    oLevels.put(enchantment, o + 1);
                    costLevel = o + 1;
                } else if (o < a) {
                    oLevels.put(enchantment, a);
                    costLevel = a;
                }
            } else {
                oLevels.put(enchantment, a);
                costLevel = a;
            }
            if (costLevel > 0) {
                cost += 2 * costLevel * (1 - enchantment.getWeight() / EntityEnchantment.getTotalWeight());
            }
        }
        return Math.min(40, cost);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag tooltip) {
        super.appendHoverText(stack, world, text, tooltip);
        getEnchantments(stack).forEach((enchantment, level) -> text.add(enchantment.getDisplayName(level).withStyle(TextFormatting.GRAY)));
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
