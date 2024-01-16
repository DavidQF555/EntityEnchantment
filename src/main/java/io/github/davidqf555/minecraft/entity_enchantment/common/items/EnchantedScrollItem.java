package io.github.davidqf555.minecraft.entity_enchantment.common.items;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.entity_enchantment.common.EntityEnchantments;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.EntityEnchantment;
import io.github.davidqf555.minecraft.entity_enchantment.registration.EntityEnchantmentRegistry;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
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

    public static ItemStack getMergeResult(ItemStack original, ItemStack addition) {
        Map<EntityEnchantment, Integer> oLevels = ((EnchantedScrollItem) original.getItem()).getEnchantments(original);
        Map<EntityEnchantment, Integer> aLevels = ((EnchantedScrollItem) addition.getItem()).getEnchantments(addition);
        Map<EntityEnchantment, Integer> merge = getMergeResult(oLevels, aLevels);
        ItemStack out = original.getItem().getDefaultInstance();
        ((EnchantedScrollItem) out.getItem()).setEnchantments(out, merge);
        return out;
    }

    public static Map<EntityEnchantment, Integer> getMergeResult(Map<EntityEnchantment, Integer> original, Map<EntityEnchantment, Integer> addition) {
        Map<EntityEnchantment, Integer> out = new HashMap<>();
        for (EntityEnchantment enchantment : addition.keySet()) {
            int a = addition.get(enchantment);
            if (original.containsKey(enchantment)) {
                int o = original.get(enchantment);
                out.put(enchantment, a == o && a < enchantment.getNaturalMax() ? o + 1 : Math.max(a, o));
            } else {
                out.put(enchantment, a);
            }
        }
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

    @Nullable
    public static Pair<EntityEnchantment, Integer> siphonRandom(Map<EntityEnchantment, Integer> enchantments, RandomSource random) {
        EntityEnchantment[] valid = enchantments.entrySet().stream().filter(entry -> entry.getValue() > 0).map(Map.Entry::getKey).toArray(EntityEnchantment[]::new);
        if (valid.length > 0) {
            EntityEnchantment selected = Util.getRandom(valid, random);
            int original = enchantments.get(selected);
            enchantments.put(selected, original - 1);
            return Pair.of(selected, original - 1);
        }
        return null;
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
        Map<EntityEnchantment, Integer> additions = getEnchantments(stack);
        Map<EntityEnchantment, Integer> merge = getMergeResult(EntityEnchantments.get(target).getAllEnchantments(), additions);
        for (Map.Entry<EntityEnchantment, Integer> entry : merge.entrySet()) {
            EntityEnchantment enchantment = entry.getKey();
            int level = entry.getValue();
            if (EntityEnchantments.setEnchantment(target, enchantment, level)) {
                result = true;
            }
        }
        return result;
    }
}
