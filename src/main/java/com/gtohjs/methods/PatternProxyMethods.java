package com.gtohjs.methods;

import com.gtohjs.machines.MESuperPatternBufferRegistration;
import com.gtohjs.machines.MEPatternBufferOutputAccess;
import com.gtocore.common.machine.multiblock.part.ae.MEPatternBufferPartMachine;
import com.gtocore.common.machine.multiblock.part.ae.MEPatternBufferProxyPartMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.handler.IO;
import com.gregtechceu.gtceu.api.recipe.handler.IRecipeHandler;
import com.gregtechceu.gtceu.api.recipe.handler.PlanScratch;
import com.gregtechceu.gtceu.api.recipe.handler.RecipeHandlerUnit;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;

import java.util.ArrayList;
import java.util.List;

/** Adds an output view to the GTO proxy only for GTOHJS's super proxy definition. */
public final class PatternProxyMethods {
    private PatternProxyMethods() {}
    public static List<RecipeHandlerUnit> append(List<RecipeHandlerUnit> original, MEPatternBufferProxyPartMachine self) {
        if (!MESuperPatternBufferRegistration.PROXY_ID.equals(self.getDefinition().getId())) return original;
        var handlers = new ArrayList<>(original);
        handlers.add(RecipeHandlerUnit.of(IO.OUT, (IMultiPart)self, List.of(new BoundOutputHandler(self))));
        return List.copyOf(handlers);
    }

    /** Resolves the binding for every operation so an unbound proxy cannot short-circuit output checks. */
    private static final class BoundOutputHandler implements IRecipeHandler {
        private final MEPatternBufferProxyPartMachine proxy;

        private BoundOutputHandler(MEPatternBufferProxyPartMachine proxy) {
            this.proxy = proxy;
        }

        private IRecipeHandler target() {
            MEPatternBufferPartMachine buffer = proxy.getBuffer();
            if (buffer instanceof MEPatternBufferOutputAccess outputAccess) {
                return outputAccess.gtohjs$getOutputHandler();
            }
            return null;
        }

        @Override
        public boolean handlesItems() {
            return true;
        }

        @Override
        public boolean handlesFluids() {
            return true;
        }

        @Override
        public boolean isInfiniteCapacity(AEKeyType type) {
            IRecipeHandler target = target();
            return target != null && target.isInfiniteCapacity(type);
        }

        @Override
        public long reserveOutput(PlanScratch plan, int member, AEKeyType type, int entry, AEKey key, long amount) {
            IRecipeHandler target = target();
            return target == null ? 0 : target.reserveOutput(plan, member, type, entry, key, amount);
        }

        @Override
        public long insertOutput(AEKeyType type, AEKey key, long amount) {
            IRecipeHandler target = target();
            return target == null ? 0 : target.insertOutput(type, key, amount);
        }

        @Override
        public void onRecipeCommitted(GTRecipe recipe) {
            IRecipeHandler target = target();
            if (target != null) target.onRecipeCommitted(recipe);
        }
    }
}
