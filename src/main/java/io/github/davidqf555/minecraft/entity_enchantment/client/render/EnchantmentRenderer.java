package io.github.davidqf555.minecraft.entity_enchantment.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.davidqf555.minecraft.entity_enchantment.common.EntityEnchantments;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;

public class EnchantmentRenderer<T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {

    public EnchantmentRenderer(IEntityRenderer<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int p_225628_3_, T entity, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
        if (EntityEnchantments.isEnchanted(entity)) {
            getParentModel().renderToBuffer(matrixStack, buffer.getBuffer(RenderType.entityGlint()), p_225628_3_, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        }
    }

}
