package com.gtohjs.client.renderer

import com.gregtechceu.gtceu.client.model.WorkableOverlayModel
import com.gregtechceu.gtceu.client.renderer.machine.WorkableCasingMachineRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.InventoryMenu
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import java.util.function.Consumer

/** Renders a native single-sprite front on the shared manipulator casing. */
class AdaptiveNetRenderer(private val front: ResourceLocation) : WorkableCasingMachineRenderer(
    ResourceLocation.fromNamespaceAndPath("gtocore", "block/manipulator"),
    ResourceLocation.fromNamespaceAndPath("gtohjs", "block/machines/adaptive_net_empty"),
) {
    @OnlyIn(Dist.CLIENT)
    override fun onPrepareTextureAtlas(atlas: ResourceLocation, register: Consumer<ResourceLocation>) {
        super.onPrepareTextureAtlas(atlas, register)
        if (InventoryMenu.BLOCK_ATLAS == atlas) {
            register.accept(front)
            overlayModel.sprites[WorkableOverlayModel.OverlayFace.FRONT] =
                WorkableOverlayModel.ActivePredicate(front, front, front, null, null, null)
        }
    }
}
