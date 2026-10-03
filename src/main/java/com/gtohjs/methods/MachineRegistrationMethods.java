package com.gtohjs.methods;

import com.gtohjs.coremod.ProofLog;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gtolib.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

/** Cold registration and validation mechanisms shared by concrete machine data. */
public final class MachineRegistrationMethods {
    private MachineRegistrationMethods() {}

    public static RecipeType registerEmptyType(RecipeType type) {
        ResourceLocation id = type.registryName;
        requireVacant(id, GTRegistries.RECIPE_TYPES.get(id));
        GTRegistries.RECIPE_TYPES.register(id, type);
        if (GTRegistries.RECIPE_TYPES.get(id) != type) {
            throw new IllegalStateException("GTOHJS machine recipe-type registry mismatch: " + id);
        }
        ProofLog.record("machine interface type registered: " + id + "; no recipes added");
        return type;
    }

    public static MachineDefinition register(ResourceLocation id, Supplier<MachineDefinition> nativeBuilder) {
        requireVacant(id, GTRegistries.MACHINES.get(id));
        MachineDefinition definition = nativeBuilder.get();
        if (definition == null || !id.equals(definition.getId()) ||
                GTRegistries.MACHINES.get(id) != definition) {
            throw new IllegalStateException("GTOHJS machine registration identity mismatch: " + id);
        }
        ProofLog.record("machine registered: " + id + "; native registry identity=PASS");
        return definition;
    }

    public static void verifyLoaded(MachineDefinition definition, ResourceLocation id, RecipeType type, int tier) {
        if (definition == null || GTRegistries.MACHINES.get(id) != definition || definition.getTier() != tier ||
                definition.getRecipeTypes().length != 1 || definition.getRecipeTypes()[0] != type ||
                GTRegistries.RECIPE_TYPES.get(type.registryName) != type) {
            throw new IllegalStateException("GTOHJS machine definition/type/tier mismatch: " + id);
        }
        if (!ForgeRegistries.BLOCKS.containsKey(id) || !ForgeRegistries.ITEMS.containsKey(id) ||
                definition.asStack().getItem() != ForgeRegistries.ITEMS.getValue(id) ||
                definition.getBlockEntityType() == null) {
            throw new IllegalStateException("GTOHJS machine Forge entries missing: " + id);
        }
        ProofLog.record("machine load validation: " + id + "; block/item/block-entity/type/tier=PASS");
    }

    private static void requireVacant(ResourceLocation id, Object existing) {
        if (existing != null) throw new IllegalStateException("GTOHJS registry collision: " + id);
    }
}
