package com.gtohjs.client

import com.gtohjs.GTOHJS
import com.gtohjs.methods.GtlPortedMachineText
import com.gtohjs.methods.AdaptiveNetLanguage
import com.gtohjs.methods.ModLog
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.language.I18n
import net.minecraft.locale.Language
import net.minecraft.network.chat.FormattedText
import net.minecraft.server.packs.resources.ResourceManagerReloadListener
import net.minecraft.util.FormattedCharSequence
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import java.lang.reflect.Modifier

/** Adds HJS machine and adaptive-net text to Minecraft's client language instance. */
@Mod.EventBusSubscriber(modid = GTOHJS.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object GTOHJSPreviewLanguage {
    private const val PROBE_KEY = "block.gtocore.neutron_control_factory"
    private const val ADAPTIVE_PROBE_KEY = "gtocore.adaptive_net.connected"

    // I18n.setLanguage(Language) is package-private in this Minecraft version. Find it by
    // its unique signature so this remains independent of mapped/production method names.
    private val setClientLanguage by lazy {
        I18n::class.java.declaredMethods.single {
            Modifier.isStatic(it.modifiers) && it.returnType == Void.TYPE &&
                it.parameterTypes.contentEquals(arrayOf(Language::class.java))
        }.apply {
            check(trySetAccessible()) { "Minecraft I18n language setter is inaccessible" }
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun register(event: RegisterClientReloadListenersEvent) {
        // Forge registers this after Minecraft's LanguageManager, so the native locale is
        // ready before this listener overlays the HJS machine and adaptive keys.
        event.registerReloadListener(ResourceManagerReloadListener {
            val locale = Minecraft.getInstance().options.languageCode
            val current = Language.getInstance()
            val base = if (current is MachineLanguage) current.base else current
            val overlay = MachineLanguage(base,
                GtlPortedMachineText.clientTranslations(locale) + AdaptiveNetLanguage.clientTranslations(locale))
            Language.inject(overlay)
            try {
                setClientLanguage.invoke(null, overlay)
                if (I18n.get(PROBE_KEY) == PROBE_KEY || I18n.get(ADAPTIVE_PROBE_KEY) == ADAPTIVE_PROBE_KEY) {
                    ModLog.error("GTO HJS client language still lacks {} or {}", PROBE_KEY, ADAPTIVE_PROBE_KEY)
                } else {
                    ModLog.info("GTO HJS preview language installed for {}", locale)
                }
            } catch (error: ReflectiveOperationException) {
                ModLog.error("Could not synchronize GTO HJS preview translations with Minecraft I18n", error)
            }
        })
    }

    private class MachineLanguage(val base: Language, additions: Map<String, String>) : Language() {
        private val all: MutableMap<String, String> = LinkedHashMap<String, String>(base.languageData).apply {
            putAll(additions)
        }

        override fun getOrDefault(key: String, fallback: String): String =
            all[key] ?: base.getOrDefault(key, fallback)

        override fun has(key: String): Boolean = all.containsKey(key) || base.has(key)

        override fun isDefaultRightToLeft(): Boolean = base.isDefaultRightToLeft

        override fun getVisualOrder(text: FormattedText): FormattedCharSequence = base.getVisualOrder(text)

        override fun getLanguageData(): MutableMap<String, String> = all
    }
}
