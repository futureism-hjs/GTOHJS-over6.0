package com.gtohjs.methods

/** Pure source generation shared by the editor and code-layer fixtures. */
object RecipeSourceGenerator {
    data class StackSpec(val id: String, val amount: Int, val snbt: String) {
        init {
            require(id.matches(Regex("[a-z0-9_.-]+:[a-z0-9/._-]+")) && amount > 0) {
                "Invalid generated stack specification: $id x$amount"
            }
        }
    }
    @JvmStatic
    fun className(recipeId: String): String {
        require(recipeId.matches(Regex("[a-z0-9/._-]+"))) { "Invalid generated recipe ID: $recipeId" }
        val name = StringBuilder("Generated")
        var upper = true
        for (character in recipeId) {
            if (character.isLetterOrDigit()) {
                name.append(if (upper) character.uppercaseChar() else character)
                upper = false
            } else upper = true
        }
        if (name.length == "Generated".length) name.append("Recipe")
        name.append("Recipe")
        for (character in recipeId) name.append(character.code.toString(16)).append('_')
        return name.toString()
    }
    /** Octal escapes avoid Java Unicode-escape preprocessing of control characters. */
    @JvmStatic
    fun escapeJava(value: String): String = buildString {
        for (character in value) {
            when (character) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                '\b' -> append("\\b")
                '\u000C' -> append("\\f")
                else -> if (character.code < 32 || character.code in 127..159)
                    append('\\').append(character.code.toString(8).padStart(3, '0'))
                    else append(character)
            }
        }
    }
    private fun call(stack: StackSpec, fluid: Boolean = false): String =
        "RecipeSourceSupport." + (if (fluid) "fluidStack" else "itemStack") +
            "(\"" + escapeJava(stack.id) + "\", " + stack.amount + ", \"" + escapeJava(stack.snbt) + "\")"

    private fun stackArray(stacks: Array<StackSpec>, fluid: Boolean = false): String =
        "new " + (if (fluid) "FluidStack" else "ItemStack") + "[]{" +
            stacks.joinToString(", ") { call(it, fluid) } + "}"

    @JvmStatic
    fun gt(typeId: String, recipeId: String, itemInputs: Array<StackSpec>, itemOutputs: Array<StackSpec>,
           fluidInputs: Array<StackSpec>, fluidOutputs: Array<StackSpec>, eut: Long, duration: Int,
           circuit: Int, temperature: Int, mana: Long): String {
        require(duration > 0) { "Generated duration must be positive" }
        val name = className(recipeId)
        val expectedCircuit = maxOf(0, circuit)
        val expectedTemperature = maxOf(0, temperature)
        return buildString {
            append("""
package com.gtohjs.recipes;

import com.gtohjs.methods.GTRecipeSource;
import com.gtohjs.methods.RecipeSourceSupport;
import com.gtolib.api.recipe.RecipeBuilder;
import com.gtolib.api.recipe.RecipeType;
import com.gregtechceu.gtceu.api.recipe.GTRecipeDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/** Generated GT data; save() executes only in the trusted original GTO lifecycle. */
public final class $name implements GTRecipeSource {
    private static final ResourceLocation RAW_ID = ResourceLocation.fromNamespaceAndPath("gtohjs", "${escapeJava(recipeId)}");
    private RecipeBuilder configuredBuilder;
    private GTRecipeDefinition accepted;
    private int normalizedDuration;

    public $name() {}
    @Override public ResourceLocation rawId() { return RAW_ID; }
    @Override public RecipeType recipeType() { return RecipeSourceSupport.recipeType("${escapeJava(typeId)}"); }
    @Override public void configure(RecipeBuilder builder) {
""".trimStart())
            for ((method, stacks) in listOf("inputItems" to itemInputs, "outputItems" to itemOutputs)) {
                for (stack in stacks) append("        builder.").append(method).append('(').append(call(stack)).append(");\n")
            }
            for ((method, stacks) in listOf("inputFluids" to fluidInputs, "outputFluids" to fluidOutputs)) {
                for (stack in stacks) append("        builder.").append(method).append('(').append(call(stack, true)).append(");\n")
            }
            if (circuit > 0) append("        builder.circuitMeta(").append(circuit).append(");\n")
            if (temperature > 0) append("        builder.blastFurnaceTemp(").append(temperature).append(");\n")
            if (mana != 0L) append("        builder.MANAt(").append(mana).append("L);\n")
            append("""
        builder.EUt(${eut}L).duration($duration);
        configuredBuilder = builder;
        var raw = builder.buildRawRecipe();
        if (raw.eut != ${eut}L || raw.duration != $duration)
            throw new IllegalStateException("Generated builder power/duration mismatch: " + RAW_ID);
    }
    @Override public void accept(GTRecipeDefinition definition, int index) {
        if (index != 0 || configuredBuilder == null) throw new IllegalStateException("Generated save order mismatch");
        normalizedDuration = configuredBuilder.buildRawRecipe().duration;
        configuredBuilder = null;
        accepted = definition;
        validateFinalized();
    }
    @Override public void validateFinalized() {
        if (accepted == null) throw new IllegalStateException("Generated recipe was not saved: " + RAW_ID);
        RecipeSourceSupport.validateGeneratedGT(accepted, normalizedDuration,
            ${stackArray(itemInputs)}, ${stackArray(itemOutputs)},
            ${stackArray(fluidInputs, true)}, ${stackArray(fluidOutputs, true)},
            ${eut}L, $expectedCircuit, $expectedTemperature, ${mana}L);
    }
}
""")
        }
    }

    @JvmStatic
    fun crafting(recipeId: String, output: StackSpec, grid: Array<StackSpec?>): String {
        require(grid.size == 9 && grid.any { it != null }) { "Crafting requires a nonempty nine-slot grid" }
        val symbols = linkedMapOf<StackSpec, Char>()
        val rows = Array(3) { StringBuilder(3) }
        for (index in grid.indices) {
            val stack = grid[index]?.copy(amount = 1)
            rows[index / 3].append(if (stack == null) ' ' else
                symbols.getOrPut(stack) { ('A'.code + symbols.size).toChar() })
        }
        val name = className(recipeId)
        val rowArguments = rows.joinToString(", ") { "\"" + it + "\"" }
        val keys = symbols.entries.joinToString(",\n                ") { (stack, symbol) ->
            "'$symbol', " + call(stack)
        }
        return """
package com.gtohjs.recipes;

import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.gtohjs.methods.CraftingRecipeSource;
import com.gtohjs.methods.RecipeSourceSupport;
import java.util.Collection;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Generated shaped data; compiled providers are discovered after rebuilding. */
public final class $name implements CraftingRecipeSource {
    private static final ResourceLocation RAW_ID = ResourceLocation.fromNamespaceAndPath("gtohjs", "${escapeJava(recipeId)}");
    public $name() {}
    @Override public Collection<ResourceLocation> rawIds() { return List.of(RAW_ID); }
    @Override public void register() {
        ItemStack output = ${call(output)};
        RecipeSourceSupport.rememberCrafting(RAW_ID, output, new String[]{$rowArguments},
                $keys);
        VanillaRecipeHelper.addShapedRecipe(RAW_ID, output,
                $rowArguments,
                $keys);
    }
}
""".trimStart()
    }
}
