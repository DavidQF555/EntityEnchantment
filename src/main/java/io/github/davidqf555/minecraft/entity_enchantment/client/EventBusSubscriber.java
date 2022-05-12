package io.github.davidqf555.minecraft.entity_enchantment.client;


import io.github.davidqf555.minecraft.entity_enchantment.client.render.EnchantmentRenderer;
import io.github.davidqf555.minecraft.entity_enchantment.client.render.IllusionRenderer;
import io.github.davidqf555.minecraft.entity_enchantment.client.render.ScrollModel;
import io.github.davidqf555.minecraft.entity_enchantment.client.render.ScrollRenderer;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import io.github.davidqf555.minecraft.entity_enchantment.common.registration.TileEntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class EventBusSubscriber {

    private EventBusSubscriber() {
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ScrollModel.LOCATION, ScrollModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TileEntityRegistry.SCROLL.get(), ScrollRenderer::new);
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            LivingEntityRenderer<Player, EntityModel<Player>> renderer = event.getSkin(skin);
            if (renderer != null) {
                addLayers(renderer);
            }
        }
        Minecraft.getInstance().getEntityRenderDispatcher().renderers.values().forEach(renderer -> {
            if (renderer instanceof LivingEntityRenderer) {
                addLayers((LivingEntityRenderer<?, ?>) renderer);
            }
        });
    }

    private static <T extends LivingEntity, M extends EntityModel<T>> void addLayers(LivingEntityRenderer<T, M> renderer) {
        renderer.addLayer(new EnchantmentRenderer<>(renderer));
        renderer.addLayer(new IllusionRenderer<>(renderer));
    }
}
