package io.github.davidqf555.minecraft.entity_enchantment.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.davidqf555.minecraft.entity_enchantment.common.enchantments.EntityEnchantment;
import io.github.davidqf555.minecraft.entity_enchantment.common.registration.EntityEnchantmentRegistry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EnchantEntityCommand {

    private static final String COULD_NOT_FIND = "command." + Main.MOD_ID + ".could_not_find", APPLY = "command." + Main.MOD_ID + ".apply";

    private EnchantEntityCommand() {
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("enchantentity")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("add")
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .then(Commands.argument("enchantment", ResourceLocationArgument.id())
                                        .suggests(new EntityEnchantmentProvider())
                                        .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                                .executes(context -> add(context.getSource(), EntityArgument.getEntities(context, "targets"), ResourceLocationArgument.getId(context, "enchantment"), IntegerArgumentType.getInteger(context, "level")))
                                        )
                                )
                        )
                )
                .then(Commands.literal("set")
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .then(Commands.argument("enchantment", ResourceLocationArgument.id())
                                        .suggests(new EntityEnchantmentProvider())
                                        .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                                .executes(context -> set(context.getSource(), EntityArgument.getEntities(context, "targets"), ResourceLocationArgument.getId(context, "enchantment"), IntegerArgumentType.getInteger(context, "level")))
                                        )
                                )
                        )
                )
        );
    }

    private static int add(CommandSource source, Collection<? extends Entity> targets, ResourceLocation enchantment, int level) {
        EntityEnchantment val = EntityEnchantmentRegistry.getRegistry().getValue(enchantment);
        if (val == null) {
            source.sendFailure(new TranslationTextComponent(COULD_NOT_FIND, enchantment));
            return 0;
        }
        return add(source, targets, val, level);
    }

    private static int set(CommandSource source, Collection<? extends Entity> targets, ResourceLocation enchantment, int level) {
        EntityEnchantment val = EntityEnchantmentRegistry.getRegistry().getValue(enchantment);
        if (val == null) {
            source.sendFailure(new TranslationTextComponent(COULD_NOT_FIND, enchantment));
            return 0;
        }
        return set(source, targets, val, level);
    }

    private static int add(CommandSource source, Collection<? extends Entity> targets, EntityEnchantment enchantment, int level) {
        int success = 0;
        for (Entity target : targets) {
            if (target instanceof LivingEntity && EntityEnchantments.addEnchantment((LivingEntity) target, enchantment, level)) {
                success++;
            }
        }
        source.sendSuccess(new TranslationTextComponent(APPLY, enchantment.getDisplayName(level), success), true);
        return success;
    }

    private static int set(CommandSource source, Collection<? extends Entity> targets, EntityEnchantment enchantment, int level) {
        int success = 0;
        for (Entity target : targets) {
            if (target instanceof LivingEntity && EntityEnchantments.setEnchantment((LivingEntity) target, enchantment, level)) {
                success++;
            }
        }
        source.sendSuccess(new TranslationTextComponent(APPLY, enchantment.getDisplayName(level), success), true);
        return success;
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static class EntityEnchantmentProvider implements SuggestionProvider<CommandSource> {

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
            for (EntityEnchantment enchantment : EntityEnchantmentRegistry.getRegistry()) {
                builder.suggest(enchantment.getRegistryName().toString());
            }
            return builder.buildFuture();
        }
    }

}
