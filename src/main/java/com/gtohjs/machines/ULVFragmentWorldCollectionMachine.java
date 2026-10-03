package com.gtohjs.machines;

import com.gtohjs.coremod.ProofLog;
import com.gtohjs.methods.MachineRegistrationMethods;
import com.gtocore.utils.register.MachineRegisterUtils;
import com.gtolib.api.recipe.GTORecipeModifiers;
import com.gtolib.api.recipe.RecipeType;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.recipe.handler.IO;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.gregtechceu.gtceu.common.data.machines.GTMachineUtils;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** Concrete native builder data for the first machine-only adaptation node. */
public final class ULVFragmentWorldCollectionMachine {
    public static final ResourceLocation ID = new ResourceLocation("gtocore", "ulv_fragment_world_collection_machine");
    private static final ResourceLocation TYPE_ID = GTCEu.id("fragment_world_collection");
    private static RecipeType recipeType;
    private static MachineDefinition definition;

    private ULVFragmentWorldCollectionMachine() {}

    public static void registerInterfaceType() {
        if (recipeType != null) throw new IllegalStateException("Duplicate ULV collector type callback");
        // Only the machine's IO/UI schema; no recipe builder/save/data registration.
        FragmentWorldCollectionRecipeTypeRegistration.register();
        recipeType = FragmentWorldCollectionRecipeTypeRegistration.definition();
    }

    public static void register() {
        if (recipeType == null) throw new IllegalStateException("ULV collector type callback did not execute");
        if (definition != null) throw new IllegalStateException("Duplicate ULV collector machine callback");
        definition = MachineRegistrationMethods.register(ID, () -> MachineRegisterUtils.machine(
                        ID.getPath(), "碎片世界采集器",
                        holder -> new SimpleTieredMachine(holder, GTValues.ULV, GTMachineUtils.largeTankSizeFunction))
                .tier(GTValues.ULV)
                .langValue("Fragment World Collection Machine")
                .editableUI(SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(
                        new ResourceLocation("gtohjs", "fragment_world_collection"), recipeType))
                .nonYAxisRotation()
                .recipeType(recipeType)
                .recipeModifier(GTORecipeModifiers.UPGRADE_OVERCLOCK)
                .workableTieredHullRenderer(new ResourceLocation("gtohjs", "block/machines/fragment_world_collection_machine"))
                .tooltips(Component.translatable("gtohjs.machine.fragment_world_collection.tooltip"))
                .register());
        ProofLog.record("ULV collector uses native SimpleTieredMachine; native tank capacity=" +
                GTMachineUtils.largeTankSizeFunction.applyAsInt(GTValues.ULV) + "mB; recipes not adapted");
    }

    public static void verifyLoaded() {
        MachineRegistrationMethods.verifyLoaded(definition, ID, recipeType, GTValues.ULV);
    }
    public static MachineDefinition definition() { return definition; }
}
