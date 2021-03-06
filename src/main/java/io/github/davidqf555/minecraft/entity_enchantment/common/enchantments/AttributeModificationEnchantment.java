package io.github.davidqf555.minecraft.entity_enchantment.common.enchantments;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;

import java.util.UUID;
import java.util.function.Function;

public class AttributeModificationEnchantment extends EntityEnchantment {

    private final Modifier[] modifiers;
    private final UUID id;

    public AttributeModificationEnchantment(int max, int weight, UUID id, Modifier... modifiers) {
        super(max, weight);
        this.id = id;
        this.modifiers = modifiers;
    }

    @Override
    public void onStart(LivingEntity entity, int level) {
        AttributeModifierManager attributes = entity.getAttributes();
        for (Modifier modifier : modifiers) {
            ModifiableAttributeInstance inst = attributes.getInstance(modifier.attribute);
            if (inst != null) {
                inst.addPermanentModifier(new AttributeModifier(id, getRegistryName() + " " + level, modifier.amount.apply(level), modifier.operation));
            }
        }
    }

    @Override
    public void onEnd(LivingEntity entity, int level) {
        AttributeModifierManager attributes = entity.getAttributes();
        for (Modifier modifier : modifiers) {
            ModifiableAttributeInstance inst = attributes.getInstance(modifier.attribute);
            if (inst != null) {
                inst.removePermanentModifier(id);
            }
        }
    }

    public static class Modifier {

        private final Attribute attribute;
        private final Function<Integer, Double> amount;
        private final AttributeModifier.Operation operation;

        public Modifier(Attribute attribute, Function<Integer, Double> amount, AttributeModifier.Operation operation) {
            this.attribute = attribute;
            this.amount = amount;
            this.operation = operation;
        }
    }
}
