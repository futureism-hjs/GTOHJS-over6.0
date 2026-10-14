package com.gtohjs.client.renderer

import com.gregtechceu.gtceu.client.model.WorkableOverlayModel
import com.gregtechceu.gtceu.client.renderer.machine.WorkableCasingMachineRenderer
import com.lowdragmc.lowdraglib.utils.ResourceHelper
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.InventoryMenu
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import java.util.Locale
import java.util.function.Consumer

/** Keeps the native casing renderer while registering addon fronts during atlas preparation. */
open class ReliableWorkableCasingRenderer @JvmOverloads constructor(
    baseCasing: ResourceLocation,
    private val workableModel: ResourceLocation,
    tint: Boolean = true,
) : WorkableCasingMachineRenderer(baseCasing, workableModel, tint) {

    @OnlyIn(Dist.CLIENT)
    override fun onPrepareTextureAtlas(atlasName: ResourceLocation, register: Consumer<ResourceLocation>) {
        super.onPrepareTextureAtlas(atlasName, register)
        if (atlasName == InventoryMenu.BLOCK_ATLAS) {
            registerMissingOverlaySprites(overlayModel, workableModel, register)
        }
    }
}

/** The normal front is required; other faces and animation frames retain native discovery. */
@OnlyIn(Dist.CLIENT)
fun registerMissingOverlaySprites(
    model: WorkableOverlayModel,
    base: ResourceLocation,
    register: Consumer<ResourceLocation>,
) {
    for (face in WorkableOverlayModel.OverlayFace.VALUES) {
        if (model.sprites.containsKey(face)) continue
        val path = "${base.path}/overlay_${face.name.lowercase(Locale.ROOT)}"
        fun sprite(suffix: String): ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(base.namespace, path + suffix)
        val normal = sprite("")
        if (face != WorkableOverlayModel.OverlayFace.FRONT && !ResourceHelper.isTextureExist(normal)) continue
        register.accept(normal)
        fun optional(suffix: String): ResourceLocation? {
            val location = sprite(suffix)
            if (!ResourceHelper.isTextureExist(location)) return null
            register.accept(location)
            return location
        }
        val active = optional("_active") ?: normal
        val paused = optional("_paused") ?: normal
        model.sprites[face] = WorkableOverlayModel.ActivePredicate(
            normal, active, paused,
            optional("_emissive"), optional("_active_emissive"), optional("_paused_emissive"),
        )
    }
}
