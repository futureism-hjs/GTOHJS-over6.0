package com.gtohjs.client

import appeng.items.tools.powered.AbstractPortableCell
import com.gtohjs.GTOHJS
import com.gtohjs.data.GTOHJSItems
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterColorHandlersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

/** Reuses AE2's native LED and housing tint. */
@Mod.EventBusSubscriber(modid = GTOHJS.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object GTOHJSItemColorRegistration {
    @JvmStatic
    @SubscribeEvent
    fun registerItemColors(event: RegisterColorHandlersEvent.Item) {
        event.register({ stack, tint -> AbstractPortableCell.getColor(stack, tint) },
            GTOHJSItems.NORMAL_AE_COMPONENT_PACK.get(), GTOHJSItems.SUPER_AE_COMPONENT_PACK.get())
    }
}

