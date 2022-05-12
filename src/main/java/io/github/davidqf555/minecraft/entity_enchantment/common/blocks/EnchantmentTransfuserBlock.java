package io.github.davidqf555.minecraft.entity_enchantment.common.blocks;

import io.github.davidqf555.minecraft.entity_enchantment.common.EntityEnchantments;
import io.github.davidqf555.minecraft.entity_enchantment.common.ServerConfigs;
import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.EntityEnchantment;
import io.github.davidqf555.minecraft.entity_enchantment.common.items.EnchantedScrollItem;
import io.github.davidqf555.minecraft.entity_enchantment.common.registration.ItemRegistry;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentTransfuserBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<ScrollState> SCROLL = EnumProperty.create("scroll", ScrollState.class);
    public static final VoxelShape SHAPE_COMMON = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    public static final VoxelShape SHAPE_WEST = Shapes.or(Block.box(4.0D, 2.0D, 4.0D, 8.0D, 14.0D, 12), Block.box(1.0D, 10.0D, 0.0D, 5.333333D, 14.0D, 16.0D), Block.box(5.333333D, 12.0D, 0.0D, 9.666667D, 16.0D, 16.0D), Block.box(9.666667D, 14.0D, 0.0D, 14.0D, 18.0D, 16.0D), SHAPE_COMMON);
    public static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 8), Block.box(0.0D, 10.0D, 1.0D, 16.0D, 14.0D, 5.333333D), Block.box(0.0D, 12.0D, 5.333333D, 16.0D, 16.0D, 9.666667D), Block.box(0.0D, 14.0D, 9.666667D, 16.0D, 18.0D, 14.0D), SHAPE_COMMON);
    public static final VoxelShape SHAPE_EAST = Shapes.or(Block.box(8.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12), Block.box(10.666667, 10.0D, 0.0D, 15.0D, 14.0D, 16.0D), Block.box(6.333333D, 12.0D, 0.0D, 10.666667D, 16.0D, 16.0D), Block.box(2, 14.0D, 0.0D, 6.333333D, 18.0D, 16.0D), SHAPE_COMMON);
    public static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(4.0D, 2.0D, 8.0D, 12.0D, 14.0D, 12), Block.box(0.0D, 10.0D, 10.666667D, 16.0D, 14.0D, 15.0D), Block.box(0.0D, 12.0D, 6.333333D, 16.0D, 16.0D, 10.666667D), Block.box(0.0D, 14.0D, 2, 16.0D, 18.0D, 6.333333), SHAPE_COMMON);

    public EnchantmentTransfuserBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult clip) {
        if (hand == InteractionHand.MAIN_HAND) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof ScrollTileEntity) {
                ScrollTileEntity scroll = (ScrollTileEntity) te;
                ItemStack inv = scroll.getItem();
                if (inv.isEmpty()) {
                    ItemStack stack = player.getItemInHand(hand);
                    if (!stack.isEmpty()) {
                        if (stack.getItem() instanceof EnchantedScrollItem) {
                            scroll.setItem(stack.split(1));
                            world.setBlockAndUpdate(pos, state.setValue(SCROLL, ScrollState.ENCHANTED));
                            return InteractionResult.CONSUME;
                        } else if (stack.getItem().equals(ItemRegistry.SCROLL.get())) {
                            scroll.setItem(stack.split(1));
                            world.setBlockAndUpdate(pos, state.setValue(SCROLL, ScrollState.SCROLL));
                            return InteractionResult.CONSUME;
                        }
                    }
                } else if (player.isCrouching()) {
                    Vec3 drop = Vec3.upFromBottomCenterOf(pos, 1);
                    Containers.dropItemStack(world, drop.x(), drop.y(), drop.z(), scroll.getItem());
                    scroll.clearContent();
                    world.setBlockAndUpdate(pos, state.setValue(SCROLL, ScrollState.EMPTY));
                    return InteractionResult.CONSUME;
                } else if (world instanceof ServerLevel) {
                    int cost = ServerConfigs.INSTANCE.shifterCost.get();
                    if (player.experienceLevel >= cost || player.isCreative()) {
                        Direction dir = state.getValue(FACING).getOpposite();
                        Vec3 start = Vec3.atCenterOf(pos).add(Vec3.atLowerCornerOf(dir.getNormal()).scale(0.5));
                        double width = ServerConfigs.INSTANCE.shifterWidth.get();
                        AABB bounds = AABB.ofSize(start.add(Vec3.atLowerCornerOf(dir.getNormal()).scale(width / 2)), width, ServerConfigs.INSTANCE.shifterHeight.get(), width);
                        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, bounds);
                        entities.sort(Comparator.comparingDouble(entity -> start.distanceToSqr(entity.position())));
                        if (inv.getItem() instanceof EnchantedScrollItem) {
                            for (LivingEntity entity : entities) {
                                if (((EnchantedScrollItem) inv.getItem()).applyTo(inv, entity)) {
                                    addTrail((ServerLevel) world, start, entity.getEyePosition(1));
                                    scroll.clearContent();
                                    world.setBlockAndUpdate(pos, state.setValue(SCROLL, ScrollState.EMPTY));
                                    player.giveExperienceLevels(-cost);
                                    return InteractionResult.CONSUME;
                                }
                            }
                        } else if (inv.getItem().equals(ItemRegistry.SCROLL.get())) {
                            for (LivingEntity entity : entities) {
                                EntityEnchantments data = EntityEnchantments.get(entity);
                                if (!data.isEmpty()) {
                                    addTrail((ServerLevel) world, start, entity.getEyePosition(1));
                                    Map<EntityEnchantment, Integer> enchantments = new HashMap<>();
                                    EntityEnchantment selected = Util.getRandom(data.getAllEnchantments().entrySet().stream().filter(entry -> entry.getValue() > 0).map(Map.Entry::getKey).toArray(EntityEnchantment[]::new), player.getRandom());
                                    enchantments.put(selected, data.getLevel(selected));
                                    EntityEnchantments.setEnchantment(entity, selected, 0);
                                    ItemStack enchanted = ItemRegistry.ENCHANTED_SCROLL.get().getDefaultInstance();
                                    ((EnchantedScrollItem) enchanted.getItem()).setEnchantments(enchanted, enchantments);
                                    scroll.setItem(enchanted);
                                    world.setBlockAndUpdate(pos, state.setValue(SCROLL, ScrollState.ENCHANTED));
                                    player.giveExperienceLevels(-cost);
                                    return InteractionResult.CONSUME;
                                }
                            }
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    private void addTrail(ServerLevel world, Vec3 start, Vec3 end) {
        double length = end.subtract(start).length();
        int count = Mth.ceil(length * 4);
        Vec3 norm = end.subtract(start).scale(1 / length);
        for (int i = 0; i < count; i++) {
            Vec3 pos = start.add(norm.scale(length / count * i));
            world.sendParticles(ParticleTypes.ENCHANT, pos.x(), pos.y(), pos.z(), 5, 0.1, 0.1, 0.1, 0.05);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOcclusionShape(BlockState p_196247_1_, BlockGetter p_196247_2_, BlockPos p_196247_3_) {
        return switch (p_196247_1_.getValue(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_COMMON;
        };
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean useShapeForLightOcclusion(BlockState p_220074_1_) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockGetter p_220071_2_, BlockPos p_220071_3_, CollisionContext p_220071_4_) {
        return switch (p_220071_1_.getValue(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_COMMON;
        };
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return switch (p_220053_1_.getValue(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_COMMON;
        };
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState original, boolean update) {
        if (!state.is(original.getBlock())) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof Container) {
                Containers.dropContents(world, pos, (Container) te);
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, original, update);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SCROLL);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ScrollTileEntity(pos, state);
    }

    public enum ScrollState implements StringRepresentable {

        EMPTY,
        SCROLL,
        ENCHANTED;

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }
    }
}
