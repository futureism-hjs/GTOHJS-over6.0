package com.gtohjs.client.renderer

import com.gregtechceu.gtceu.client.renderer.machine.OverlayTieredMachineRenderer
import com.gtolib.GTOCore
import net.minecraft.resources.ResourceLocation

/** Native animated ME front on the existing amprosium casing. */
class AmprosiumPatternBufferRenderer(tier: Int, overlayModel: ResourceLocation) :
    OverlayTieredMachineRenderer(tier, overlayModel) {
    init {
        updateModelWithoutReloadingResource(GTOCore.id("block/amprosium_active_casing"))
        setTextureOverride(mapOf(
            "all" to GTOCore.id("block/neutronium_active_casing"),
            "side" to GTOCore.id("block/neutronium_active_casing")))
    }
}

