package com.gtohjs.adaptivenet;

import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gtocore.common.block.BlockMap;
import com.gtocore.common.data.GTOBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import static com.gregtechceu.gtceu.api.pattern.Predicates.abilities;

/** Allows the terminal in a tower frame position during pattern and size checks. */
public final class AdaptiveNetStructurePredicate {
    private AdaptiveNetStructurePredicate() {}

    public static TraceabilityPredicate towerCasing(TraceabilityPredicate casing) {
        return casing.or(abilities(AdaptiveNetRegistration.TERMINAL));
    }

    public static boolean isTowerShell(Block block) {
        return BlockMap.test(block, BlockMap.GLASSMAP) || isTowerFrame(block);
    }

    public static boolean isTowerFrame(Block block) {
        return block == GTOBlocks.WIRELESS_ENERGY_TOWER_FRAME.get() || isTerminal(block);
    }

    private static boolean isTerminal(Block block) {
        var definition = GTRegistries.MACHINES.get(
                ResourceLocation.fromNamespaceAndPath("gtocore", "adaptive_net_energy_terminal"));
        return definition != null && definition.get() == block;
    }
}
