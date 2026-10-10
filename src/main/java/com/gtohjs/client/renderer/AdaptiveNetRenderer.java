package com.gtohjs.client.renderer;

import com.gregtechceu.gtceu.client.model.WorkableOverlayModel;
import com.gregtechceu.gtceu.client.renderer.machine.WorkableCasingMachineRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;

/** Renders a native single-sprite front on the shared manipulator casing. */
public final class AdaptiveNetRenderer extends WorkableCasingMachineRenderer {
    private final ResourceLocation front;

    public AdaptiveNetRenderer(ResourceLocation front) {
        super(ResourceLocation.fromNamespaceAndPath("gtocore", "block/manipulator"),
                ResourceLocation.fromNamespaceAndPath("gtohjs", "block/machines/adaptive_net_empty"));
        this.front = front;
    }

    @Override @OnlyIn(Dist.CLIENT)
    public void onPrepareTextureAtlas(ResourceLocation atlas, Consumer<ResourceLocation> register) {
        super.onPrepareTextureAtlas(atlas, register);
        if (InventoryMenu.BLOCK_ATLAS.equals(atlas)) {
            register.accept(front);
            overlayModel.sprites.put(WorkableOverlayModel.OverlayFace.FRONT,
                    new WorkableOverlayModel.ActivePredicate(front, front, front, null, null, null));
        }
    }
}
