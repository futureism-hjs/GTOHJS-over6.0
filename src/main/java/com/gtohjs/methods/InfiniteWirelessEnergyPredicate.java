package com.gtohjs.methods;

import com.gtocore.api.pattern.GTOPredicates;
import com.gtocore.common.block.WirelessEnergyUnitBlock;
import com.gtocore.common.block.WirelessEnergyTowerUnit;
import com.gtocore.api.wireless.energy.Provider;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.GTValues;
import com.gtocore.common.machine.multiblock.storage.WirelessEnergySubstationMachine;
import com.google.common.collect.Multimap;
import com.gtohjs.blocks.InfiniteWirelessEnergyUnitBlock;
import com.gtohjs.data.GTOHJSBlocks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;

public final class InfiniteWirelessEnergyPredicate {
    private InfiniteWirelessEnergyPredicate() {}

    public static boolean countsAtCasingTier(WirelessEnergyTowerUnit unit, int casingTier) {
        return InfiniteWirelessEnergyMethods.isInfiniteUnit(unit.towerBlock()) || unit.getTier() <= casingTier;
    }

    public static boolean overTier(Multimap<Integer, BlockPos> positions, int casingTier,
                                   WirelessEnergySubstationMachine machine) {
        var level = machine.getLevel();
        if (level == null) return false;
        for (var entry : positions.entries()) {
            if (entry.getKey() > casingTier &&
                    !InfiniteWirelessEnergyMethods.isInfiniteUnit(level.getBlockState(entry.getValue()).getBlock())) {
                return true;
            }
        }
        return false;
    }

    public static TraceabilityPredicate wirelessEnergyUnit() {
        var addon = GTOPredicates.dataBlock(GTOPredicates.DataKeys.WIRELESS_ENERGY_UNIT,
                ArrayList::new, (data, state) -> {
                    data.add(new WirelessEnergyTowerUnit.BlockData(
                            (WirelessEnergyTowerUnit) state.getBlockState().getBlock(), state.getPos()));
                    return data;
                }, GTOHJSBlocks.INFINITE_WIRELESS_ENERGY_UNIT.get());
        return GTOPredicates.wirelessEnergyUnit().or(addon).setPreviewCount(1);
    }

    public static List<Provider.Unit> withInfiniteUnit(WirelessEnergySubstationMachine machine,
                                                        Multimap<Integer, BlockPos> positions,
                                                        List<Provider.Unit> nativeUnits) {
        var level = machine.getLevel();
        if (level == null) return nativeUnits;
        int count = 0;
        for (var pos : positions.get(GTValues.MAX)) {
            if (InfiniteWirelessEnergyMethods.isInfiniteUnit(level.getBlockState(pos).getBlock())) count++;
        }
        if (count == 0) return nativeUnits;
        var result = new ArrayList<Provider.Unit>(nativeUnits.size() + 1);
        var nativeMax = WirelessEnergyUnitBlock.get(GTValues.MAX);
        for (var unit : nativeUnits) {
            if (nativeMax != null && unit.tier() == GTValues.MAX &&
                    unit.capacity().equals(nativeMax.getCapacity()) && unit.loss() == nativeMax.getLoss()) {
                int remaining = unit.count() - count;
                if (remaining < 0) throw new IllegalStateException("Infinite wireless unit count exceeds native MAX count");
                if (remaining > 0) result.add(new Provider.Unit(unit.tier(), remaining, unit.capacity(), unit.loss()));
            } else {
                result.add(unit);
            }
        }
        var addon = GTOHJSBlocks.INFINITE_WIRELESS_ENERGY_UNIT.get();
        result.add(new Provider.Unit(addon.isGridScaled() ? GTValues.MAX : Provider.Unit.FIXED,
                count, addon.getCapacity(), addon.getLoss()));
        return result;
    }
}
