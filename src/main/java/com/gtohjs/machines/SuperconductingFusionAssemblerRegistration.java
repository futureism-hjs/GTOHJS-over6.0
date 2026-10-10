package com.gtohjs.machines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblockpro.Structure;
import com.gregtechceu.gtceu.api.machine.multiblockpro.Symbols;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;

import com.gtocore.common.data.GTORecipeTypes;
import com.gtocore.utils.register.MachineRegisterUtils;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import com.gtohjs.methods.HyperdimensionalPatternResources;
import com.gtohjs.methods.GtlPortedMachineText;
import com.gtohjs.methods.PatternBufferPlacementMethods;
import com.gtohjs.methods.ModLog;
import com.gtolib.api.machine.multiblock.CrossRecipeMultiblockMachine;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import java.util.Arrays;

/**
 * 超导磁约束熔合组装器（Superconducting Fusion Assembler）。
 *
 * <p>几何来自 GTL-EnhancedCore 2.8.3 的 {@code superconducting_fusion_assembler.pattern.gz}：
 * 21 个切面 x 14 行 x 21 列。按字符逐格搬运，未旋转、未重排——
 * GTL 的 {@code FactoryBlockPattern.start()} 默认方向与本工程 {@code piece()} 的
 * {@code LEFT, UP, FRONT} 一致。
 *
 * <p>GTO 的 {@link CrossRecipeMultiblockMachine} 从并行仓和线程仓执行订单。
 */
public final class SuperconductingFusionAssemblerRegistration {

    private static final String PATTERN_NAME = "superconducting_fusion_assembler";

    private static final int EXPECTED_WIDTH = 21;
    private static final int EXPECTED_HEIGHT = 14;
    private static final int EXPECTED_DEPTH = 21;

    /** 各符号的确切数量：几何被改动时立即失败，而不是静默变形。 */
    private static final int EXPECTED_C_POSITIONS = 1;
    private static final int EXPECTED_X_POSITIONS = 8;
    private static final int EXPECTED_Q_POSITIONS = 26;
    private static final int EXPECTED_A_POSITIONS = 361;
    private static final int EXPECTED_B_POSITIONS = 28;
    private static final int EXPECTED_D_POSITIONS = 0;
    private static final int EXPECTED_G_POSITIONS = 575;
    private static final int EXPECTED_I_POSITIONS = 1137;
    private static final int EXPECTED_J_POSITIONS = 102;
    private static final int EXPECTED_SPACE_POSITIONS = 3936;

    private static final String CASING_ID = "gtceu:robust_machine_casing";

    /** 配方类型：与 GTL 原机器一致；上游已有的直接用原生类型，缺失的用本工程新建类型。 */
    private static final GTRecipeType[] EXPECTED_RECIPE_TYPES = {
            GTRecipeTypes.ARC_FURNACE_RECIPES,
            GtlPortedRecipeTypeRegistration.lightningProcessor(),
            GTRecipeTypes.ASSEMBLER_RECIPES,
            GTORecipeTypes.PRECISION_ASSEMBLER_RECIPES,
            GTRecipeTypes.CIRCUIT_ASSEMBLER_RECIPES,
    };

    public enum State { NOT_STARTED, REGISTERING, REGISTERED, FAILED }

    public static final ResourceLocation MACHINE_ID = ResourceLocation.fromNamespaceAndPath("gtocore", PATTERN_NAME);

    private static volatile State state = State.NOT_STARTED;
    private static volatile MultiblockMachineDefinition definition;

    private SuperconductingFusionAssemblerRegistration() {}

    public static synchronized void register() {
        if (state == State.REGISTERED || state == State.REGISTERING) {
            return;
        }
        state = State.REGISTERING;
        try {
            MachineDefinition existing = GTRegistries.MACHINES.get(MACHINE_ID);
            if (existing != null) {
                if (!(existing instanceof MultiblockMachineDefinition multiblock)) {
                    throw new IllegalStateException("Existing machine is not a multiblock: " + MACHINE_ID);
                }
                definition = multiblock;
                validate(multiblock, false);
                state = State.REGISTERED;
                return;
            }

            definition = MachineRegisterUtils.multiblock(
                            PATTERN_NAME, GtlPortedMachineText.machineNameCn(PATTERN_NAME),
                            holder -> new OrderAwareElectricMultiblockMachine(holder))
                    .langValue(GtlPortedMachineText.machineNameEn(PATTERN_NAME))
                    .nonYAxisRotation()
                    .recipeTypes(EXPECTED_RECIPE_TYPES)
                    .tooltips(GtlPortedMachineText.fusion(EXPECTED_RECIPE_TYPES))
                    .parallelizableTooltips()
                    .multipleRecipesTooltips()
                    .block(() -> HyperdimensionalPatternResources.block(CASING_ID))
                    .structure(SuperconductingFusionAssemblerRegistration::buildStructure)
                    .workableCasingRenderer(
                            GTCEu.id("block/casings/solid/machine_casing_robust_tungstensteel"),
                            GTCEu.id("block/multiblock/fusion_reactor"))
                    .renderMultiblockXEIPreview(true)
                    .register();

            if (GTRegistries.MACHINES.get(MACHINE_ID) != definition) {
                throw new IllegalStateException("GT registry entry does not match: " + MACHINE_ID);
            }
            validate(definition, false);
            state = State.REGISTERED;
            ModLog.info("Registered {}; dimensions={}x{}x{}, recipeTypes={}",
                    MACHINE_ID, EXPECTED_WIDTH, EXPECTED_HEIGHT, EXPECTED_DEPTH,
                    Arrays.toString(EXPECTED_RECIPE_TYPES));
        } catch (Throwable error) {
            state = State.FAILED;
            definition = null;
            ModLog.error("超导磁约束熔合组装器 registration failed", error);
            throw error;
        }
    }

