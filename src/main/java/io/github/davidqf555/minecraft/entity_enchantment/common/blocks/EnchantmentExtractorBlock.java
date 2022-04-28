package io.github.davidqf555.minecraft.entity_enchantment.common.blocks;

import io.github.davidqf555.minecraft.entity_enchantment.common.EntityEnchantments;
import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.EntityEnchantment;
import io.github.davidqf555.minecraft.entity_enchantment.common.items.EnchantedScrollItem;
import io.github.davidqf555.minecraft.entity_enchantment.common.registration.ItemRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class EnchantmentExtractorBlock extends ContainerBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final double VERTICAL_RANGE = 2, HORIZONTAL_RANGE = 5;

    public EnchantmentExtractorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
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
                    if (!stack.isEmpty() && stack.getItem().equals(ItemRegistry.SCROLL.get())) {
                        scroll.setItem(stack.split(1));
                        return ActionResultType.CONSUME;
                    }
                } else if (inv.getItem() instanceof EnchantedScrollItem) {
                    InventoryHelper.dropContents(world, pos, scroll);
                    scroll.clearContent();
                    return ActionResultType.CONSUME;
                } else if (!world.isClientSide()) {
                    Direction dir = state.getValue(FACING);
                    Vector3d start = Vector3d.atCenterOf(pos).add(Vector3d.atLowerCornerOf(dir.getNormal()).scale(0.5));
                    AxisAlignedBB bounds = AxisAlignedBB.ofSize(HORIZONTAL_RANGE, VERTICAL_RANGE, HORIZONTAL_RANGE).move(start.add(Vector3d.atLowerCornerOf(dir.getNormal()).scale(HORIZONTAL_RANGE / 2)));
                    LivingEntity nearest = null;
                    for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, bounds)) {
                        if (!EntityEnchantments.get(entity).isEmpty() && (nearest == null || entity.distanceToSqr(start) < nearest.distanceToSqr(start))) {
                            nearest = entity;
                        }
                    }
                    if (nearest != null) {
                        Map<EntityEnchantment, Integer> enchantments = new HashMap<>();
                        EntityEnchantments data = EntityEnchantments.get(nearest);
                        EntityEnchantment selected = Util.getRandom(data.getAllEnchantments().entrySet().stream().filter(entry -> entry.getValue() > 0).map(Map.Entry::getKey).toArray(EntityEnchantment[]::new), player.getRandom());
                        enchantments.put(selected, data.getLevel(selected));
                        data.setLevel(selected, 0);
                        ItemStack enchanted = ItemRegistry.ENCHANTED_SCROLL.get().getDefaultInstance();
                        ((EnchantedScrollItem) enchanted.getItem()).setEnchantments(enchanted, enchantments);
                        scroll.setItem(enchanted);
                        return ActionResultType.CONSUME;
                    }
                }
            }
        }
        return ActionResultType.PASS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState original, boolean update) {
        if (!state.is(original.getBlock())) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof IInventory) {
                InventoryHelper.dropContents(world, pos, (IInventory) te);
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, original, update);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader reader) {
        return new ScrollTileEntity();
    }
}
