package com.gtohjs.machines

import com.gregtechceu.gtceu.api.gui.GuiTextures
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider
import com.gregtechceu.gtceu.api.gui.fancy.TabsWidget
import com.gregtechceu.gtceu.api.gui.widget.LongInputWidget
import com.gregtechceu.gtceu.api.gui.widget.CoverConfigurator
import com.gregtechceu.gtceu.api.cover.IUICover
import com.gregtechceu.gtceu.api.machine.MetaMachine
import com.gregtechceu.gtceu.uipro.UIElement
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture
import com.lowdragmc.lowdraglib.gui.texture.TextTexture
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget
import com.lowdragmc.lowdraglib.gui.widget.Widget
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import java.util.Locale

/** Static methods and nested host interfaces retain the Java machine ABI. */
object ElectromagneticThermalControlUI {
    interface TemperatureHost {
        fun getTargetTemperature(): Long
        fun setTargetTemperature(temperature: Long)
    }
    interface Host : TemperatureHost {
        fun getHeatOutputFacing(): Direction
        fun setHeatOutputFacing(direction: Direction)
    }

    @JvmStatic
    fun createTemperatureWidget(host: TemperatureHost): Widget {
        val page = WidgetGroup(0, 0, 118, 38)
        page.setBackground(GuiTextures.BACKGROUND_INVERSE)
        page.addWidget(LabelWidget(4, 5,
            Component.translatable("gtohjs.machine.electromagnetic_thermal_control.target")).setClientSideWidget())
        val input = LongInputWidget(4, 20, 110, 14, host::getTargetTemperature, host::setTargetTemperature)
        input.setMin(0L)
        input.setMax(ElectromagneticThermalControlHatchPartMachine.MAX_TEMPERATURE)
        page.addWidget(input)
        return page
    }

    @JvmStatic
    fun attachHeatOutputControls(sideTabs: TabsWidget, machine: MetaMachine, host: Host) {
        sideTabs.attachSubTab(object : IFancyUIProvider {
            override fun getTitle(): Component =
                Component.translatable("gtohjs.machine.electromagnetic_thermal_control.machine_control")
            override fun getTabIcon(): IGuiTexture = GuiTextures.BUTTON_FLUID_OUTPUT
            override fun createMainPage(widget: FancyMachineUIWidget): Widget {
                val page = UIElement.column(176)
                page.addChild(LabelWidget(0, 0,
                    Component.translatable("gtohjs.machine.electromagnetic_thermal_control.heat_direction")).setClientSideWidget())
                val row = UIElement.row(22)
                for (direction in Direction.entries) {
                    row.addChild(ButtonWidget(0, 0, 26, 20) { click ->
                        if (!click.isRemote) host.setHeatOutputFacing(direction)
                    }.setButtonTexture(GuiTextureGroup(GuiTextures.VANILLA_BUTTON,
                        TextTexture(direction.name.substring(0, 1).uppercase(Locale.ROOT))))
                        .setHoverTooltips(Component.translatable("gtohjs.direction." + direction.getName())))
                }
                page.addChild(row)
                for (direction in Direction.entries) {
                    val cover = machine.coverContainer.getCoverAtSide(direction)
                    if (cover is IUICover) {
                        page.addChild(LabelWidget(0, 0,
                            Component.translatable("gtohjs.direction." + direction.getName())).setClientSideWidget())
                        page.addChild(CoverConfigurator(machine.coverContainer, direction, cover).createConfigurator())
                    }
                }
                return page
            }
        })
    }
}