    private static Structure buildStructure(MultiblockMachineDefinition machine) {
        var patternBuffers = PatternBufferPlacementMethods.patternBuffersOnly();
        var hatchFace = Predicates.blocks(HyperdimensionalPatternResources.block("gtceu:robust_machine_casing"))
                                .or(Predicates.abilities(PartAbility.MAINTENANCE).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(patternBuffers)
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.PARALLEL_HATCH).setMaxGlobalLimited(1).setPreviewCount(1))
                                .or(Predicates.abilities(com.gtocore.api.machine.part.GTOPartAbility.THREAD_HATCH).setMaxGlobalLimited(1).setPreviewCount(1));
        return Structure.root(HyperdimensionalPatternResources.piece(PATTERN_NAME))
                .symbols(Symbols.create()
                        .where('C', Predicates.controller(machine))
                        .where('X', hatchFace)
                        .where('Q', hatchFace)
                        .where('A', Predicates.blocks(HyperdimensionalPatternResources.block("gtceu:large_scale_assembler_casing")))
                        .where('B', Predicates.blocks(HyperdimensionalPatternResources.block("gtceu:laminated_glass")))
                        .where('D', Predicates.blocks(Blocks.IRON_BLOCK))
                        .where('G', Predicates.blocks(HyperdimensionalPatternResources.block("gtceu:stress_proof_casing")))
                        .where('I', Predicates.blocks(HyperdimensionalPatternResources.block("gtceu:robust_machine_casing")))
                        .where('J', Predicates.blocks(HyperdimensionalPatternResources.block("gtceu:molybdenum_disilicide_coil_block")))
                        .where(' ', Predicates.any())
                )
                .build();
    }

    private static void validate(MultiblockMachineDefinition candidate, boolean buildPattern) {
        if (!Arrays.equals(candidate.getRecipeTypes(), EXPECTED_RECIPE_TYPES)) {
            throw new IllegalStateException("Unexpected recipe types for " + MACHINE_ID);
        }
        if (!candidate.hasStructure() || candidate.getRenderer() == null
                || !candidate.isRenderXEIPreview()) {
            throw new IllegalStateException("Structure, renderer or XEI preview missing for " + MACHINE_ID);
        }
        if (buildPattern && candidate.getStructure() == null) {
            throw new IllegalStateException("Pattern did not build for " + MACHINE_ID);
        }
        checkSymbol('C', EXPECTED_C_POSITIONS);
        checkSymbol('X', EXPECTED_X_POSITIONS);
        checkSymbol('Q', EXPECTED_Q_POSITIONS);
        checkSymbol('A', EXPECTED_A_POSITIONS);
        checkSymbol('B', EXPECTED_B_POSITIONS);
        checkSymbol('D', EXPECTED_D_POSITIONS);
        checkSymbol('G', EXPECTED_G_POSITIONS);
        checkSymbol('I', EXPECTED_I_POSITIONS);
        checkSymbol('J', EXPECTED_J_POSITIONS);
        checkSymbol(' ', EXPECTED_SPACE_POSITIONS);
    }

    private static void checkSymbol(char symbol, int expected) {
        int actual = HyperdimensionalPatternResources.countSymbol(PATTERN_NAME, symbol);
        if (actual != expected) {
            throw new IllegalStateException(
                    "Symbol " + symbol + " count changed for " + MACHINE_ID
                            + ": actual=" + actual + ", expected=" + expected);
        }
    }

    public static void validateLoaded() {
        if (state != State.REGISTERED || definition == null) {
            throw new IllegalStateException("Machine not registered: " + MACHINE_ID);
        }
        try {
            validate(definition, true);
        } catch (Throwable error) {
            state = State.FAILED;
            throw error;
        }
    }

    public static State state() {
        return state;
    }

    public static MultiblockMachineDefinition definition() {
        return definition;
    }
}
