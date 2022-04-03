package io.github.davidqf555.minecraft.entity_enchantment.common;

import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.EntityEnchantment;
import io.github.davidqf555.minecraft.entity_enchantment.common.packets.UpdateEnchantedEntityPacket;
import io.github.davidqf555.minecraft.entity_enchantment.common.registration.EntityEnchantmentRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EntityEnchantments implements INBTSerializable<CompoundNBT> {

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
        if (level <= 0) {
            if (current > 0) {
                enchantment.onEnd(entity, current);
                enchantments.setLevel(enchantment, 0);
                boolean prev = isEnchanted(entity);
                setEnchanted(entity, anyEnchantments(entity));
                boolean curr = isEnchanted(entity);
                if (prev != curr) {
                    Main.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new UpdateEnchantedEntityPacket(entity.getId(), curr));
                }
                return true;
            }
        } else if (enchantment.isValid(entity, level) && level != current) {
            enchantment.onEnd(entity, level);
            enchantments.setLevel(enchantment, level);
            enchantment.onStart(entity, level);
            boolean prev = isEnchanted(entity);
            setEnchanted(entity, true);
            if (!prev) {
                Main.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new UpdateEnchantedEntityPacket(entity.getId(), true));
            }
            return true;
        }
        return false;
    }

    public static boolean anyEnchantments(LivingEntity entity) {
        EntityEnchantments enchantments = get(entity);
        return EntityEnchantmentRegistry.getRegistry().getValues().stream().anyMatch(enchantment -> enchantments.getLevel(enchantment) > 0);
    }

    public static boolean isEnchanted(LivingEntity entity) {
        CompoundNBT tag = entity.getPersistentData().getCompound(Main.MOD_ID);
        return tag.contains("Enchanted", Constants.NBT.TAG_BYTE) && tag.getBoolean("Enchanted");
    }

    public static void setEnchanted(Entity entity, boolean enchanted) {
        CompoundNBT data = entity.getPersistentData();
        if (data.contains(Main.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
            data.getCompound(Main.MOD_ID).putBoolean("Enchanted", enchanted);
        } else {
            CompoundNBT tag = new CompoundNBT();
            tag.putBoolean("Enchanted", enchanted);
            data.put(Main.MOD_ID, tag);
        }
    }

    public static EntityEnchantments get(LivingEntity entity) {
        return entity.getCapability(Provider.capability).orElseGet(EntityEnchantments::new);
    }

    public void setLevel(EntityEnchantment enchantment, int level) {
        enchantments.put(enchantment, level);
    }

    public int getLevel(EntityEnchantment enchantment) {
        return enchantments.getOrDefault(enchantment, 0);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        for (EntityEnchantment enchantment : EntityEnchantmentRegistry.getRegistry()) {
            tag.putInt(enchantment.getRegistryName().toString(), getLevel(enchantment));
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        IForgeRegistry<EntityEnchantment> registry = EntityEnchantmentRegistry.getRegistry();
        for (String key : nbt.getAllKeys()) {
            if (nbt.contains(key, Constants.NBT.TAG_INT)) {
                setLevel(registry.getValue(new ResourceLocation(key)), nbt.getInt(key));
            }
        }
    }

    public static class Provider implements ICapabilitySerializable<INBT> {

        @CapabilityInject(EntityEnchantments.class)
        public static Capability<EntityEnchantments> capability = null;
        private final LazyOptional<EntityEnchantments> instance = LazyOptional.of(() -> Objects.requireNonNull(capability.getDefaultInstance()));

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
            return cap == capability ? instance.cast() : LazyOptional.empty();
        }

        @Override
        public INBT serializeNBT() {
            return capability.getStorage().writeNBT(capability, instance.orElseThrow(NullPointerException::new), null);
        }

        @Override
        public void deserializeNBT(INBT nbt) {
            capability.getStorage().readNBT(capability, instance.orElseThrow(NullPointerException::new), null, nbt);
        }
    }

    public static class Storage implements Capability.IStorage<EntityEnchantments> {

        @Override
        public INBT writeNBT(Capability<EntityEnchantments> capability, EntityEnchantments instance, Direction side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<EntityEnchantments> capability, EntityEnchantments instance, Direction side, INBT nbt) {
            instance.deserializeNBT((CompoundNBT) nbt);
        }
    }
}
