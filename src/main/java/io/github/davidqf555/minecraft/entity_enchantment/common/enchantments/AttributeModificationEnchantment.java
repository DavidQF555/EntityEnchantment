package io.github.davidqf555.minecraft.entity_enchantment.common.enchantments;

import io.github.davidqf555.minecraft.entity_enchantment.common.registration.EntityEnchantmentRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.IForgeRegistry;

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
        AttributeMap attributes = entity.getAttributes();
        IForgeRegistry<EntityEnchantment> registry = EntityEnchantmentRegistry.getRegistry();
        for (Modifier modifier : modifiers) {
            AttributeInstance inst = attributes.getInstance(modifier.attribute);
            if (inst != null) {
                inst.addPermanentModifier(new AttributeModifier(id, registry.getKey(this) + " " + level, modifier.amount.apply(level), modifier.operation));
            }
        }
    }

    @Override
    public void onEnd(LivingEntity entity, int level) {
        AttributeMap attributes = entity.getAttributes();
        for (Modifier modifier : modifiers) {
            AttributeInstance inst = attributes.getInstance(modifier.attribute);
            if (inst != null) {
                inst.removePermanentModifier(id);
            }
        }
    }

    public record Modifier(Attribute attribute, Function<Integer, Double> amount,
                           AttributeModifier.Operation operation) {
    }
}
