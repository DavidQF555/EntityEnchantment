package io.github.davidqf555.minecraft.entity_enchantment.common;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ServerConfigs {

    public static final ServerConfigs INSTANCE;
    public static final ForgeConfigSpec SPEC;

    static {
        Pair<ServerConfigs, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ServerConfigs::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final ForgeConfigSpec.DoubleValue shifterHeight, shifterWidth;

    public ServerConfigs(ForgeConfigSpec.Builder builder) {
        builder.push("Server config for Entity Enchantment mod");
        shifterWidth = builder.comment("This is the width that the Enchantment Shifter checks for entities. ")
                .defineInRange("infuserWidth", 3.0, 0.0, Double.MAX_VALUE);
        shifterHeight = builder.comment("This is the height that the Enchantment Shifter checks for entities. ")
                .defineInRange("infuserHeight", 2.0, 0.0, Double.MAX_VALUE);
        builder.pop();
    }

}
