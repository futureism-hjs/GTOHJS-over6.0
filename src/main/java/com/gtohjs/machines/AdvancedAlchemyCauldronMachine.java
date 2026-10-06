package com.gtohjs.machines;
import com.gtohjs.methods.HyperdimensionalRecipeSupport;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentList;
import com.gregtechceu.gtceu.api.recipe.handler.RecipeHandlerUnit;
import com.gtocore.common.machine.mana.multiblock.ElectricManaMultiblockMachine;
import org.jetbrains.annotations.NotNull;

/** Applies the advanced cauldron's lossless-input and guaranteed-output rules. */
public final class AdvancedAlchemyCauldronMachine extends ElectricManaMultiblockMachine {
    public AdvancedAlchemyCauldronMachine(MetaMachineBlockEntity holder) {
        super(holder);
    }

    /** The stock mana garden produces mana; this machine consumes it. */
    @Override
    public boolean isGeneratorMana() {
        return false;
    }

    @Override
    protected GTRecipe getRealRecipe(@NotNull RecipeHandlerUnit unit, GTRecipe recipe) {
        recipe.itemInputs = makeChanceInputsNonConsumable(recipe.itemInputs);
        recipe.fluidInputs = makeChanceInputsNonConsumable(recipe.fluidInputs);
        recipe.itemOutputs = guaranteeChanceOutputs(recipe.itemOutputs);
        recipe.fluidOutputs = guaranteeChanceOutputs(recipe.fluidOutputs);
        return super.getRealRecipe(unit, recipe);
    }

    private static ContentList makeChanceInputsNonConsumable(ContentList contents) {
        return normalizeChances(contents, true);
    }

    private static ContentList guaranteeChanceOutputs(ContentList contents) {
        return normalizeChances(contents, false);
    }

    private static ContentList normalizeChances(ContentList contents, boolean input) {
        ContentList.Builder changed = null;
        for (int index = 0; index < contents.size(); index++) {
            int chance = contents.chance(index);
            boolean replace = input ? chance > 0 && chance < ContentList.MAX_CHANCE
                    : chance < ContentList.MAX_CHANCE;
            if (replace && changed == null) {
                changed = new ContentList.Builder(contents.size());
                for (int prior = 0; prior < index; prior++) {
                    changed.add(contents.ingredient(prior), contents.amount(prior), contents.chance(prior),
                            contents.boost(prior), contents.rollUnit(prior));
                }
            }
            if (changed != null) {
                changed.add(contents.ingredient(index), contents.amount(index),
                        replace ? (input ? 0 : ContentList.MAX_CHANCE) : chance,
                        contents.boost(index), contents.rollUnit(index));
            }
        }
        return changed == null ? contents : changed.build();
    }
}
