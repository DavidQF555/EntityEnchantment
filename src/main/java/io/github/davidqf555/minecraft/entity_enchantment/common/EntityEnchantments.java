package io.github.davidqf555.minecraft.entity_enchantment.common;

import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.EntityEnchantment;
import io.github.davidqf555.minecraft.entity_enchantment.common.packets.UpdateClientEntityEnchantmentsPacket;
import io.github.davidqf555.minecraft.entity_enchantment.registration.EntityEnchantmentRegistry;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class EntityEnchantments implements INBTSerializable<CompoundTag> {

    private final Map<EntityEnchantment, Integer> enchantments;

    public EntityEnchantments() {
        enchantments = new HashMap<>();
    }

    public static boolean addEnchantment(LivingEntity entity, EntityEnchantment enchantment, int level) {
        return setEnchantment(entity, enchantment, get(entity).getLevel(enchantment) + level);
    }

    public static boolean setEnchantment(LivingEntity entity, EntityEnchantment enchantment, int level) {
        EntityEnchantments enchantments = get(entity);
        int current = enchantments.getLevel(enchantment);
        if (current != level && enchantment.isValid(entity, level)) {
            if (current > 0) {
                enchantment.onEnd(entity, current);
            }
            enchantments.setLevel(enchantment, level);
            if (level > 0) {
                enchantment.onStart(entity, level);
            }
            Map<EntityEnchantment, Integer> levels = enchantments.getAllEnchantments();
            setEnchantmentsData(entity, levels);
            Main.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new UpdateClientEntityEnchantmentsPacket(entity.getId(), levels));
            return true;
        }
        return false;
    }

    public static boolean isDataEnchanted(LivingEntity entity) {
        CompoundTag data = entity.getPersistentData();
        if (data.contains(Main.MOD_ID, Tag.TAG_COMPOUND)) {
            CompoundTag tag = data.getCompound(Main.MOD_ID);
            for (String key : tag.getAllKeys()) {
                if (tag.contains(key, Tag.TAG_INT) && tag.getInt(key) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void setEnchantmentsData(Entity entity, Map<EntityEnchantment, Integer> enchantments) {
        CompoundTag data = entity.getPersistentData();
        CompoundTag tag = new CompoundTag();
        enchantments.forEach((enchantment, level) -> tag.putInt(enchantment.toString(), level));
        data.put(Main.MOD_ID, tag);
    }

    public static EntityEnchantments get(LivingEntity entity) {
        return entity.getCapability(Provider.CAPABILITY).orElseGet(EntityEnchantments::new);
    }

    public void onTick(LivingEntity entity) {
        getAllEnchantments().forEach((enchantment, level) -> {
            if (level > 0) {
                enchantment.onTick(entity, level);
            }
        });
    }

    public void onDamaged(LivingEntity entity, DamageSource source, float damage) {
        getAllEnchantments().forEach(((enchantment, level) -> {
            if (level > 0) {
                enchantment.onDamaged(entity, level, source, damage);
            }
        }));
    }

    public void onAttack(LivingEntity entity, LivingEntity target, float damage) {
        getAllEnchantments().forEach((enchantment, level) -> {
            if (level > 0) {
                enchantment.onAttack(entity, level, target, damage);
            }
        });
    }

    public void setLevel(EntityEnchantment enchantment, int level) {
        enchantments.put(enchantment, level);
    }

    public int getLevel(EntityEnchantment enchantment) {
        return enchantments.getOrDefault(enchantment, 0);
    }

    public Map<EntityEnchantment, Integer> getAllEnchantments() {
        return enchantments;
    }

    public boolean isEmpty() {
        return getAllEnchantments().isEmpty() || getAllEnchantments().values().stream().noneMatch(level -> level > 0);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        IForgeRegistry<EntityEnchantment> registry = EntityEnchantmentRegistry.getRegistry();
        for (EntityEnchantment enchantment : EntityEnchantmentRegistry.getRegistry()) {
            tag.putInt(registry.getKey(enchantment).toString(), getLevel(enchantment));
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        IForgeRegistry<EntityEnchantment> registry = EntityEnchantmentRegistry.getRegistry();
        for (String key : nbt.getAllKeys()) {
            if (nbt.contains(key, Tag.TAG_INT)) {
                setLevel(registry.getValue(new ResourceLocation(key)), nbt.getInt(key));
            }
        }
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {

        public static final ResourceLocation ID = new ResourceLocation(Main.MOD_ID, "entity_enchantments");
        public static final Capability<EntityEnchantments> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
        });
        private final LazyOptional<EntityEnchantments> instance = LazyOptional.of(EntityEnchantments::new);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
            return cap == CAPABILITY ? instance.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return instance.orElseThrow(NullPointerException::new).serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            instance.orElseThrow(NullPointerException::new).deserializeNBT(nbt);
        }
    }
}
