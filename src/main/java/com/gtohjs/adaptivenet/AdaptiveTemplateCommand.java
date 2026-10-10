package com.gtohjs.adaptivenet;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/** Server-side export of the current runtime whitelist for pack maintainers. */
public final class AdaptiveTemplateCommand {
    private AdaptiveTemplateCommand() {}

    public static void register(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("gtohjs")
                .then(Commands.literal("dumptemplates").requires(source -> source.hasPermission(2))
                        .executes(context -> dump(context.getSource())));
        event.getDispatcher().register(root);
    }

    private static int dump(CommandSourceStack source) {
        StringBuilder markdown = new StringBuilder("# Adaptive network template whitelist\n\n");
        for (var family : AdaptiveTemplateRegistry.Family.values()) {
            markdown.append("## ").append(family).append("\n\n")
                    .append("| Item ID | Name | Tier | Voltage | Amperage | Ability |\n")
                    .append("| --- | --- | ---: | ---: | ---: | --- |\n");
            AdaptiveTemplateRegistry.entries(family).entrySet().stream()
                    .sorted(java.util.Comparator.comparing(entry -> ForgeRegistries.ITEMS.getKey(entry.getKey()).toString()))
                    .forEach(entry -> {
                        ResourceLocation id = ForgeRegistries.ITEMS.getKey(entry.getKey());
                        var spec = entry.getValue();
                        markdown.append('|').append(id).append('|').append(entry.getKey().getDescription().getString())
                                .append('|').append(spec.tier()).append('|').append(spec.voltage())
                                .append('|').append(spec.amperage()).append('|').append(family.ability.getName())
                                .append("|\n");
                    });
            markdown.append('\n');
        }
        try {
            var path = source.getServer().getWorldPath(LevelResource.ROOT).resolve("ADAPTIVE_NET_TEMPLATE_WHITELIST.md");
            Files.writeString(path, markdown, StandardCharsets.UTF_8);
            source.sendSuccess(() -> Component.literal("Template whitelist exported: " + path), true);
            return 1;
        } catch (IOException error) {
            source.sendFailure(Component.literal("Template whitelist export failed: " + error.getMessage()));
            return 0;
        }
    }
}
