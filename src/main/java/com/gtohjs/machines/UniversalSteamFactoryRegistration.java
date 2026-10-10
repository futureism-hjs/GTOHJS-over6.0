package com.gtohjs.machines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.machine.multiblockpro.Structure;
import com.gregtechceu.gtceu.api.machine.multiblockpro.Piece;
import com.gregtechceu.gtceu.api.machine.multiblockpro.Symbols;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gtocore.api.machine.part.GTOPartAbility;
import com.gtocore.common.data.GTORecipeTypes;
import com.gtocore.common.data.GTOMachines;
import com.gtocore.common.machine.multiblock.steam.LargeSteamMultiblockMachine;
import com.gtocore.utils.register.MachineRegisterUtils;
import com.gtolib.GTOCore;
import com.gtohjs.methods.HyperdimensionalPatternResources;
import com.gtohjs.methods.ModLog;
import com.gtolib.api.machine.MultiblockDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

import static com.gregtechceu.gtceu.api.machine.multiblock.PartAbility.STEAM;
import static com.gregtechceu.gtceu.api.machine.multiblock.PartAbility.STEAM_EXPORT_ITEMS;
import static com.gregtechceu.gtceu.api.machine.multiblock.PartAbility.STEAM_IMPORT_ITEMS;

/** Registers the user-supplied universal steam factory structure draft. */
public final class UniversalSteamFactoryRegistration {
    private static final String PATTERN_NAME = "universal_steam_factory";
    private static final int EXPECTED_CASING_POSITIONS = 80;
    private static final int EXPECTED_FRAME_POSITIONS = 2;
    private static final int EXPECTED_PIPE_POSITIONS = 4;
    private static final int EXPECTED_GEARBOX_POSITIONS = 1;
    private static final int EXPECTED_INTEGRAL_FRAMEWORK_POSITIONS = 1;
    private static final int EXPECTED_CONTROLLER_POSITIONS = 1;
    private static final int MAX_RECIPE_EUT = (int) GTValues.V[GTValues.MV];
    private static final int LOCKED_RECIPE_DURATION = 1;
    private static final int EXPECTED_RECIPE_TYPE_COUNT = 17;

    public enum State {
        NOT_STARTED,
        REGISTERING,
        REGISTERED,
        FAILED
    }

    public static final ResourceLocation MACHINE_ID =
            ResourceLocation.fromNamespaceAndPath("gtocore", "universal_steam_factory");
    private static final GTRecipeType[] EXPECTED_RECIPE_TYPES = {
            GTORecipeTypes.BENDER_RECIPES,
            GTORecipeTypes.ROLLING_RECIPES,
            GTORecipeTypes.WIREMILL_RECIPES,
            GTORecipeTypes.LOOM_RECIPES,
            GTORecipeTypes.FLUID_SOLIDFICATION_RECIPES,
            GTORecipeTypes.LATHE_RECIPES,
            GTORecipeTypes.EXTRACTOR_RECIPES,
            GTORecipeTypes.PACKER_RECIPES,
            GTORecipeTypes.UNPACKER_RECIPES,
            GTORecipeTypes.EXTRUDER_RECIPES,
            GTORecipeTypes.FORMING_PRESS_RECIPES,
            GTORecipeTypes.CLUSTER_RECIPES,
            GTORecipeTypes.FORGE_HAMMER_RECIPES,
            GTORecipeTypes.CHEMICAL_BATH_RECIPES,
            GTORecipeTypes.CIRCUIT_ASSEMBLER_RECIPES,
            GTORecipeTypes.MIXER_RECIPES,
            GTORecipeTypes.CENTRIFUGE_RECIPES
    };
    private static volatile State state = State.NOT_STARTED;
    private static volatile MultiblockMachineDefinition definition;

    private UniversalSteamFactoryRegistration() {
    }

