package io.github.davidqf555.minecraft.entity_enchantment.common.blocks;

import io.github.davidqf555.minecraft.entity_enchantment.common.registration.ItemRegistry;
import io.github.davidqf555.minecraft.entity_enchantment.common.registration.TileEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class ScrollTileEntity extends TileEntity implements IInventory {

    private ItemStack item;

    protected ScrollTileEntity(TileEntityType<?> type) {
        super(type);
        item = ItemStack.EMPTY;
    }

    public ScrollTileEntity() {
        this(TileEntityRegistry.SCROLL.get());
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt = super.save(nbt);
        nbt.put("Item", getItem().save(new CompoundNBT()));
        return nbt;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        if (nbt.contains("Item", Constants.NBT.TAG_COMPOUND)) {
            setItem(ItemStack.of(nbt.getCompound("Item")));
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        deserializeNBT(pkt.getTag());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), 0, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    public ItemStack getItem() {
        return getItem(0);
    }

    public void setItem(ItemStack stack) {
        setItem(0, stack);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return getItem().isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? item : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int amount) {
        if (index == 0) {
            ItemStack stack = getItem();
            ItemStack split = stack.split(amount);
            setChanged();
            return split;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        if (index == 0) {
            ItemStack stack = getItem();
            setItem(ItemStack.EMPTY);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index == 0) {
            item = stack;
            setChanged();
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @Override
    public void clearContent() {
        setItem(ItemStack.EMPTY);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return index == 0 && stack.getItem().equals(ItemRegistry.SCROLL.get());
    }

}
