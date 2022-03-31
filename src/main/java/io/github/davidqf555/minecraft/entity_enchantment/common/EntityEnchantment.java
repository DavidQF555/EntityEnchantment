package io.github.davidqf555.minecraft.entity_enchantment.common;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("entity_enchantment")
public class EntityEnchantment {

    public static final String MOD_ID = "entity_enchantment";

    public EntityEnchantment() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
