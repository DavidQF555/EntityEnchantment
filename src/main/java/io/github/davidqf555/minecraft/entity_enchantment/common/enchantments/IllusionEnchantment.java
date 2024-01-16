package io.github.davidqf555.minecraft.entity_enchantment.common.enchantments;

import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.packets.UpdateClientIllusionTicksPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Function;

public class IllusionEnchantment extends EntityEnchantment {

    private final Function<Integer, Integer> duration, count;

    public IllusionEnchantment(int max, int weight, Function<Integer, Integer> duration, Function<Integer, Integer> count) {
        super(max, weight);
        this.duration = duration;
        this.count = count;
    }

    public static void setIllusionDuration(Entity entity, int ticks) {
        CompoundTag tag = entity.getPersistentData();
        CompoundTag data;
        if (tag.contains(Main.MOD_ID, Tag.TAG_COMPOUND)) {
            data = tag.getCompound(Main.MOD_ID);
        } else {
            data = new CompoundTag();
            tag.put(Main.MOD_ID, data);
        }
        data.putInt("Illusion Duration", ticks);
    }

    public static int getIllusionDuration(Entity entity) {
        CompoundTag tag = entity.getPersistentData();
        if (tag.contains(Main.MOD_ID, Tag.TAG_COMPOUND)) {
            CompoundTag data = tag.getCompound(Main.MOD_ID);
            return data.contains("Illusion Duration", Tag.TAG_INT) ? data.getInt("Illusion Duration") : 0;
        } else {
            return 0;
        }
    }

    public static void setTotalIllusionDuration(Entity entity, int ticks) {
        CompoundTag tag = entity.getPersistentData();
        CompoundTag data;
        if (tag.contains(Main.MOD_ID, Tag.TAG_COMPOUND)) {
            data = tag.getCompound(Main.MOD_ID);
        } else {
            data = new CompoundTag();
            tag.put(Main.MOD_ID, data);
        }
        data.putInt("Illusion Total Duration", ticks);
    }

    public static int getTotalIllusionDuration(Entity entity) {
        CompoundTag tag = entity.getPersistentData();
        if (tag.contains(Main.MOD_ID, Tag.TAG_COMPOUND)) {
            CompoundTag data = tag.getCompound(Main.MOD_ID);
            return data.contains("Illusion Total Duration", Tag.TAG_INT) ? data.getInt("Illusion Total Duration") : 0;
        } else {
            return 0;
        }
    }

    public static Vec3[] getIllusionOffsets(Entity entity) {
        CompoundTag tag = entity.getPersistentData();
        if (tag.contains(Main.MOD_ID, Tag.TAG_COMPOUND)) {
            CompoundTag data = tag.getCompound(Main.MOD_ID);
            if (data.contains("Illusion Offsets", Tag.TAG_LIST)) {
                ListTag list = data.getList("Illusion Offsets", Tag.TAG_COMPOUND);
                Vec3[] offsets = new Vec3[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    CompoundTag nbt = list.getCompound(i);
                    offsets[i] = new Vec3(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
                }
                return offsets;
            }
        }
        return new Vec3[0];
    }

    public static void setIllusionOffsets(Entity entity, Vec3[] offsets) {
        CompoundTag tag = entity.getPersistentData();
        CompoundTag data;
        if (tag.contains(Main.MOD_ID, Tag.TAG_COMPOUND)) {
            data = tag.getCompound(Main.MOD_ID);
        } else {
            data = new CompoundTag();
            tag.put(Main.MOD_ID, data);
        }
        ListTag list = new ListTag();
        for (Vec3 offset : offsets) {
            CompoundTag nbt = new CompoundTag();
            nbt.putDouble("X", offset.x());
            nbt.putDouble("Y", offset.y());
            nbt.putDouble("Z", offset.z());
            list.add(nbt);
        }
        data.put("Illusion Offsets", list);
    }

    @Override
    public void onTick(LivingEntity entity, int level) {
        int ticks = getIllusionDuration(entity);
        if (ticks > 0) {
            setIllusionDuration(entity, ticks - 1);
            Main.CHANNEL.send(new UpdateClientIllusionTicksPacket(entity.getId(), ticks - 1, getTotalIllusionDuration(entity), getIllusionOffsets(entity)), PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity));
        }
    }

    @Override
    public void onDamaged(LivingEntity entity, int level, DamageSource source, float damage) {
        int current = getIllusionDuration(entity);
        if (current <= 0) {
            int count = this.count.apply(level);
            RandomSource rand = entity.getRandom();
            Vec3[] offsets = new Vec3[count];
            for (int i = 0; i < count; i++) {
                offsets[i] = new Vec3(rand.nextDouble() * 7 - 3, rand.nextDouble() * 3 - 1, rand.nextDouble() * 7 - 3);
            }
            setIllusionOffsets(entity, offsets);
            int duration = this.duration.apply(level);
            setTotalIllusionDuration(entity, duration);
            setIllusionDuration(entity, duration);
        } else {
            setIllusionDuration(entity, Math.min(current, Math.min(10, getTotalIllusionDuration(entity) / 2)));
        }
    }

}
