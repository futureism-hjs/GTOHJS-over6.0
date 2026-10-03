package com.gtohjs.client

import com.gtohjs.GTOHJS
import com.gtohjs.methods.FullRegistrationMethods
import com.gtocore.api.lang.toComponentSupplier
import net.minecraft.network.chat.Component
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.ForgeRegistries

/** Native GTO scrolling colors apply only to the HJS name. */
@Mod.EventBusSubscriber(modid = GTOHJS.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object AddedByTooltip {
    private val logo = Component.literal("GTO HJS").toComponentSupplier().scrollFullColor()

    @JvmStatic
    fun attribution(): Component = Component.translatable(
        "gtohjs.tooltip.added_by", logo.get().append(Component.literal("§r")))

    @JvmStatic
    @SubscribeEvent
    fun onTooltip(event: ItemTooltipEvent) {
        val id = ForgeRegistries.ITEMS.getKey(event.itemStack.item) ?: return
        if (id.namespace == GTOHJS.MOD_ID || FullRegistrationMethods.ownsMachine(id)) {
            event.toolTip.add(attribution())
        }
    }
}
