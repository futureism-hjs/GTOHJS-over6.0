package com.gtohjs.coremod;

import com.gtohjs.bootstrap.GTOHJSBootstrap;
import com.gtohjs.machines.ULVFragmentWorldCollectionMachine;
import com.gtohjs.methods.Fix2RegistrationMethods;
import com.gtohjs.methods.FullRegistrationMethods;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/** Independent Java checks are required because Forge catches JS failures. */
public final class InjectionVerifier {
    private static final String[][] TARGETS = {
            {"com.gtocore.common.data.GTOMachines", "gtohjs$dev9$machine"},
            {"com.gtocore.common.data.GTORecipeTypes", "gtohjs$dev9$recipeType"},
            {"com.gtocore.data.Data", "gtohjs$dev9$recipe"},
            {"com.gtocore.common.data.machines.GTAEMachines", "gtohjs$dev9$ae"},
            {"com.gtocore.common.machine.multiblock.generator.GeneratorArrayMachine", "gtohjs$fix2$generator"},
            {"com.gtocore.common.machine.multiblock.steam.BaseSteamMultiblockMachine", "gtohjs$fix2$steam"},
            {"com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine", "gtohjs$fix2$steamUi"},
            {"com.gtocore.data.recipe.generated.GTOMaterialRecipeHandler", "gtohjs$fix2$material"},
            {"com.gtocore.common.data.GTOCovers", "gtohjs$fix3$cover"},
            {"com.gtocore.common.recipe.condition.VacuumCondition", "gtohjs$fix3$vacuum"},
            {"com.gtocore.common.machine.multiblock.part.ae.MEPatternBufferProxyPartMachine", "gtohjs$fix3$proxy"},
            {"com.gtocore.common.machine.multiblock.part.ae.MEPatternPartUI", "gtohjs$fix3$layout"}
    };

    private InjectionVerifier() {}

    public static void verifyLoading() {
        var core = ModList.get().getModContainerById("gtocore").orElseThrow(
                () -> new IllegalStateException("GTOHJS requires GTOCore dev9 capabilities"));
        Object packVersion = core.getModInfo().getModProperties().get("pack_version");
        if (packVersion == null) {
            throw new IllegalStateException("GTOHJS requires GTOCore pack_version metadata");
        }
        ProofLog.record("GTOCore=" + core.getModInfo().getVersion() +
                " pack=" + packVersion + " profile=dev9 semantic anchors");
        ClassLoader loader = InjectionVerifier.class.getClassLoader();
        for (String[] target : TARGETS) {
            try {
                // Inspect markers without forcing premature GTO static initialization.
                Class<?> type = Class.forName(target[0], false, loader);
                Field marker = type.getDeclaredField(target[1]);
                if (marker.getType() != boolean.class || !Modifier.isStatic(marker.getModifiers()) ||
                        !marker.isSynthetic()) {
                    throw new IllegalStateException("Invalid GTOHJS injection marker on " + target[0]);
                }
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("GTOHJS injection missing: " + target[0], exception);
            }
        }
        GTOHJSBootstrap.requireExecuted(GTOHJSBootstrap.MACHINE);
        GTOHJSBootstrap.requireExecuted(GTOHJSBootstrap.RECIPE_TYPE);
        GTOHJSBootstrap.requireExecuted(GTOHJSBootstrap.AE);
        ULVFragmentWorldCollectionMachine.verifyLoaded();
        Fix2RegistrationMethods.verifyLoaded();
        FullRegistrationMethods.verifyLoaded();
        ProofLog.record("fix3 twelve class transformations verified: PASS");
        // Client Data.commonInit is asynchronous; pending is not a missing transform.
        if (!GTOHJSBootstrap.allExecuted()) {
            ProofLog.record("recipe bridge pending asynchronous GTO data loading");
        }
    }

    public static void verifyExecuted() {
        if (!GTOHJSBootstrap.allExecuted()) {
            throw new IllegalStateException("GTOHJS proof is not ready: wait for all four lifecycle bridges; " +
                    "recipe data loading may still be pending");
        }
        ULVFragmentWorldCollectionMachine.verifyLoaded();
        Fix2RegistrationMethods.requireRecipesFinished();
    }
}
