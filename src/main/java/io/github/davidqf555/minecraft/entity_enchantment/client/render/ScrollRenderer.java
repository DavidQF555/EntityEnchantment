package io.github.davidqf555.minecraft.entity_enchantment.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.blocks.EnchantmentShifterBlock;
import io.github.davidqf555.minecraft.entity_enchantment.common.blocks.ScrollTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class ScrollRenderer extends TileEntityRenderer<ScrollTileEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MOD_ID, "textures/entity/scroll.png");
    private final ScrollModel model;

    public ScrollRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
        model = new ScrollModel();
    }

    @Override
    public void render(ScrollTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
        BlockState state = p_225616_1_.getBlockState();
        p_225616_3_.pushPose();
        p_225616_3_.translate(0.5D, 1.0625D, 0.5D);
        p_225616_3_.mulPose(Vector3f.YP.rotationDegrees(-state.getValue(EnchantmentShifterBlock.FACING).getClockWise().toYRot()));
        p_225616_3_.mulPose(Vector3f.ZN.rotationDegrees(22.5F));
        p_225616_3_.translate(0.125, -0.125D, 0.0D);
        p_225616_3_.mulPose(Vector3f.XP.rotationDegrees(90));
        p_225616_3_.translate(0, 0, -0.125);
        switch (state.getValue(EnchantmentShifterBlock.SCROLL)) {
            case ENCHANTED:
                model.renderToBuffer(p_225616_3_, p_225616_4_.getBuffer(RenderType.entityGlint()), p_225616_5_, p_225616_6_, 1, 1, 1, 1);
            case SCROLL:
                model.renderToBuffer(p_225616_3_, p_225616_4_.getBuffer(model.renderType(TEXTURE)), p_225616_5_, p_225616_6_, 1, 1, 1, 1);
        }
        p_225616_3_.popPose();
    }
}
