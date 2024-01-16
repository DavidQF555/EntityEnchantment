package io.github.davidqf555.minecraft.entity_enchantment.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.blocks.EnchantmentTransfuserBlock;
import io.github.davidqf555.minecraft.entity_enchantment.common.blocks.ScrollTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class ScrollRenderer implements BlockEntityRenderer<ScrollTileEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MOD_ID, "textures/entity/scroll.png");
    private final ScrollModel model;

    public ScrollRenderer(BlockEntityRendererProvider.Context context) {
        model = new ScrollModel(context.bakeLayer(ScrollModel.LOCATION));
    }

    @Override
    public void render(ScrollTileEntity p_225616_1_, float p_225616_2_, PoseStack p_225616_3_, MultiBufferSource p_225616_4_, int p_225616_5_, int p_225616_6_) {
        BlockState state = p_225616_1_.getBlockState();
        p_225616_3_.pushPose();
        p_225616_3_.translate(0.5D, 1.0625D, 0.5D);
        p_225616_3_.mulPose(Axis.YP.rotationDegrees(-state.getValue(EnchantmentTransfuserBlock.FACING).getClockWise().toYRot()));
        p_225616_3_.mulPose(Axis.ZN.rotationDegrees(22.5F));
        p_225616_3_.translate(0.125, -0.125D, 0.0D);
        p_225616_3_.mulPose(Axis.XP.rotationDegrees(90));
        p_225616_3_.translate(0, 0, -0.125);
        switch (state.getValue(EnchantmentTransfuserBlock.SCROLL)) {
            case ENCHANTED:
                model.renderToBuffer(p_225616_3_, p_225616_4_.getBuffer(RenderType.entityGlint()), p_225616_5_, p_225616_6_, 1, 1, 1, 1);
            case SCROLL:
                model.renderToBuffer(p_225616_3_, p_225616_4_.getBuffer(model.renderType(TEXTURE)), p_225616_5_, p_225616_6_, 1, 1, 1, 1);
        }
        p_225616_3_.popPose();
    }
}
