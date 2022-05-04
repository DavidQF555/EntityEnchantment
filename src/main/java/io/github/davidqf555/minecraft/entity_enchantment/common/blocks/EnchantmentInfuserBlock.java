package io.github.davidqf555.minecraft.entity_enchantment.common.blocks;

import io.github.davidqf555.minecraft.entity_enchantment.common.items.EnchantedScrollItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;

public class EnchantmentInfuserBlock extends ScrollBlock {

    private static final double VERTICAL_RANGE = 2, HORIZONTAL_RANGE = 10;

    public EnchantmentInfuserBlock(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult clip) {
        if (hand == Hand.MAIN_HAND) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof ScrollTileEntity) {
                ScrollTileEntity scroll = (ScrollTileEntity) te;
                ItemStack inv = scroll.getItem();
                if (inv.isEmpty()) {
                    ItemStack stack = player.getItemInHand(hand);
                    if (!stack.isEmpty() && stack.getItem() instanceof EnchantedScrollItem) {
                        scroll.setItem(stack.split(1));
                        return ActionResultType.CONSUME;
                    }
                } else if (player.isCrouching()) {
                    InventoryHelper.dropContents(world, pos, scroll);
                    scroll.clearContent();
                    return ActionResultType.CONSUME;
                } else if (!world.isClientSide()) {
                    Direction dir = state.getValue(FACING);
                    Vector3d start = Vector3d.atCenterOf(pos).add(Vector3d.atLowerCornerOf(dir.getNormal()).scale(0.5));
                    AxisAlignedBB bounds = AxisAlignedBB.ofSize(HORIZONTAL_RANGE, VERTICAL_RANGE, HORIZONTAL_RANGE).move(start.add(Vector3d.atLowerCornerOf(dir.getNormal()).scale(HORIZONTAL_RANGE / 2)));
                    List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, bounds);
                    entities.sort(Comparator.comparingDouble(entity -> start.distanceToSqr(entity.position())));
                    for (LivingEntity entity : entities) {
                        if (((EnchantedScrollItem) inv.getItem()).applyTo(inv, entity)) {
                            scroll.clearContent();
                            return ActionResultType.CONSUME;
                        }
                    }
                }
            }
        }
        return ActionResultType.PASS;
    }

}
