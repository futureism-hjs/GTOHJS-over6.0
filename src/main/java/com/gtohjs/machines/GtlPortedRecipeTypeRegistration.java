package com.gtohjs.machines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.handler.IO;
import com.gregtechceu.gtceu.api.recipe.info.FluidRecipeInfo;
import com.gregtechceu.gtceu.api.recipe.info.ItemRecipeInfo;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.sound.SoundEntry;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;

import com.gtohjs.methods.ModLog;
import com.gtohjs.methods.GtlPortedMachineText;
import com.gtolib.api.recipe.RecipeType;
import com.gtolib.utils.register.RecipeTypeRegisterUtils;

import net.minecraft.resources.ResourceLocation;

import static com.lowdragmc.lowdraglib.gui.texture.ProgressTexture.FillDirection.LEFT_TO_RIGHT;

/**
 * GTL-EnhancedCore 移植所需的三个专属配方类型。
 *
 * <p>上游 GTOCore dev10 没有这三个类型（已逐个检索确认），故按本工程既有范式
 * （见 {@link OneStopRareEarthRecipeTypeRegistration}）新建：
 * <ul>
 *   <li>{@code gtceu:platinum_refining} —— 铂系精炼矩阵</li>
 *   <li>{@code gtceu:exotic_proliferation} —— 龙式场约束增殖核心</li>
 *   <li>{@code gtceu:lightning_processor} —— 超导磁约束熔合组装器</li>
 * </ul>
 *
 * <p>其余机器用到的类型上游均已存在，直接从 {@code GTRecipeTypes} / {@code GTORecipeTypes}
 * 引用，不在此重复注册。
 */
public final class GtlPortedRecipeTypeRegistration {

    public enum State { NOT_STARTED, REGISTERING, REGISTERED, FAILED }

    /** GTL source: one item input and six item outputs; no fluids. */
    public static final ResourceLocation PLATINUM_REFINING_ID = GTCEu.id("platinum_refining");
    /** GTL source: two item inputs, one item output and one fluid input. */
    public static final ResourceLocation EXOTIC_PROLIFERATION_ID = GTCEu.id("exotic_proliferation");
    /** 超导磁约束熔合组装器：3 物品入 / 1 物品出 / 0 流体入 / 0 流体出。 */
    public static final ResourceLocation LIGHTNING_PROCESSOR_ID = GTCEu.id("lightning_processor");

    private static final String PLATINUM_REFINING_PATH = "platinum_refining";
    private static final String EXOTIC_PROLIFERATION_PATH = "exotic_proliferation";
    private static final String LIGHTNING_PROCESSOR_PATH = "lightning_processor";

    private static volatile State state = State.NOT_STARTED;
    private static volatile GTRecipeType platinumRefining;
    private static volatile GTRecipeType exoticProliferation;
    private static volatile GTRecipeType lightningProcessor;

    private GtlPortedRecipeTypeRegistration() {}

