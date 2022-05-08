package io.github.davidqf555.minecraft.entity_enchantment.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.IllusionEnchantment;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

public class IllusionRenderer<T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {

    private final LivingRenderer<T, M> renderer;

    public IllusionRenderer(LivingRenderer<T, M> renderer) {
        super(renderer);
        this.renderer = renderer;
    }

    @Override
    public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
        float ticks = IllusionEnchantment.getIllusionDuration(p_225628_4_) - p_225628_7_;
        if (ticks > 0) {
            int totalDuration = IllusionEnchantment.getTotalIllusionDuration(p_225628_4_);
            double distFactor = 1 - Math.pow(2, (ticks >= totalDuration / 2f ? ticks - totalDuration : -ticks) / 2);
            M model = getParentModel();
            for (Vector3d offset : IllusionEnchantment.getIllusionOffsets(p_225628_4_)) {
                Vector3d dif = offset.scale(distFactor);
                p_225628_1_.pushPose();
                p_225628_1_.translate(dif.x(), dif.y(), dif.z());
                model.renderToBuffer(p_225628_1_, p_225628_2_.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(p_225628_4_))), p_225628_3_, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
                for (LayerRenderer<T, M> layer : renderer.layers) {
                    if (!equals(layer)) {
                        layer.render(p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_, p_225628_8_, p_225628_9_, p_225628_10_);
                    }
                }
                p_225628_1_.popPose();
            }
        }
    }
}
