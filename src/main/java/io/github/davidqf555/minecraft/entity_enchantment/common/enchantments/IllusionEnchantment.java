package io.github.davidqf555.minecraft.entity_enchantment.common.enchantments;

import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.packets.UpdateClientIllusionTicksPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Random;
import java.util.function.Function;

public class IllusionEnchantment extends EntityEnchantment {

    private final Function<Integer, Integer> duration, count;

    public IllusionEnchantment(int max, int weight, Function<Integer, Integer> duration, Function<Integer, Integer> count) {
        super(max, weight);
        this.duration = duration;
        this.count = count;
    }

    public static void setIllusionDuration(Entity entity, int ticks) {
        CompoundNBT tag = entity.getPersistentData();
        CompoundNBT data;
        if (tag.contains(Main.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
            data = tag.getCompound(Main.MOD_ID);
        } else {
            data = new CompoundNBT();
            tag.put(Main.MOD_ID, data);
        }
        data.putInt("Illusion Duration", ticks);
    }

    public static int getIllusionDuration(Entity entity) {
        CompoundNBT tag = entity.getPersistentData();
        if (tag.contains(Main.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT data = tag.getCompound(Main.MOD_ID);
            return data.contains("Illusion Duration", Constants.NBT.TAG_INT) ? data.getInt("Illusion Duration") : 0;
        } else {
            return 0;
        }
    }

    public static void setTotalIllusionDuration(Entity entity, int ticks) {
        CompoundNBT tag = entity.getPersistentData();
        CompoundNBT data;
        if (tag.contains(Main.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
            data = tag.getCompound(Main.MOD_ID);
        } else {
            data = new CompoundNBT();
            tag.put(Main.MOD_ID, data);
        }
        data.putInt("Illusion Total Duration", ticks);
    }

    public static int getTotalIllusionDuration(Entity entity) {
        CompoundNBT tag = entity.getPersistentData();
        if (tag.contains(Main.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT data = tag.getCompound(Main.MOD_ID);
            return data.contains("Illusion Total Duration", Constants.NBT.TAG_INT) ? data.getInt("Illusion Total Duration") : 0;
        } else {
            return 0;
        }
    }

    public static Vector3d[] getIllusionOffsets(Entity entity) {
        CompoundNBT tag = entity.getPersistentData();
        if (tag.contains(Main.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT data = tag.getCompound(Main.MOD_ID);
            if (data.contains("Illusion Offsets", Constants.NBT.TAG_LIST)) {
                ListNBT list = data.getList("Illusion Offsets", Constants.NBT.TAG_COMPOUND);
                Vector3d[] offsets = new Vector3d[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    CompoundNBT nbt = list.getCompound(i);
                    offsets[i] = new Vector3d(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
                }
                return offsets;
            }
        }
        return new Vector3d[0];
    }

    public static void setIllusionOffsets(Entity entity, Vector3d[] offsets) {
        CompoundNBT tag = entity.getPersistentData();
        CompoundNBT data;
        if (tag.contains(Main.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
            data = tag.getCompound(Main.MOD_ID);
        } else {
            data = new CompoundNBT();
            tag.put(Main.MOD_ID, data);
        }
        ListNBT list = new ListNBT();
        for (Vector3d offset : offsets) {
            CompoundNBT nbt = new CompoundNBT();
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
            Main.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new UpdateClientIllusionTicksPacket(entity.getId(), ticks - 1, getTotalIllusionDuration(entity), getIllusionOffsets(entity)));
        }
    }

    @Override
    public void onDamaged(LivingEntity entity, int level, DamageSource source, float damage) {
        if (getIllusionDuration(entity) <= 0) {
            int count = this.count.apply(level);
            Random rand = entity.getRandom();
            Vector3d[] offsets = new Vector3d[count];
            for (int i = 0; i < count; i++) {
                offsets[i] = new Vector3d(rand.nextDouble() * 7 - 3, rand.nextDouble() * 2, rand.nextDouble() * 7 - 3);
            }
            setIllusionOffsets(entity, offsets);
            int duration = this.duration.apply(level);
            setTotalIllusionDuration(entity, duration);
            setIllusionDuration(entity, duration);
        } else {
            setIllusionDuration(entity, Math.min(10, getTotalIllusionDuration(entity) / 2));
        }
    }

}
