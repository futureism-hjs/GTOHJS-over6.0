package com.gtohjs.methods;

import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gtocore.common.data.GTORecipeDataKeys;

/** Dev10-native equivalent of GTL's fixed neutron-factory order. */
public final class NeutronFactoryRecipeSupport {
    public static final long PARALLEL = 2048;
    private static final long EU_PER_EVT = 2000;

    public static final RecipeModifier NEUTRON_FACTORY = (holder, unit, original) -> {
        var recipe = original.copy();
        long evt = Math.max(0L, (long) recipe.data.getInt(GTORecipeDataKeys.EVT));
        try {
            recipe.setEUt(Math.multiplyExact(evt, EU_PER_EVT));
        } catch (ArithmeticException overflow) {
            return null;
        }
        // Fixed means that a partial order does not run at a smaller parallel count.
        recipe = ParallelLogic.accurateParallel(holder, unit, recipe, PARALLEL);
        return recipe != null && recipe.parallels == PARALLEL ? recipe : null;
    };

    private NeutronFactoryRecipeSupport() {}
}
