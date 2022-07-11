package io.github.davidqf555.minecraft.entity_enchantment.common.registration;

import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.UUID;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EntityEnchantmentRegistry {

    public static final DeferredRegister<EntityEnchantment> ENCHANTMENTS = DeferredRegister.create(EntityEnchantment.class, Main.MOD_ID);
    public static final RegistryObject<AttributeModificationEnchantment> STRENGTH = register("strength", () -> new AttributeModificationEnchantment(5, 5, UUID.fromString("eb19a5fb-f2bf-4610-8bf6-5fcaf80fcff9"), new AttributeModificationEnchantment.Modifier(Attributes.ATTACK_DAMAGE, level -> Math.pow(1.25, level), AttributeModifier.Operation.MULTIPLY_BASE)));
    public static final RegistryObject<AttributeModificationEnchantment> SPEED = register("speed", () -> new AttributeModificationEnchantment(5, 5, UUID.fromString("96f15dae-cec7-11ec-9d64-0242ac120002"), new AttributeModificationEnchantment.Modifier(Attributes.MOVEMENT_SPEED, level -> Math.pow(1.1, level), AttributeModifier.Operation.MULTIPLY_BASE)));
    public static final RegistryObject<AttributeModificationEnchantment> ARMOR = register("armor", () -> new AttributeModificationEnchantment(5, 5, UUID.fromString("acf22782-cec7-11ec-9d64-0242ac120002"), new AttributeModificationEnchantment.Modifier(Attributes.ARMOR, level -> level * 1.0, AttributeModifier.Operation.ADDITION)));
    public static final RegistryObject<EntityEnchantment> UNSTABLE = register("unstable", () -> new ExplosionEnchantment(2, 2, 5, level -> level * 2f, level -> level >= 2));
    public static final RegistryObject<IllusionEnchantment> ILLUSION = register("illusion", () -> new IllusionEnchantment(3, 4, level -> level * 30, level -> level * 2));
    public static final RegistryObject<FireEnchantment> INFERNAL = register("infernal", () -> new FireEnchantment(5, 2, level -> level * 2));
    public static final RegistryObject<IceEnchantment> FROSTBORN = register("frostborn", () -> new IceEnchantment(5, 3, level -> level * 40, level -> level - 1));
    public static final RegistryObject<ReflectEnchantment> REFLECT = register("reflect", () -> new ReflectEnchantment(3, 5, level -> level * 0.2f));

    private static IForgeRegistry<EntityEnchantment> REGISTRY = null;

    private EntityEnchantmentRegistry() {
    }

    private static <T extends EntityEnchantment> RegistryObject<T> register(String name, Supplier<T> enchantment) {
        return ENCHANTMENTS.register(name, enchantment);
    }

    public static IForgeRegistry<EntityEnchantment> getRegistry() {
        return REGISTRY;
    }

    @SubscribeEvent
    public static void onNewRegistry(RegistryEvent.NewRegistry event) {
        REGISTRY = new RegistryBuilder<EntityEnchantment>().setType(EntityEnchantment.class).setName(new ResourceLocation(Main.MOD_ID, "entity_enchantment")).create();
    }
}
