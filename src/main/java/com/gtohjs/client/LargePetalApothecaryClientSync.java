package com.gtohjs.client;
import com.google.common.collect.ImmutableSet;
import com.gtohjs.GTOHJS;
import com.gtohjs.machines.LargePetalApothecaryRecipeTypeRegistration;
import com.gtocore.common.data.GTORecipes;
import com.gtocore.integration.emi.GTEMIRecipe;
import com.gregtechceu.gtceu.integration.emi.recipe.GTRecipeEMICategory;
import dev.emi.emi.api.recipe.EmiRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
/** Reuses dev9's public display constructor and category/cache APIs. */
@Mod.EventBusSubscriber(modid=GTOHJS.MOD_ID,bus=Mod.EventBusSubscriber.Bus.FORGE,value=Dist.CLIENT)
public final class LargePetalApothecaryClientSync {
    private static final Set<EmiRecipe> LAST=Collections.newSetFromMap(new IdentityHashMap<>());
    private LargePetalApothecaryClientSync() {}
    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static synchronized void onRecipesUpdated(RecipesUpdatedEvent event) {
        var definitions=LargePetalApothecaryRecipeTypeRegistration.definition().synchronizeClientRecipes(event.getRecipeManager());
        if(GTORecipes.EMI_RECIPES==null) throw new IllegalStateException("GTO recipe display cache not initialized");
        LinkedHashSet<EmiRecipe> merged=new LinkedHashSet<>();
        for(EmiRecipe recipe:GTORecipes.EMI_RECIPES) if(!LAST.contains(recipe)) merged.add(recipe);
        LAST.clear();
        for(var definition:definitions) {
            EmiRecipe display=new GTEMIRecipe(definition,GTRecipeEMICategory.CATEGORIES.apply(definition.recipeCategory));
            merged.add(display); LAST.add(display);
        }
        GTORecipes.EMI_RECIPES=ImmutableSet.copyOf(merged);
    }
}
