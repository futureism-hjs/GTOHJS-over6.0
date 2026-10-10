package com.gtohjs.client.renderer

import com.gregtechceu.gtceu.GTCEu
import com.gregtechceu.gtceu.api.capability.IWorkable
import com.gregtechceu.gtceu.api.machine.MachineDefinition
import com.gregtechceu.gtceu.api.machine.MetaMachine
import com.gregtechceu.gtceu.client.model.WorkableOverlayModel
import com.gregtechceu.gtceu.client.renderer.machine.TieredHullMachineRenderer
import com.gregtechceu.gtceu.client.util.StaticFaceBakery
import com.gtocore.client.renderer.machine.IHeaterRenderer
import com.lowdragmc.lowdraglib.client.bakedpipeline.Quad
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.resources.model.ModelState
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.inventory.InventoryMenu
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import java.util.function.Consumer

/** Existing ordinary front animation and native GTO side thermometers. */
class ElectromagneticThermalControlRenderer(tier: Int, workableModel: ResourceLocation) :
    TieredHullMachineRenderer(tier, GTCEu.id("block/machine/hull_machine")), IHeaterRenderer {
    private val normalFrontOverlay = WorkableOverlayModel(workableModel)

    @OnlyIn(Dist.CLIENT)
    override fun renderMachine(quads: MutableList<BakedQuad>, definition: MachineDefinition,
        machine: MetaMachine?, frontFacing: Direction, side: Direction?, rand: RandomSource,
        modelFacing: Direction?, modelState: ModelState) {
        super.renderMachine(quads, definition, machine, frontFacing, side, rand, modelFacing, modelState)
        val workable = machine as? IWorkable
        renderNormalFrontOverlay(quads, side, modelState,
            workable?.isActive == true, workable?.isWorkingEnabled == true)
        renderHeater(quads, definition, machine, frontFacing, side, rand, modelFacing, modelState)
    }

    @OnlyIn(Dist.CLIENT)
    private fun renderNormalFrontOverlay(quads: MutableList<BakedQuad>, side: Direction?,
        modelState: ModelState, active: Boolean, workingEnabled: Boolean) {
        for (renderSide in Direction.entries) {
            val predicate = normalFrontOverlay.sprites[WorkableOverlayModel.OverlayFace.bySide(renderSide)] ?: continue
            val texture = predicate.getSprite(active, workingEnabled) ?: continue
            val quad = StaticFaceBakery.bakeFace(StaticFaceBakery.SLIGHTLY_OVER_BLOCK,
                renderSide, texture, modelState, -1, 0, true, true)
            if (quad.direction == side) quads.add(Quad.from(quad, 0.002f).rebake())
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun onPrepareTextureAtlas(atlasName: ResourceLocation, register: Consumer<ResourceLocation>) {
        super.onPrepareTextureAtlas(atlasName, register)
        if (atlasName == InventoryMenu.BLOCK_ATLAS) normalFrontOverlay.registerTextureAtlas(register)
    }
}
