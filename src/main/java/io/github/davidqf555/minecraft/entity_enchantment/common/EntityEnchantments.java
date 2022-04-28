package io.github.davidqf555.minecraft.entity_enchantment.common;

import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.EntityEnchantment;
import io.github.davidqf555.minecraft.entity_enchantment.common.packets.UpdateClientEntityEnchantmentsPacket;
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

    public static boolean isEnchanted(LivingEntity entity) {
        CompoundNBT data = entity.getPersistentData();
        if (data.contains(Main.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT tag = data.getCompound(Main.MOD_ID);
            for (String key : tag.getAllKeys()) {
                if (tag.contains(key, Constants.NBT.TAG_INT) && tag.getInt(key) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void setEnchantments(Entity entity, Map<EntityEnchantment, Integer> enchantments) {
        CompoundNBT data = entity.getPersistentData();
        CompoundNBT tag = new CompoundNBT();
        enchantments.forEach((enchantment, level) -> tag.putInt(enchantment.toString(), level));
        data.put(Main.MOD_ID, tag);
    }

    public static EntityEnchantments get(LivingEntity entity) {
        return entity.getCapability(Provider.capability).orElseGet(EntityEnchantments::new);
    }

    public void onTick(LivingEntity entity) {
        getAllEnchantments().forEach((enchantment, level) -> {
            enchantment.onTick(entity, level);
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
