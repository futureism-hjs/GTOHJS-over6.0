package com.gtohjs.methods;

import com.gtocore.api.pattern.GTOPredicates;
import com.gtocore.common.block.BlockMap;
import com.gtocore.common.block.WirelessEnergyUnitBlock;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.predicates.SimplePredicate;
import com.gtocore.common.machine.multiblock.storage.WirelessEnergySubstationMachine;
import com.google.common.collect.Multimap;
import com.gtohjs.blocks.InfiniteWirelessEnergyUnitBlock;
import com.gtohjs.data.GTOHJSBlocks;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public final class InfiniteWirelessEnergyPredicate {
    private InfiniteWirelessEnergyPredicate() {}

    public static boolean countsAtCasingTier(WirelessEnergyUnitBlock block, int casingTier) {
        return block instanceof InfiniteWirelessEnergyUnitBlock || block.getTier() <= casingTier;
    }

    public static boolean allUnitsEligible(WirelessEnergySubstationMachine machine,
                                           Multimap<Integer, BlockPos> positions,
                                           int unitTier, int casingTier) {
        if (unitTier <= casingTier) return true;
        var level = machine.getLevel();
        if (level == null || positions.get(unitTier).isEmpty()) return false;
        for (BlockPos pos : positions.get(unitTier)) {
            if (!(level.getBlockState(pos).getBlock() instanceof InfiniteWirelessEnergyUnitBlock)) return false;
        }
        return true;
    }

    public static TraceabilityPredicate wirelessEnergyUnit() {
        return new TraceabilityPredicate(new SimplePredicate(state -> {
            if (!(state.getBlockState().getBlock() instanceof WirelessEnergyUnitBlock block)) return false;
            state.getMatchContext()
                    .getOrCreate(GTOPredicates.DataKeys.WIRELESS_ENERGY_UNIT, ArrayList::new)
                    .add(new WirelessEnergyUnitBlock.BlockData(block, state.getPos()));
            return true;
        }, () -> BlockInfo.fromBlock(GTOHJSBlocks.INFINITE_WIRELESS_ENERGY_UNIT.get()),
                InfiniteWirelessEnergyPredicate::candidates)).setPreviewCount(1);
    }

    private static Block[] candidates() {
        Block[] nativeUnits = BlockMap.WIRELESS_ENERGY_UNIT;
        Block[] candidates = Arrays.copyOf(nativeUnits, nativeUnits.length + 1);
        candidates[nativeUnits.length] = GTOHJSBlocks.INFINITE_WIRELESS_ENERGY_UNIT.get();
        return candidates;
    }
}
