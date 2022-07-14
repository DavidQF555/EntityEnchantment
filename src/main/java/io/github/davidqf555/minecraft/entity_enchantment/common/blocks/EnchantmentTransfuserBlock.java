package io.github.davidqf555.minecraft.entity_enchantment.common.blocks;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.entity_enchantment.common.EntityEnchantments;
import io.github.davidqf555.minecraft.entity_enchantment.common.ServerConfigs;
import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.EntityEnchantment;
import io.github.davidqf555.minecraft.entity_enchantment.common.items.EnchantedScrollItem;
import io.github.davidqf555.minecraft.entity_enchantment.common.registration.EntityEnchantmentRegistry;
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
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentTransfuserBlock extends ContainerBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<ScrollState> SCROLL = EnumProperty.create("scroll", ScrollState.class);
    public static final VoxelShape SHAPE_COMMON = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    public static final VoxelShape SHAPE_WEST = VoxelShapes.or(Block.box(4.0D, 2.0D, 4.0D, 8.0D, 14.0D, 12), Block.box(1.0D, 10.0D, 0.0D, 5.333333D, 14.0D, 16.0D), Block.box(5.333333D, 12.0D, 0.0D, 9.666667D, 16.0D, 16.0D), Block.box(9.666667D, 14.0D, 0.0D, 14.0D, 18.0D, 16.0D), SHAPE_COMMON);
    public static final VoxelShape SHAPE_NORTH = VoxelShapes.or(Block.box(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 8), Block.box(0.0D, 10.0D, 1.0D, 16.0D, 14.0D, 5.333333D), Block.box(0.0D, 12.0D, 5.333333D, 16.0D, 16.0D, 9.666667D), Block.box(0.0D, 14.0D, 9.666667D, 16.0D, 18.0D, 14.0D), SHAPE_COMMON);
    public static final VoxelShape SHAPE_EAST = VoxelShapes.or(Block.box(8.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12), Block.box(15.0D, 10.0D, 0.0D, 10.666667D, 14.0D, 16.0D), Block.box(10.666667D, 12.0D, 0.0D, 6.333333D, 16.0D, 16.0D), Block.box(6.333333D, 14.0D, 0.0D, 2.0D, 18.0D, 16.0D), SHAPE_COMMON);
    public static final VoxelShape SHAPE_SOUTH = VoxelShapes.or(Block.box(4.0D, 2.0D, 8.0D, 12.0D, 14.0D, 12), Block.box(0.0D, 10.0D, 15.0D, 16.0D, 14.0D, 10.666667D), Block.box(0.0D, 12.0D, 10.666667D, 16.0D, 16.0D, 6.333333D), Block.box(0.0D, 14.0D, 6.333333D, 16.0D, 18.0D, 2.0D), SHAPE_COMMON);

    public EnchantmentTransfuserBlock(Properties properties) {
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
                    if (!stack.isEmpty()) {
                        if (stack.getItem() instanceof EnchantedScrollItem) {
                            scroll.setItem(stack.split(1));
                            world.setBlockAndUpdate(pos, state.setValue(SCROLL, ScrollState.ENCHANTED));
                            return ActionResultType.CONSUME;
                        } else if (stack.getItem().equals(ItemRegistry.SCROLL.get())) {
                            scroll.setItem(stack.split(1));
                            world.setBlockAndUpdate(pos, state.setValue(SCROLL, ScrollState.SCROLL));
                            return ActionResultType.CONSUME;
                        }
                    }
                } else if (player.isCrouching()) {
                    Vector3d drop = Vector3d.upFromBottomCenterOf(pos, 1);
                    InventoryHelper.dropItemStack(world, drop.x(), drop.y(), drop.z(), scroll.getItem());
                    scroll.clearContent();
                    world.setBlockAndUpdate(pos, state.setValue(SCROLL, ScrollState.EMPTY));
                    return ActionResultType.CONSUME;
                } else if (world instanceof ServerWorld) {
                    int cost = ServerConfigs.INSTANCE.shifterCost.get();
                    if (player.experienceLevel >= cost || player.isCreative()) {
                        Direction dir = state.getValue(FACING).getOpposite();
                        Vector3d start = Vector3d.atCenterOf(pos).add(Vector3d.atLowerCornerOf(dir.getNormal()).scale(0.5));
                        double width = ServerConfigs.INSTANCE.shifterWidth.get();
                        AxisAlignedBB bounds = AxisAlignedBB.ofSize(width, ServerConfigs.INSTANCE.shifterHeight.get(), width).move(start.add(Vector3d.atLowerCornerOf(dir.getNormal()).scale(width / 2)));
                        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, bounds);
                        entities.sort(Comparator.comparingDouble(entity -> start.distanceToSqr(entity.position())));
                        if (inv.getItem() instanceof EnchantedScrollItem) {
                            for (LivingEntity entity : entities) {
                                if (((EnchantedScrollItem) inv.getItem()).applyTo(inv, entity)) {
                                    addTrail((ServerWorld) world, start, entity.getEyePosition(1));
                                    scroll.clearContent();
                                    world.setBlockAndUpdate(pos, state.setValue(SCROLL, ScrollState.EMPTY));
                                    player.giveExperienceLevels(-cost);
                                    return ActionResultType.CONSUME;
                                }
                            }
                        } else if (inv.getItem().equals(ItemRegistry.SCROLL.get())) {
                            for (LivingEntity entity : entities) {
                                EntityEnchantments data = EntityEnchantments.get(entity);
                                if (!data.isEmpty()) {
                                    Map<EntityEnchantment, Integer> original = new HashMap<>(data.getAllEnchantments());
                                    Map<EntityEnchantment, Integer> enchantments = new HashMap<>();
                                    Pair<EntityEnchantment, Integer> siphon = EnchantedScrollItem.siphonRandom(original, player.getRandom());
                                    if (siphon != null) {
                                        enchantments.put(siphon.getFirst(), siphon.getSecond());
                                        for (EntityEnchantment enchantment : EntityEnchantmentRegistry.getRegistry()) {
                                            EntityEnchantments.setEnchantment(entity, enchantment, original.getOrDefault(enchantment, 0));
                                        }
                                        ItemStack enchanted = ItemRegistry.ENCHANTED_SCROLL.get().getDefaultInstance();
                                        ((EnchantedScrollItem) enchanted.getItem()).setEnchantments(enchanted, enchantments);
                                        scroll.setItem(enchanted);
                                        addTrail((ServerWorld) world, start, entity.getEyePosition(1));
                                        world.setBlockAndUpdate(pos, state.setValue(SCROLL, ScrollState.ENCHANTED));
                                        player.giveExperienceLevels(-cost);
                                        return ActionResultType.CONSUME;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return ActionResultType.PASS;
    }

    private void addTrail(ServerWorld world, Vector3d start, Vector3d end) {
        double length = end.subtract(start).length();
        int count = MathHelper.ceil(length * 4);
        Vector3d norm = end.subtract(start).scale(1 / length);
        for (int i = 0; i < count; i++) {
            Vector3d pos = start.add(norm.scale(length / count * i));
            world.sendParticles(ParticleTypes.ENCHANT, pos.x(), pos.y(), pos.z(), 5, 0.1, 0.1, 0.1, 0.05);
        }
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOcclusionShape(BlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_) {
        switch (p_196247_1_.getValue(FACING)) {
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case EAST:
                return SHAPE_EAST;
            case WEST:
                return SHAPE_WEST;
            default:
                return SHAPE_COMMON;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean useShapeForLightOcclusion(BlockState p_220074_1_) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
        switch (p_220071_1_.getValue(FACING)) {
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case EAST:
                return SHAPE_EAST;
            case WEST:
                return SHAPE_WEST;
            default:
                return SHAPE_COMMON;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        switch (p_220053_1_.getValue(FACING)) {
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case EAST:
                return SHAPE_EAST;
            case WEST:
                return SHAPE_WEST;
            default:
                return SHAPE_COMMON;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
        return p_185499_1_.setValue(FACING, p_185499_2_.rotate(p_185499_1_.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
        return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
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
        builder.add(FACING, SCROLL);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader reader) {
        return new ScrollTileEntity();
    }

    public enum ScrollState implements IStringSerializable {

        EMPTY,
        SCROLL,
        ENCHANTED;

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }
    }
}
