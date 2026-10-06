package com.gtohjs.methods

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.gregtechceu.gtceu.api.recipe.GTRecipeType
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.network.chat.contents.TranslatableContents
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/** Bilingual source and tooltip layout for the seven GTL-derived dev10 machines. */
object GtlPortedMachineText {
    private data class Text(val zh: String, val en: String)
    private const val STRUCTURE_SOURCE = "结构来源：GTL-Enhancedcore"
    private val structureColors = intArrayOf(
        0xFF5555, 0xFFAA00, 0xFFFF55, 0x55FF55,
        0x55FFFF, 0x5555FF, 0xAA00AA, 0xFF55FF
    )

    private val machineNames = linkedMapOf(
        "neutron_control_factory" to Text("中子控制工厂", "Neutron Control Factory"),
        "platinum_refining_matrix" to Text("铂系精炼矩阵", "Platinum Refining Matrix"),
        "dragon_field_proliferation_core" to Text("龙式场约束增殖核心", "Dragon-Field Proliferation Core"),
        "plasma_machine_tool" to Text("磁流体约束等离子机床", "Magnetic Confinement Plasma Machine Tool"),
        "hadron_catalytic_refinery" to Text("强子流态催化精炼塔", "Hadron Flow Catalytic Refinery Tower"),
        "quantum_mass_spectrum_array" to Text("量子场质谱解离阵列", "Quantum Field Mass Spectrometry Dissociation Array"),
        "superconducting_fusion_assembler" to Text("超导磁约束熔合组装器", "Superconducting Magnetic Confinement Fusion Assembler")
    )
    private val recipeNames = linkedMapOf(
        "platinum_refining" to Text("铂系精炼", "Platinum Refining"),
        "exotic_proliferation" to Text("异种物质增殖", "Exotic Proliferation"),
        "lightning_processor" to Text("闪电处理器", "Lightning Processor")
    )

    private val translations = buildTranslations()

    @JvmStatic
    fun machineNameCn(path: String): String = requireNotNull(machineNames[path]) { "Unknown GTL machine: $path" }.zh

    @JvmStatic
    fun machineNameEn(path: String): String = requireNotNull(machineNames[path]) { "Unknown GTL machine: $path" }.en

    @JvmStatic
    fun recipeNameCn(path: String): String = requireNotNull(recipeNames[path]) { "Unknown GTL recipe type: $path" }.zh

    /** The same bilingual entries used by the item tooltip and the client preview language. */
    @JvmStatic
    fun clientTranslations(locale: String): Map<String, String> =
        translations.mapValues { (_, value) -> if (locale == "zh_cn") value.zh else value.en }

    @JvmStatic
    fun isGtlMachine(path: String): Boolean = machineNames.containsKey(path)

