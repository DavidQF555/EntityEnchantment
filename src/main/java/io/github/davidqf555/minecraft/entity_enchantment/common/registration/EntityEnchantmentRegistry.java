package io.github.davidqf555.minecraft.entity_enchantment.common.registration;

import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.AttributeModificationEnchantment;
import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.EntityEnchantment;
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
    public static final RegistryObject<AttributeModificationEnchantment> STRENGTH = register("strength", () -> new AttributeModificationEnchantment(UUID.fromString("eb19a5fb-f2bf-4610-8bf6-5fcaf80fcff9"), new AttributeModificationEnchantment.Modifier(Attributes.ATTACK_DAMAGE, 1.5, AttributeModifier.Operation.MULTIPLY_TOTAL)));
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