    /** 必须在 GTCEu 的 GTRecipeType 注册事件里调用；注册表冻结后再注册会崩。 */
    public static synchronized void register() {
        if (state == State.REGISTERED || state == State.REGISTERING) {
            return;
        }
        state = State.REGISTERING;
        try {
            platinumRefining = registerOrReuse(PLATINUM_REFINING_ID, PLATINUM_REFINING_PATH,
                    GtlPortedMachineText.recipeNameCn(PLATINUM_REFINING_PATH), 1, 6, 0, 0, GTSoundEntries.CHEMICAL);
            exoticProliferation = registerOrReuse(EXOTIC_PROLIFERATION_ID, EXOTIC_PROLIFERATION_PATH,
                    GtlPortedMachineText.recipeNameCn(EXOTIC_PROLIFERATION_PATH), 2, 1, 1, 0, GTSoundEntries.CHEMICAL);
            lightningProcessor = registerOrReuse(LIGHTNING_PROCESSOR_ID, LIGHTNING_PROCESSOR_PATH,
                    GtlPortedMachineText.recipeNameCn(LIGHTNING_PROCESSOR_PATH), 3, 1, 0, 0, GTSoundEntries.ARC);

            validate(platinumRefining, PLATINUM_REFINING_ID, 1, 6, 0, 0);
            validate(exoticProliferation, EXOTIC_PROLIFERATION_ID, 2, 1, 1, 0);
            validate(lightningProcessor, LIGHTNING_PROCESSOR_ID, 3, 1, 0, 0);

            state = State.REGISTERED;
            ModLog.info("Registered GTL ported recipe types: {}, {}, {}",
                    PLATINUM_REFINING_ID, EXOTIC_PROLIFERATION_ID, LIGHTNING_PROCESSOR_ID);
        } catch (Throwable error) {
            state = State.FAILED;
            platinumRefining = null;
            exoticProliferation = null;
            lightningProcessor = null;
            ModLog.error("GTL ported recipe type registration failed", error);
            if (error instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IllegalStateException("GTL ported recipe type registration failed", error);
        }
    }

    private static GTRecipeType registerOrReuse(ResourceLocation id, String path, String cnName,
                                                int itemIn, int itemOut, int fluidIn, int fluidOut,
                                                SoundEntry sound) {
        GTRecipeType existing = GTRegistries.RECIPE_TYPES.get(id);
        if (existing != null) {
            if (!(existing instanceof RecipeType recipeType)) {
                throw new IllegalStateException("Existing recipe type is not a GTO RecipeType: " + existing);
            }
            return recipeType;
        }
        return RecipeTypeRegisterUtils.register(path, cnName, GTRecipeTypes.MULTIBLOCK)
                .setEUIO(IO.IN)
                .setMaxIOSize(itemIn, itemOut, fluidIn, fluidOut)
                .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, LEFT_TO_RIGHT)
                .setSound(sound);
    }

    private static void validate(GTRecipeType candidate, ResourceLocation expected,
                                 int itemIn, int itemOut, int fluidIn, int fluidOut) {
        if (candidate == null || !expected.equals(candidate.registryName)) {
            throw new IllegalStateException("Unexpected recipe type: " + candidate + ", expected " + expected);
        }
        if (!GTRecipeTypes.MULTIBLOCK.equals(candidate.group)) {
            throw new IllegalStateException("Expected multiblock recipe group for " + expected
                    + ", got " + candidate.group);
        }
        if (candidate.getMaxInputs(ItemRecipeInfo.INSTANCE) != itemIn
                || candidate.getMaxOutputs(ItemRecipeInfo.INSTANCE) != itemOut
                || candidate.getMaxInputs(FluidRecipeInfo.INSTANCE) != fluidIn
                || candidate.getMaxOutputs(FluidRecipeInfo.INSTANCE) != fluidOut) {
            throw new IllegalStateException("Unexpected IO limits for " + expected);
        }
        if (candidate.getRecipeUI() == null) {
            throw new IllegalStateException("Recipe UI was not created for " + expected);
        }
    }

    public static synchronized void validateLoaded() {
        if (state != State.REGISTERED) {
            throw new IllegalStateException("GTL ported recipe types were not registered; state=" + state);
        }
        validate(platinumRefining, PLATINUM_REFINING_ID, 1, 6, 0, 0);
        validate(exoticProliferation, EXOTIC_PROLIFERATION_ID, 2, 1, 1, 0);
        validate(lightningProcessor, LIGHTNING_PROCESSOR_ID, 3, 1, 0, 0);
    }

    public static GTRecipeType platinumRefining() { return require(platinumRefining, PLATINUM_REFINING_ID); }

    public static GTRecipeType exoticProliferation() { return require(exoticProliferation, EXOTIC_PROLIFERATION_ID); }

    public static GTRecipeType lightningProcessor() { return require(lightningProcessor, LIGHTNING_PROCESSOR_ID); }

    private static GTRecipeType require(GTRecipeType value, ResourceLocation id) {
        if (value == null) {
            throw new IllegalStateException("Recipe type not registered: " + id + "; state=" + state);
        }
        return value;
    }

    public static State state() {
        return state;
    }
}