    /** A fixed rainbow where each neighboring character has a different color. */
    @JvmStatic
    fun structureSource(): Component = Component.empty().also { result ->
        STRUCTURE_SOURCE.forEachIndexed { index, character ->
            result.append(Component.literal(character.toString())
                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(structureColors[index % structureColors.size]))))
        }
    }

    /** Resolve this machine's hover text through the existing GTO HJS item tooltip event. */
    @JvmStatic
    fun localizeHoverText(lines: MutableList<Component>, path: String, locale: String) {
        val machine = machineNames[path] ?: return
        if (lines.isEmpty()) return
        val chinese = locale == "zh_cn"
        lines[0] = Component.translatableWithFallback(
            "block.gtocore.$path", if (chinese) machine.zh else machine.en
        ).withStyle(lines[0].style)
        for (index in 1 until lines.size) {
            val old = lines[index]
            val content = old.contents as? TranslatableContents ?: continue
            val text = translations[content.key] ?: continue
            lines[index] = Component.translatableWithFallback(
                content.key, if (chinese) text.zh else text.en, *content.args
            ).withStyle(old.style)
        }
    }

    @JvmStatic
    fun neutron(types: Array<GTRecipeType>): Array<Component> = arrayOf(
        line("gtohjs.machine.ported.neutron_control_factory.intro", ChatFormatting.AQUA),
        line("gtohjs.machine.ported.neutron_control_factory.auto_energy", ChatFormatting.GRAY),
        line("gtohjs.machine.ported.neutron_control_factory.convert", ChatFormatting.GRAY),
        line("gtohjs.machine.ported.neutron_control_factory.muffler", ChatFormatting.RED),
        line("gtohjs.machine.ported.neutron_control_factory.maintenance", ChatFormatting.GREEN),
        translated(
            "gtohjs.machine.ported.neutron_control_factory.parallel",
            Component.literal(NeutronFactoryRecipeSupport.PARALLEL.toString()).withStyle(ChatFormatting.DARK_PURPLE)
        ).withStyle(ChatFormatting.RED)
    )

    @JvmStatic
    fun platinum(types: Array<GTRecipeType>): Array<Component> = arrayOf(
        line("gtohjs.machine.ported.platinum_refining_matrix.tips", ChatFormatting.AQUA),
    )

    @JvmStatic
    fun dragon(types: Array<GTRecipeType>): Array<Component> = arrayOf(
        line("gtohjs.machine.ported.dragon_field_proliferation_core.tips", ChatFormatting.AQUA),
        line("gtohjs.machine.ported.dragon_field_proliferation_core.breath_supply", ChatFormatting.GREEN)
    )

    @JvmStatic
    fun plasma(types: Array<GTRecipeType>): Array<Component> = iv("plasma_machine_tool", types)

    @JvmStatic
    fun hadron(types: Array<GTRecipeType>): Array<Component> = iv("hadron_refinery", types)

    @JvmStatic
    fun quantum(types: Array<GTRecipeType>): Array<Component> = iv("quantum_mass_array", types)

    @JvmStatic
    fun fusion(types: Array<GTRecipeType>): Array<Component> = iv("fusion_assembler", types)

    private fun iv(originalKey: String, types: Array<GTRecipeType>): Array<Component> = arrayOf(
        line("gtohjs.machine.ported.$originalKey.intro", ChatFormatting.AQUA),
        line("gtohjs.machine.order_iv.thread_hatch", ChatFormatting.GRAY),
        line("gtohjs.machine.order_iv.pattern_order", ChatFormatting.AQUA)
    )

    private fun translated(key: String, vararg args: Any): MutableComponent =
        Component.translatableWithFallback(key, requireNotNull(translations[key]) { "Missing machine text: $key" }.zh, *args)

    private fun line(key: String, style: ChatFormatting): Component = translated(key).withStyle(style)

    @JvmStatic
    fun inferredMode(): Component = translated("gtohjs.diagnostic.order.inferred_mode")

    @JvmStatic
    fun orderFailure(failureName: String): Component =
        translated("gtohjs.diagnostic.order.${failureName.lowercase()}")

    private fun buildTranslations(): Map<String, Text> = linkedMapOf<String, Text>().apply {
        machineNames.forEach { (path, name) ->
            for (key in arrayOf("block.gtocore.$path", "item.gtocore.$path", "machine.gtocore.$path", "block.gtceu.$path")) {
                put(key, name)
            }
        }
        recipeNames.forEach { (path, name) -> put("gtceu.$path", name) }

        put("gtohjs.machine.ported.neutron_control_factory.intro", Text(
            "初步掌控了中子的运动规律，对其进行了智能化约束掌控，但还无法做到批量控制",
            "Gained preliminary mastery over neutron motion and constrains it intelligently, though batch control is still out of reach"
        ))
        put("gtohjs.machine.ported.neutron_control_factory.auto_energy", Text(
            "工作时会消耗对应电量，自动提供中子动能",
            "Consumes the matching amount of power while running and supplies neutron kinetic energy automatically"
        ))
        put("gtohjs.machine.ported.neutron_control_factory.convert", Text(
            "中子动能自动转换比例为 2EU → 1eV", "Neutron kinetic energy conversion ratio is fixed at 2EU -> 1eV"
        ))
        put("gtohjs.machine.ported.neutron_control_factory.muffler", Text("必须安装 ZPM 消声仓", "ZPM muffler hatch is required"))
        put("gtohjs.machine.ported.neutron_control_factory.maintenance", Text("支持安装维护仓（最多 1 个）", "Maintenance hatch supported (at most 1)"))
        put("gtohjs.machine.ported.neutron_control_factory.parallel", Text(
            "无法安装并行控制仓，并行固定为 %s", "Parallel Hatch not supported; parallel is fixed at %s"
        ))
        put("gtohjs.machine.ported.platinum_refining_matrix.tips", Text(
            "在封闭式多级反应腔中完成浸出、萃取、置换、电解与真空熔炼。",
            "Leaching, extraction, displacement, electrolysis and vacuum smelting are completed in a sealed multi-stage reaction chamber."
        ))
        put("gtohjs.machine.ported.dragon_field_proliferation_core.tips", Text(
            "以龙形生物机械构型承载异种物质增殖反应",
            "Hosts exotic-matter proliferation within a draconic biomechanical frame"
        ))
        put("gtohjs.machine.ported.dragon_field_proliferation_core.breath_supply", Text(
            "龙息可由流体输入仓或样板总成提供。",
            "Supply Dragon Breath through a fluid input hatch or pattern buffer."
        ))
        put("gtohjs.machine.ported.plasma_machine_tool.intro", Text(
            "利用超导高温磁流体约束材料进行高精度加工，效率是普通大型机器的数倍",
            "Uses superconducting high-temperature magnetically confined plasma for high-precision processing, several times more efficient than ordinary large machines"
        ))
        put("gtohjs.machine.ported.hadron_refinery.intro", Text(
            "利用强子束流碰撞激发超临界催化状态，大幅提升各种流体与化学品的处理效率",
            "Collides hadron beams to trigger a supercritical catalytic state, greatly boosting fluid and chemical processing efficiency"
        ))
        put("gtohjs.machine.ported.quantum_mass_array.intro", Text(
            "精确扰动微观粒子质量，在同一设备中对极其复杂的混合矿源进行多重高纯度分离",
            "Precisely perturbs microscopic particle mass to perform multiple high-purity separations of extremely complex mixed ores in one device"
        ))
        put("gtohjs.machine.ported.fusion_assembler.intro", Text(
            "在极度压缩的约束场内熔合基础原料，直接批量生产各类精密电路与合成元件",
            "Fuses basic raw materials in an extremely compressed confinement field to mass-produce precision circuits and synthesized components"
        ))
        put("gtohjs.machine.order_iv.thread_hatch", Text(
            "未安装线程仓时只有 1 线程；线程数不随电压自动增加。",
            "Without a Thread Hatch there is only 1 thread; thread count does not rise with voltage."
        ))
        put("gtohjs.machine.order_iv.pattern_order", Text(
            "仅接受样板总成输入；扣料前检查总成模式与样板配方，组合模式下拒绝歧义订单。",
            "Pattern buffers are the only inputs; their mode and recorded recipe are checked before consumption, and ambiguous combined-mode orders are rejected."
        ))
        put("gtohjs.diagnostic.order.unknown_slot", Text("样板槽位无法识别", "Pattern slot could not be identified"))
        put("gtohjs.diagnostic.order.unavailable_type", Text("该配方类型不属于本机", "Recipe type is unavailable on this machine"))
        put("gtohjs.diagnostic.order.missing_recipe", Text("样板记录的配方已不存在", "The pattern's recorded recipe no longer exists"))
        put("gtohjs.diagnostic.order.wrong_mode", Text("样板配方与当前机器模式不符", "Pattern recipe conflicts with the selected machine mode"))
        put("gtohjs.diagnostic.order.wrong_recipe", Text("机器找到的配方与样板记录不符", "Found recipe differs from the pattern's recorded recipe"))
        put("gtohjs.diagnostic.order.no_match", Text("当前输入未匹配到配方", "Current input matches no recipe"))
        put("gtohjs.diagnostic.order.ambiguous", Text("当前输入匹配多个配方，请指定机器模式或配方", "Current input matches multiple recipes; select a mode or recorded recipe"))
        put("gtohjs.diagnostic.order.inferred_mode", Text("超级通配符总成的模式可能由输入自动推断", "The super wildcard buffer may infer its mode from inputs"))
    }

    /** Gradle runs this after Kotlin compilation and merges these entries into both packed locales. */
    @JvmStatic
    fun main(args: Array<String>) {
        require(args.size == 2) { "Expected source and generated language directories" }
        val source = Path.of(args[0])
        val output = Path.of(args[1], "assets", "gtohjs", "lang")
        Files.createDirectories(output)
        val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
        for (locale in arrayOf("zh_cn", "en_us")) {
            val input = source.resolve("$locale.json")
            val json = Files.newBufferedReader(input, StandardCharsets.UTF_8).use { JsonParser.parseReader(it).asJsonObject }
            translations.forEach { (key, value) -> json.addProperty(key, if (locale == "zh_cn") value.zh else value.en) }
            Files.newBufferedWriter(output.resolve("$locale.json"), StandardCharsets.UTF_8).use {
                gson.toJson(json, it)
            }
        }
        println("Generated ${translations.size} bilingual GTL machine entries from Kotlin")
    }
}
