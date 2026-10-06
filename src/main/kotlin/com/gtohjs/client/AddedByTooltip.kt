package com.gtohjs.client

import com.gtohjs.GTOHJS
import com.gtohjs.methods.FullRegistrationMethods
import com.gtohjs.methods.GtlPortedMachineText
import net.minecraft.client.Minecraft
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.ForgeRegistries

/** Fixed attribution colors preserve the existing item ownership filter. */
@Mod.EventBusSubscriber(modid = GTOHJS.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object AddedByTooltip {
    private val logo = Component.literal("GTO HJS")
        .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFA500)))

    @JvmStatic
    fun attribution(): Component = Component.translatable("gtohjs.tooltip.added_by", logo)
        .withStyle(ChatFormatting.DARK_PURPLE)

    @JvmStatic
    @SubscribeEvent
    fun onTooltip(event: ItemTooltipEvent) {
        val id = ForgeRegistries.ITEMS.getKey(event.itemStack.item) ?: return
        if (id.namespace == "gtocore") {
            GtlPortedMachineText.localizeHoverText(event.toolTip, id.path, Minecraft.getInstance().options.languageCode)
        }
        if (id.namespace == GTOHJS.MOD_ID || FullRegistrationMethods.ownsMachine(id)) {
            event.toolTip.add(attribution())
            if (id.namespace == "gtocore" && GtlPortedMachineText.isGtlMachine(id.path)) {
                event.toolTip.add(GtlPortedMachineText.structureSource())
            }
        }
    }
}
