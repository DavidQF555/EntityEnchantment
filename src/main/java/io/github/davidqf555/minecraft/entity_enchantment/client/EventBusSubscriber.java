package io.github.davidqf555.minecraft.entity_enchantment.client;


import io.github.davidqf555.minecraft.entity_enchantment.client.render.EnchantmentRenderer;
import io.github.davidqf555.minecraft.entity_enchantment.common.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class EventBusSubscriber {

    private EventBusSubscriber() {
    }

    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            EntityRendererManager manager = Minecraft.getInstance().getEntityRenderDispatcher();
            manager.getSkinMap().values().forEach(EventBusSubscriber::addEnchantmentLayer);
            manager.renderers.forEach((type, renderer) -> {
                if (renderer instanceof LivingRenderer) {
                    addEnchantmentLayer((LivingRenderer<LivingEntity, EntityModel<LivingEntity>>) renderer);
                }
            });
        });
    }

    private static <T extends LivingEntity, M extends EntityModel<T>> void addEnchantmentLayer(LivingRenderer<T, M> renderer) {
        renderer.addLayer(new EnchantmentRenderer<>(renderer));
    }
}
