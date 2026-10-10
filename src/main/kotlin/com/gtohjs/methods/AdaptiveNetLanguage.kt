package com.gtohjs.methods

import com.google.gson.JsonParser
import net.minecraft.network.chat.Component
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/** Exposes the addon's adaptive-net locale entries to GTO's client language overlay. */
object AdaptiveNetLanguage {
    private val english by lazy { load("en_us") }
    private val chinese by lazy { load("zh_cn") }

    @JvmStatic
    fun clientTranslations(locale: String): Map<String, String> =
        if (locale == "zh_cn") chinese else english

    /** Keeps a readable English fallback if a client resource reload has not run yet. */
    @JvmStatic
    fun component(key: String, vararg args: Any): Component =
        Component.translatableWithFallback(key, english[key] ?: key, *args)

    private fun load(locale: String): Map<String, String> {
        // The addon namespace has a unique classpath resource; the gtocore path
        // also exists in GTOCore and may resolve to its copy first.
        val path = "/assets/gtohjs/lang/$locale.json"
        val stream = checkNotNull(AdaptiveNetLanguage::class.java.getResourceAsStream(path)) {
            "Missing adaptive-net language resource: $path"
        }
        val json = InputStreamReader(stream, StandardCharsets.UTF_8).use {
            JsonParser.parseReader(it).asJsonObject
        }
        return json.entrySet().asSequence()
            .filter { (key, _) -> key.startsWith("gtocore.adaptive_net.") ||
                key.startsWith("block.gtocore.adaptive_net_") ||
                key == "item.gtocore.net_data_stick" || key == "gtocore.part_ability.adaptive_net_terminal" }
            .associate { (key, value) -> key to value.asString }
    }
}
