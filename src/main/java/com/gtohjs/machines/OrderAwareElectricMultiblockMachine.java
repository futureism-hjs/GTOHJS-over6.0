package com.gtohjs.machines;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeDefinition;
import com.gregtechceu.gtceu.api.recipe.handler.RecipeHandlerUnit;
import com.gtohjs.methods.PatternOrderMethods;
import com.gtohjs.methods.GtlPortedMachineText;
import com.gtolib.api.machine.multiblock.CrossRecipeMultiblockMachine;
import com.gtolib.utils.MachineUtils;
import net.minecraft.network.chat.Component;
import java.util.List;

/** Keeps native GTO execution and rejects an invalid pattern order before input handling. */
public final class OrderAwareElectricMultiblockMachine extends CrossRecipeMultiblockMachine {
    private PatternOrderMethods.Failure lastOrderFailure = PatternOrderMethods.Failure.NONE;

    public OrderAwareElectricMultiblockMachine(MetaMachineBlockEntity holder) {
        super(holder, false, true, MachineUtils::getHatchParallel);
    }

    @Override
    public GTRecipe fullModifyRecipe(RecipeHandlerUnit unit, GTRecipeDefinition definition) {
        var decision = PatternOrderMethods.check(this, unit, definition);
        lastOrderFailure = decision.failure();
        return decision.accepted() ? super.fullModifyRecipe(unit, definition) : null;
    }

    @Override
    public void customText(List<Component> textList) {
        super.customText(textList);
        for (var part : getParts()) {
            if (part instanceof MESuperWildcardPatternBufferPartMachine) {
                textList.add(GtlPortedMachineText.inferredMode());
                break;
            }
        }
        if (lastOrderFailure != PatternOrderMethods.Failure.NONE) {
            textList.add(GtlPortedMachineText.orderFailure(lastOrderFailure.name()));
        }
    }
}