    /** Called in GTO's machine registration window. */
    public static synchronized void register() {
        if (state == State.REGISTERED || state == State.REGISTERING) {
            ModLog.info("Skipping duplicate universal steam factory registration; state={}", state);
            return;
        }

        state = State.REGISTERING;
        ModLog.info("Registering {} on thread {}", MACHINE_ID, Thread.currentThread().getName());
        try {
            MachineDefinition existing = GTRegistries.MACHINES.get(MACHINE_ID);
            if (existing != null) {
                if (!(existing instanceof MultiblockMachineDefinition multiblock)) {
                    throw new IllegalStateException("Existing machine is not a multiblock: " + existing);
                }
                definition = multiblock;
                validate(multiblock, false);
                state = State.REGISTERED;
                ModLog.info("Universal steam factory already exists; using {}", multiblock);
                return;
            }

            definition = MachineRegisterUtils.multiblock(
                            "universal_steam_factory",
                    "通用蒸汽厂",
                            holder -> new LargeSteamMultiblockMachine(holder, MAX_RECIPE_EUT))
                    .langValue("Universal Steam Factory")
                    .nonYAxisRotation()
                    .largeSteamRuleTooltips()
                    .recipeTypes(EXPECTED_RECIPE_TYPES)
                    .multipleRecipesTooltips()
                    .steamOverclock(GTValues.MV)
                    .block(GTBlocks.CASING_BRONZE_BRICKS)
                    
                    .structure(machine -> Structure.root(HyperdimensionalPatternResources.piece(PATTERN_NAME))
                            .symbols(Symbols.create()
                            .where('A', Predicates.blocks(GTBlocks.CASING_BRONZE_BRICKS.get())
                                    .or(Predicates.abilities(STEAM)
                                            .setExactLimit(1)
                                            .setPreviewCount(1))
                                    .or(Predicates.abilities(STEAM_IMPORT_ITEMS)
                                            .setMaxGlobalLimited(1)
                                            .setPreviewCount(1))
                                    .or(Predicates.abilities(STEAM_EXPORT_ITEMS)
                                            .setMaxGlobalLimited(1)
                                            .setPreviewCount(1))
                                    .or(Predicates.abilities(GTOPartAbility.STEAM_IMPORT_FLUIDS)
                                            .setMaxGlobalLimited(1)
                                            .setPreviewCount(1))
                                    .or(Predicates.abilities(GTOPartAbility.STEAM_EXPORT_FLUIDS)
                                            .setMaxGlobalLimited(1)
                                            .setPreviewCount(1))
                                    .or(Predicates.blocks(GTOMachines.STEAM_VENT_HATCH.get())
                                            .setExactLimit(1)
                                            .setPreviewCount(1))
                                    // Base item I/O abilities expand to every registered ordinary bus tier.
                                    .or(Predicates.abilities(GTOPartAbility.IMPORT_ITEMS)
                                            .setMaxGlobalLimited(1)
                                            .setPreviewCount(1))
                                    .or(Predicates.abilities(GTOPartAbility.EXPORT_ITEMS)
                                            .setMaxGlobalLimited(4))
                                    .or(Predicates.abilities(GTOPartAbility.IMPORT_FLUIDS)
                                            .setMaxGlobalLimited(1))
                                    .or(Predicates.abilities(GTOPartAbility.EXPORT_FLUIDS)
                                            .setMaxGlobalLimited(4)))
                            .where('S', Predicates.controller(machine))
                            .where('F', Predicates.blocks(HyperdimensionalPatternResources.block(
                                    "gtceu:bronze_frame")))
                            .where('P', Predicates.blocks(GTBlocks.CASING_BRONZE_PIPE.get()))
                            .where('G', Predicates.blocks(GTBlocks.CASING_BRONZE_GEARBOX.get()))
                            .where('I', Predicates.blocks(HyperdimensionalPatternResources.block(
                                    "gtohjs:integral_bronze_framework")))
                            .where(' ', Predicates.any())
                            )
                            .build())
                    .workableCasingRenderer(
                            GTCEu.id("block/casings/solid/machine_casing_bronze_plated_bricks"),
                            GTCEu.id("block/multiblock/steam_oven"))
                    .renderMultiblockXEIPreview(true)
                    .register();

            MachineDefinition registered = GTRegistries.MACHINES.get(MACHINE_ID);
            if (registered != definition) {
                throw new IllegalStateException("GT registry entry does not match the registered definition");
            }
            validate(definition, false);
            state = State.REGISTERED;
            ModLog.info("Registered {} as {}; recipeTypes={}, patternResource={}, dimensions=5x5x5, " +
                            "largeSteam=true, maxRecipeTier=MV, maxRecipeEUt={}, recipeDuration={}t, " +
                            "renderWorldPreview={}, renderXEIPreview={}, renderer={}",
                    MACHINE_ID, definition, Arrays.toString(EXPECTED_RECIPE_TYPES), PATTERN_NAME,
                    MAX_RECIPE_EUT, LOCKED_RECIPE_DURATION,
                    definition.hasStructure(), definition.isRenderXEIPreview(),
                    GTCEu.id("block/multiblock/steam_oven"));
        } catch (Throwable error) {
            state = State.FAILED;
            definition = null;
            ModLog.error("Universal steam factory registration failed", error);
        }
    }

