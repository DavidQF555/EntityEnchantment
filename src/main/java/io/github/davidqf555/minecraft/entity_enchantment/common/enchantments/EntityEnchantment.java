package io.github.davidqf555.minecraft.entity_enchantment.common.enchantments;

import io.github.davidqf555.minecraft.entity_enchantment.common.registration.EntityEnchantmentRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class EntityEnchantment extends ForgeRegistryEntry<EntityEnchantment> {

    private final int max;
    private String id;

    public EntityEnchantment(int max) {
        this.max = max;
    }

    public void onStart(LivingEntity entity, int level) {

    }

    public void onEnd(LivingEntity entity, int level) {

    }

    public void onTick(LivingEntity entity, int level) {

    }

    public int getNaturalMax() {
        return max;
    }

    public boolean isValid(LivingEntity entity, int level) {
        return true;
    }

    public String getID() {
        if (id == null) {
            id = Util.makeDescriptionId("entity_enchantment", EntityEnchantmentRegistry.getRegistry().getKey(this));
        }
        return id;
    }

    public ITextComponent getDisplayName(int level) {
        IFormattableTextComponent text = new TranslationTextComponent(getID()).withStyle(TextFormatting.GRAY);
        if (level != 1 || getNaturalMax() != 1) {
            text.append(" ").append(new TranslationTextComponent("enchantment.level." + level));
        }
        return text;
    }

}
