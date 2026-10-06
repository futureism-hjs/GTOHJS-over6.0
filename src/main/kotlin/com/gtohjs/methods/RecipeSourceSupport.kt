package com.gtohjs.methods

import com.gregtechceu.gtceu.api.recipe.GTRecipeDefinition
import com.gregtechceu.gtceu.api.recipe.content.ContentList
import com.gregtechceu.gtceu.api.recipe.content.KeyIngredient
import com.gregtechceu.gtceu.api.registry.GTRegistries
import com.gregtechceu.gtceu.common.data.GTRecipeDataKeys
import com.gregtechceu.gtceu.common.data.GTRecipes
import com.gregtechceu.gtceu.data.recipe.builder.ShapedRecipeBuilder
import com.gtolib.api.recipe.RecipeBuilder
import com.gtolib.api.recipe.RecipeType
import com.gtolib.api.recipe.extension.MANATRecipeExtension
import net.minecraft.nbt.TagParser
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.ShapedRecipe
import net.minecraft.core.RegistryAccess
import net.minecraft.server.MinecraftServer
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.registries.ForgeRegistries

/** Shared strict registry, IO and generated-recipe checks. Never calls save(). */
object RecipeSourceSupport {
    @JvmStatic
    fun recipeType(id: String): RecipeType {
        val key = ResourceLocation.tryParse(id)
        val resolved = if (key == null) null else GTRegistries.RECIPE_TYPES.get(key)
        return resolved as? RecipeType ?: error("Recipe type is not a GTO RecipeType: $id")
    }

    @JvmStatic
    fun itemStack(id: String, amount: Int, snbt: String?): ItemStack {
        val key = ResourceLocation.tryParse(id)
        require(key != null && ForgeRegistries.ITEMS.containsKey(key) && amount > 0) {
            "Invalid generated item stack: $id x$amount"
        }
        val item = ForgeRegistries.ITEMS.getValue(key)
        require(item != null && ForgeRegistries.ITEMS.getKey(item) == key) { "Mismatched generated item: $id" }
        val stack = ItemStack(item, amount)
        require(!stack.isEmpty) { "Empty generated item: $id" }
        if (!snbt.isNullOrBlank()) {
            try { stack.tag = TagParser.parseTag(snbt) }
            catch (error: Exception) { throw IllegalArgumentException("Invalid generated item NBT for $id", error) }
        }
        return stack
    }

    @JvmStatic
    fun fluidStack(id: String, amount: Int, snbt: String?): FluidStack {
        val key = ResourceLocation.tryParse(id)
        require(key != null && ForgeRegistries.FLUIDS.containsKey(key) && amount > 0) {
            "Invalid generated fluid stack: $id x$amount"
        }
        val fluid = ForgeRegistries.FLUIDS.getValue(key)
        require(fluid != null && ForgeRegistries.FLUIDS.getKey(fluid) == key) { "Mismatched generated fluid: $id" }
        val stack = FluidStack(fluid, amount)
        require(!stack.isEmpty) { "Empty generated fluid: $id" }
        if (!snbt.isNullOrBlank()) {
            try { stack.tag = TagParser.parseTag(snbt) }
            catch (error: Exception) { throw IllegalArgumentException("Invalid generated fluid NBT for $id", error) }
        }
        return stack
    }

    /** Extracted from the already verified imported-directory checks. */
    @JvmStatic
    fun validateItems(actual: ContentList, expected: List<ItemStack>,
                      recipeId: ResourceLocation, direction: String) {
        check(actual.size() == expected.size) { "Unexpected item $direction count in $recipeId" }
        for (index in expected.indices) {
            val stack = expected[index]
            check(actual.chance(index) == ContentList.MAX_CHANCE && actual.boost(index) == 0 &&
                actual.amount(index) == stack.count.toLong() &&
                actual.ingredient(index) == KeyIngredient.of(stack)) {
                "Unexpected item $direction #$index in $recipeId"
            }
        }
    }

    @JvmStatic
    fun validateFluids(actual: ContentList, expected: List<FluidStack>,
                       recipeId: ResourceLocation, direction: String) {
        check(actual.size() == expected.size) { "Unexpected fluid $direction count in $recipeId" }
        for (index in expected.indices) {
            val stack = expected[index]
            check(actual.chance(index) == ContentList.MAX_CHANCE && actual.boost(index) == 0 &&
                actual.amount(index) == stack.amount.toLong() &&
                actual.ingredient(index) == KeyIngredient.of(stack)) {
                "Unexpected fluid $direction #$index in $recipeId"
            }
        }
    }

