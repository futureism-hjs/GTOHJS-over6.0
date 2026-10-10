package com.gtohjs.methods;

import com.gregtechceu.gtceu.api.GTValues;
import com.gtohjs.blocks.InfiniteWirelessEnergyUnitBlock;
import net.minecraft.world.level.block.Block;

import java.math.BigInteger;

/** Reusable parameters and identity checks for the addon wireless energy unit. */
public final class InfiniteWirelessEnergyMethods {
    public static final BigInteger CAPACITY = BigInteger.ONE.shiftLeft(126).subtract(BigInteger.ONE);

    private InfiniteWirelessEnergyMethods() {}

    public static BigInteger capacity() { return CAPACITY; }

    public static int tier() { return GTValues.MAX; }

    public static int loss() { return 0; }

    public static boolean isInfiniteUnit(Block block) {
        return block instanceof InfiniteWirelessEnergyUnitBlock;
    }

    public static boolean isInfiniteCapacity(BigInteger capacity) {
        return capacity.compareTo(CAPACITY) >= 0;
    }
}
