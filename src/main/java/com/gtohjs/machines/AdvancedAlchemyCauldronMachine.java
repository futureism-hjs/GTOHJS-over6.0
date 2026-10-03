package com.gtohjs.machines;
import com.gtohjs.methods.HyperdimensionalRecipeSupport;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.content.ContentInner;
import com.gregtechceu.gtceu.api.recipe.handler.RecipeHandlerUnit;
import com.gtocore.common.machine.mana.multiblock.ElectricManaMultiblockMachine;
import java.util.List;
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

    private static <T extends ContentInner> List<Content<T>> makeChanceInputsNonConsumable(
            List<Content<T>> contents) {
        return normalizeChances(contents, true);
    }

    private static <T extends ContentInner> List<Content<T>> guaranteeChanceOutputs(
            List<Content<T>> contents) {
        return normalizeChances(contents, false);
    }

    private static <T extends ContentInner> List<Content<T>> normalizeChances(List<Content<T>> contents, boolean input) {
        java.util.ArrayList<Content<T>> changed = null;
        for (int index = 0; index < contents.size(); index++) {
            Content<T> content = contents.get(index);
            boolean replace = input ? content.chance > 0 && content.chance < Content.MAX_CHANCE
                    : content.chance < Content.MAX_CHANCE;
            if (replace && changed == null) {
                changed = new java.util.ArrayList<>(contents.size());
                for (int prior = 0; prior < index; prior++) changed.add(contents.get(prior));
            }
            if (changed != null) changed.add(replace ? withChance(content, input ? 0 : Content.MAX_CHANCE) : content);
        }
        return changed == null ? contents : changed;
    }

    private static <T extends ContentInner> Content<T> withChance(Content<T> content, int chance) {
        return new Content<>(content.inner, content.amount, chance, content.tierChanceBoost);
    }
}