    @JvmStatic
    fun validateGeneratedGT(definition: GTRecipeDefinition, normalizedDuration: Int,
        itemInputs: Array<ItemStack>, itemOutputs: Array<ItemStack>,
        fluidInputs: Array<FluidStack>, fluidOutputs: Array<FluidStack>,
        eut: Long, circuit: Int, temperature: Int, mana: Long) {
        val id = definition.id
        val circuits = (0 until definition.itemInputs.size()).filter {
            definition.itemInputs.ingredient(it).kind == KeyIngredient.CIRCUIT
        }
        val regularInputs = (0 until definition.itemInputs.size()).filterNot { it in circuits }
        check(regularInputs.size == itemInputs.size) { "Generated item input count mismatch: $id" }
        for ((index, sourceIndex) in regularInputs.withIndex()) {
            val actual = definition.itemInputs
            val stack = itemInputs[index]
            check(actual.chance(sourceIndex) == ContentList.MAX_CHANCE && actual.boost(sourceIndex) == 0 &&
                actual.amount(sourceIndex) == stack.count.toLong() &&
                actual.ingredient(sourceIndex) == KeyIngredient.of(stack)) {
                "Unexpected item input #$index in $id"
            }
        }
        validateItems(definition.itemOutputs, itemOutputs.toList(), id, "output")
        validateFluids(definition.fluidInputs, fluidInputs.toList(), id, "input")
        validateFluids(definition.fluidOutputs, fluidOutputs.toList(), id, "output")
        check(circuits.size == if (circuit > 0) 1 else 0) { "Generated circuit count mismatch: $id" }
        if (circuit > 0) {
            val index = circuits.single()
            val content = definition.itemInputs
            check(content.ingredient(index).circuitConfiguration() == circuit &&
                content.amount(index) == 1L && content.chance(index) == 0 && content.boost(index) == 0) {
                "Generated circuit metadata mismatch: $id"
            }
        }
        // Native save may normalize duration; inspect that same builder after save.
        // Do not reimplement its nullable-data and signed-EU duration rule.
        check(definition.eut == eut && definition.duration == normalizedDuration && definition.duration > 0 &&
            definition.data.getInt(GTRecipeDataKeys.EBF_TEMP) == temperature &&
            definition.data.getLong(MANATRecipeExtension.INSTANCE) == mana) {
            "Generated power/duration/temperature/mana mismatch: $id"
        }
        if (mana != 0L) check(definition.tickRecipeExtensions.contains(MANATRecipeExtension.INSTANCE)) {
            "Generated mana tick extension missing: $id"
        }
    }

    private data class CraftSpec(val output: ItemStack, val grid: List<Ingredient>)
    private val crafts = mutableMapOf<ResourceLocation, CraftSpec>()

    @JvmStatic
    @Synchronized
    fun rememberCrafting(rawId: ResourceLocation, output: ItemStack,
                         rows: Array<String>, vararg keys: Any) {
        require(rows.size == 3 && rows.all { it.length == 3 } && keys.size % 2 == 0)
        val ingredients = mutableMapOf<Char, Ingredient>()
        for (index in keys.indices step 2) {
            val symbol = keys[index] as Char
            val stack = keys[index + 1] as ItemStack
            ingredients[symbol] = if (stack.hasTag())
                net.minecraftforge.common.crafting.StrictNBTIngredient.of(stack)
                else ShapedRecipeBuilder.INGREDIENT_ITEM_FUNCTION.apply(stack.item)
        }
        val grid = rows.flatMap { row -> row.map { if (it == ' ') Ingredient.EMPTY else
            ingredients[it] ?: error("Unknown generated shape symbol: $it") } }
        val finalId = ShapedRecipeBuilder(rawId).id
        check(crafts.putIfAbsent(finalId, CraftSpec(output.copy(), grid)) == null) {
            "Duplicate generated crafting expectation: $finalId"
        }
    }

    @JvmStatic
    fun validateCraftingLoaded(rawIds: Collection<ResourceLocation>) {
        for (rawId in rawIds) {
            val id = ShapedRecipeBuilder(rawId).id
            validateCraft(id, GTRecipes.RECIPE_MAP[id])
        }
    }

    @JvmStatic
    fun validateCraftingServer(rawIds: Collection<ResourceLocation>, server: MinecraftServer) {
        for (rawId in rawIds) {
            val id = ShapedRecipeBuilder(rawId).id
            validateCraft(id, server.recipeManager.byKey(id).orElse(null))
        }
    }

    private fun validateCraft(id: ResourceLocation, recipe: Recipe<*>?) {
        check(recipe is ShapedRecipe && recipe.id == id &&
            recipe.type == net.minecraft.world.item.crafting.RecipeType.CRAFTING) {
            "Missing/invalid generated shaped recipe: $id"
        }
        val expected = crafts[id] ?: error("Missing generated crafting expectation: $id")
        check(ItemStack.matches(recipe.getResultItem(RegistryAccess.EMPTY), expected.output) &&
            recipe.width == 3 && recipe.height == 3 && recipe.ingredients.size == 9) {
            "Generated crafting output/shape mismatch: $id"
        }
        for (index in 0 until 9) check(recipe.ingredients[index].toJson() == expected.grid[index].toJson()) {
            "Generated crafting ingredient #$index mismatch: $id"
        }
    }
}
