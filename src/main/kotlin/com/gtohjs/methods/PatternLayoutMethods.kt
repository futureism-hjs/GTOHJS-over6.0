package com.gtohjs.methods

import com.gtohjs.config.MEPatternBufferConfig
import com.gtohjs.machines.MESuperPatternBufferPartMachine
import com.gtohjs.machines.MESuperWildcardPatternBufferPartMachine
import com.gtocore.common.machine.multiblock.part.ae.MEPatternPartMachine
import com.gregtechceu.gtceu.uipro.UIElement
import com.gregtechceu.gtceu.uipro.elements.PageView
import com.gregtechceu.gtceu.uipro.styletemplate.UISizes
import net.minecraft.network.chat.Component
import java.util.function.Consumer
import java.util.function.Supplier

/** Null/-1 preserves the upstream buffer layout. Static ABI is used by ASM. */
object PatternLayoutMethods {
    private fun custom(machine: MEPatternPartMachine<*>): Boolean =
        machine is MESuperPatternBufferPartMachine || machine is MESuperWildcardPatternBufferPartMachine

    @JvmStatic
    fun pageCount(machine: MEPatternPartMachine<*>): Int {
        if (!custom(machine)) return -1
        val slots = MEPatternBufferConfig.columnsFor(machine) * MEPatternBufferConfig.rowsFor(machine)
        return maxOf(1, (machine.maxPatternCount + slots - 1) / slots)
    }

    @JvmStatic
    fun pages(machine: MEPatternPartMachine<*>, width: Int, header: Consumer<UIElement>?,
              headerHeight: Int, emptyText: Supplier<Component>?): PageView? {
        if (!custom(machine)) return null
        val columns = MEPatternBufferConfig.columnsFor(machine)
        val rows = MEPatternBufferConfig.rowsFor(machine)
        val perPage = columns * rows
        val count = machine.maxPatternCount
        val pages = PageView(maxOf(width, columns * UISizes.SLOT),
            headerHeight + rows * UISizes.SLOT)
        for (start in 0 until count step perPage) {
            val end = minOf(count, start + perPage)
            pages.addPage { page ->
                page.layout { it.alignCenter().gapAll(UISizes.GAP.toFloat()) }
                header?.accept(page)
                val grid = UIElement.column(columns * UISizes.SLOT)
                for (rowStart in start until end step columns) {
                    val row = UIElement.row(UISizes.SLOT)
                    for (index in rowStart until minOf(end, rowStart + columns)) {
                        row.addChild(machine.createPatternSlot(index))
                    }
                    grid.addChild(row)
                }
                page.addChild(grid)
            }
        }
        return pages
    }
}
