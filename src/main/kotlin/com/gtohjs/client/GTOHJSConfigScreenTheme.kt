package com.gtohjs.client

import com.gtohjs.GTOHJS
import com.gtohjs.config.MEPatternBufferConfig
import dev.toma.configuration.client.ConfigurationClient
import dev.toma.configuration.client.theme.ConfigTheme
import dev.toma.configuration.client.widget.render.SolidColorRenderer
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = GTOHJS.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object GTOHJSConfigScreenTheme {
    @JvmStatic
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork(Runnable {
            ConfigurationClient.setCustomConfigTheme(MEPatternBufferConfig.holder(), ::configure)
        })
    }

    private fun configure(theme: ConfigTheme) {
        theme.setHeader(ConfigTheme.Header(null, 0xFF303238.toInt(), 0xFFFFFFFF.toInt()))
        theme.setFooter(ConfigTheme.Footer(0xFF303238.toInt()))
        theme.setScrollbar(ConfigTheme.Scrollbar(6, 0xFF696D76.toInt()))
        theme.setBackgroundFillColor(0xFF202124.toInt())
        theme.setWidgetTextColor(0xFFE8E9EA.toInt(), 0xFFFFFFFF.toInt(), 0xFF8A8D93.toInt())
        theme.setConfigEntry(ConfigTheme.ConfigEntry(0xFFE8E9EA.toInt(), null, 0x332D3036))
        theme.setButtonBackground { widget ->
            SolidColorRenderer { if (widget.isHoveredOrFocused) 0xFF4A4D54.toInt() else 0xFF3B3D42.toInt() }
        }
        theme.setEditBoxBackground { SolidColorRenderer { 0xFF17181B.toInt() } }
        theme.setSliderBackground { SolidColorRenderer { 0xFF303238.toInt() } }
        theme.setSliderHandle { SolidColorRenderer { 0xFF777B84.toInt() } }
    }
}

