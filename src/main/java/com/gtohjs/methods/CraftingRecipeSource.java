package com.gtohjs.methods;

import java.util.Collection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

/** A workbench-recipe source executed in GTO's common recipe-loading window. */
public interface CraftingRecipeSource {
    Collection<ResourceLocation> rawIds();

    void register();

    default void validateLoaded() {
        RecipeSourceSupport.validateCraftingLoaded(rawIds());
    }

    default void validateServerRecipes(MinecraftServer server) {
        RecipeSourceSupport.validateCraftingServer(rawIds(), server);
    }
}
