package io.github.davidqf555.minecraft.entity_enchantment.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ScrollModel extends Model {

    public static final ModelLayerLocation LOCATION = new ModelLayerLocation(new ResourceLocation(Main.MOD_ID, "scroll"), "main");
    private final ModelPart scroll;

    public ScrollModel(ModelPart model) {
        super(RenderType::entitySolid);
        scroll = model.getChild("scroll");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild("scroll", CubeListBuilder.create().texOffs(0, 0).addBox(-6, -5, -0.005f, 12, 10, 0.005f), PartPose.ZERO);
        return LayerDefinition.create(mesh, 12, 10);
    }

    @Override
    public void renderToBuffer(PoseStack p_225598_1_, VertexConsumer p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
        scroll.render(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
    }

}