    private static void validate(MultiblockMachineDefinition candidate, boolean buildPattern) {
        validatePatternSymbols();
        if (EXPECTED_RECIPE_TYPES.length != EXPECTED_RECIPE_TYPE_COUNT) {
            throw new IllegalStateException("Unexpected universal steam factory mode count: " +
                    EXPECTED_RECIPE_TYPES.length + "; expected " + EXPECTED_RECIPE_TYPE_COUNT);
        }
        if (candidate.getRecipeTypes() == null ||
                !Arrays.equals(candidate.getRecipeTypes(), EXPECTED_RECIPE_TYPES)) {
            throw new IllegalStateException("Unexpected universal steam factory recipe types: " +
                    Arrays.toString(candidate.getRecipeTypes()));
        }
        if (!candidate.hasStructure()) {
            throw new IllegalStateException("Universal steam factory pattern supplier was not created");
        }
        if (buildPattern && candidate.getStructure() == null) {
            throw new IllegalStateException("Universal steam factory pattern could not be built");
        }
        if (candidate.getRenderer() == null) {
            throw new IllegalStateException("Universal steam factory renderer was not created");
        }
        if (!candidate.hasStructure() || !candidate.isRenderXEIPreview()) {
            throw new IllegalStateException("Universal steam factory previews are disabled");
        }
    }

    private static void validatePatternSymbols() {
        assertSymbolCount('A', EXPECTED_CASING_POSITIONS);
        assertSymbolCount('F', EXPECTED_FRAME_POSITIONS);
        assertSymbolCount('P', EXPECTED_PIPE_POSITIONS);
        assertSymbolCount('G', EXPECTED_GEARBOX_POSITIONS);
        assertSymbolCount('I', EXPECTED_INTEGRAL_FRAMEWORK_POSITIONS);
        assertSymbolCount('S', EXPECTED_CONTROLLER_POSITIONS);
    }

    private static void assertSymbolCount(char symbol, int expected) {
        int actual = HyperdimensionalPatternResources.countSymbol(PATTERN_NAME, symbol);
        if (actual != expected) {
            throw new IllegalStateException("Unexpected '" + symbol + "' count in " + PATTERN_NAME +
                    ": " + actual + "; expected " + expected);
        }
    }

    public static synchronized void validateLoaded() {
        if (state != State.REGISTERED || definition == null) {
            throw new IllegalStateException("Universal steam factory was not registered; state=" + state);
        }
        try {
            validate(definition, true);
            int patternFactories = definition instanceof MultiblockDefinition gtoDefinition &&
                    gtoDefinition.hasStructure() ? 1 : 0;
            ModLog.info("Validated loaded {}; patternBuilt=true, recipeTypes={}, patternResource={}, " +
                            "dimensions=5x5x5, maxRecipeTier=MV, maxRecipeEUt={}, recipeDuration={}t, " +
                            "renderWorldPreview={}, renderXEIPreview={}, patternFactories={}",
                    MACHINE_ID, Arrays.toString(EXPECTED_RECIPE_TYPES), PATTERN_NAME,
                    MAX_RECIPE_EUT, LOCKED_RECIPE_DURATION,
                    definition.hasStructure(), definition.isRenderXEIPreview(), patternFactories);
        } catch (Throwable error) {
            state = State.FAILED;
            ModLog.error("Loaded universal steam factory validation failed", error);
            if (error instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IllegalStateException("Loaded universal steam factory validation failed", error);
        }
    }

    public static State state() {
        return state;
    }

    public static MultiblockMachineDefinition definition() {
        return definition;
    }
}
