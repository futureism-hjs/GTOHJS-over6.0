package com.gtohjs.methods;

import com.gtohjs.coremod.ProofLog;
import com.gtohjs.data.GTOHJSBlocks;
import com.gtohjs.data.GTOHJSItems;
import com.gtohjs.machines.*;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.registries.ForgeRegistries;

/** Explicit lifecycle dispatch over concrete data; no generic machine descriptor. */
public final class Fix2RegistrationMethods {
    private static final String[] MACHINE_PATHS = {
        "hyperdimensional_forge","hyperdimensional_steam_furnace","hyperdimensional_smelter",
        "hyperdimensional_chemical_factory","hyperdimensional_biochemical_factory","universal_steam_factory",
        "one_stop_rare_earth_processing_plant","advanced_generator_array","advanced_alchemy_cauldron",
        "steam_array","advanced_steam_array","large_petal_apothecary","large_fragment_world_collection_machine"};
    private static boolean machinesRegistered;
    private static volatile boolean recipesFinished;
    private Fix2RegistrationMethods() {}

    public static void registerTypes() {
        for (String path : new String[]{"one_stop_rare_earth_processing","hyperdimensional_biochemical_processing","large_petal_apothecary"}) {
            ResourceLocation id = new ResourceLocation("gtceu", path);
            if (GTRegistries.RECIPE_TYPES.get(id) != null) throw new IllegalStateException("GTOHJS recipe-type collision: " + id);
        }
        OneStopRareEarthRecipeTypeRegistration.register();
        HyperdimensionalBiochemicalRecipeTypeRegistration.register();
        LargePetalApothecaryRecipeTypeRegistration.register();
    }

    public static void registerMachines() {
        if (machinesRegistered) throw new IllegalStateException("Duplicate fix2 machine lifecycle");
        for (String path : MACHINE_PATHS) {
            ResourceLocation id = new ResourceLocation("gtocore", path);
            if (GTRegistries.MACHINES.get(id) != null) throw new IllegalStateException("GTOHJS machine collision: " + id);
        }
        HyperdimensionalForgeRegistration.register();
        HyperdimensionalSteamFurnaceRegistration.register();
        HyperdimensionalSmelterRegistration.register();
        HyperdimensionalChemicalFactoryRegistration.register();
        HyperdimensionalBiochemicalFactoryRegistration.register();
        UniversalSteamFactoryRegistration.register();
        OneStopRareEarthProcessingPlantRegistration.register();
        AdvancedGeneratorArrayRegistration.register();
        AdvancedAlchemyCauldronRegistration.register();
        SteamArrayRegistration.register();
        AdvancedSteamArrayRegistration.register();
        LargePetalApothecaryRegistration.register();
        FragmentWorldCollectionMachineRegistration.register();
        machinesRegistered = true;
        ProofLog.record("fix2 13 multiblock definitions registered");
    }

    public static void verifyLoaded() {
        if (!machinesRegistered) throw new IllegalStateException("Fix2 machine registration incomplete");
        GTOHJSBlocks.validateLoaded(); GTOHJSItems.validateLoaded();
        OneStopRareEarthRecipeTypeRegistration.validateLoaded();
        HyperdimensionalBiochemicalRecipeTypeRegistration.validateLoaded();
        LargePetalApothecaryRecipeTypeRegistration.validateLoaded();
        FragmentWorldCollectionRecipeTypeRegistration.validateLoaded();
        HyperdimensionalForgeRegistration.validateLoaded();
        HyperdimensionalSteamFurnaceRegistration.validateLoaded();
        HyperdimensionalSmelterRegistration.validateLoaded();
        HyperdimensionalChemicalFactoryRegistration.validateLoaded();
        HyperdimensionalBiochemicalFactoryRegistration.validateLoaded();
        UniversalSteamFactoryRegistration.validateLoaded();
        OneStopRareEarthProcessingPlantRegistration.validateLoaded();
        AdvancedGeneratorArrayRegistration.validateLoaded();
        AdvancedAlchemyCauldronRegistration.validateLoaded();
        SteamArrayRegistration.validateLoaded(); AdvancedSteamArrayRegistration.validateLoaded();
        LargePetalApothecaryRegistration.validateLoaded(); FragmentWorldCollectionMachineRegistration.validateLoaded();
        for (String path : MACHINE_PATHS) {
            ResourceLocation id = new ResourceLocation("gtocore", path);
            MachineDefinition definition = GTRegistries.MACHINES.get(id);
            if (definition == null || !ForgeRegistries.BLOCKS.containsKey(id) || !ForgeRegistries.ITEMS.containsKey(id) ||
                    definition.asItem() != ForgeRegistries.ITEMS.getValue(id) || definition.getBlockEntityType() == null)
                throw new IllegalStateException("Fix2 machine registry binding missing: " + id);
        }
        ProofLog.record("fix2 13 multiblocks, native patterns and resource bindings: PASS");
    }
    public static void afterRecipeFinish() {
        RecipeRegistrationMethods.validateGTFinalized();
        RecipeRegistrationMethods.validateMaterialFinalized();
        RecipeRegistrationMethods.validateCraftingLoaded();
        recipesFinished = true;
        ProofLog.record("fix2 trusted recipe save and final table validation: PASS");
    }
    public static void requireRecipesFinished() {
        if (!recipesFinished) throw new IllegalStateException("Fix2 recipe loading incomplete or failed");
    }
    public static void verifyServerRecipes(MinecraftServer server) {
        requireRecipesFinished();
        RecipeRegistrationMethods.validateCraftingServerRecipes(server);
        LargePetalApothecaryRecipeTypeRegistration.validateProxyRecipes(server);
    }
}
