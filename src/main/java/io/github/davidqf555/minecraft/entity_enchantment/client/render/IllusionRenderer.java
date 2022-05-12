package io.github.davidqf555.minecraft.entity_enchantment.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.IllusionEnchantment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class IllusionRenderer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private final LivingEntityRenderer<T, M> renderer;

    public IllusionRenderer(LivingEntityRenderer<T, M> renderer) {
        super(renderer);
        this.renderer = renderer;
    }

    @Override
    public void render(PoseStack p_225628_1_, MultiBufferSource p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
        float ticks = IllusionEnchantment.getIllusionDuration(p_225628_4_) - p_225628_7_;
        if (ticks > 0) {
            int totalDuration = IllusionEnchantment.getTotalIllusionDuration(p_225628_4_);
            double distFactor = 1 - Math.pow(2, (ticks >= totalDuration / 2f ? ticks - totalDuration : -ticks) / 2);
            M model = getParentModel();
            for (Vec3 offset : IllusionEnchantment.getIllusionOffsets(p_225628_4_)) {
                Vec3 dif = offset.scale(distFactor);
                p_225628_1_.pushPose();
                p_225628_1_.translate(dif.x(), -dif.y(), dif.z());
                model.renderToBuffer(p_225628_1_, p_225628_2_.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(p_225628_4_))), p_225628_3_, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
                for (RenderLayer<T, M> layer : renderer.layers) {
                    if (!equals(layer)) {
                        layer.render(p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_, p_225628_8_, p_225628_9_, p_225628_10_);
                    }
                }
                p_225628_1_.popPose();
            }
        }
    }
}
