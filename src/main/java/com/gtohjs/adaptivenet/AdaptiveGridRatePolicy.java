package com.gtohjs.adaptivenet;

import com.gtocore.api.wireless.energy.EnergyPort;

/** The native grid still checks node reach, relay transport and actual account balance. */
public final class AdaptiveGridRatePolicy {
    private AdaptiveGridRatePolicy() {}

    public static boolean bypass(EnergyPort port) {
        return port.machine() instanceof IAdaptiveGridBypass;
    }

    public static long effectiveRate(long nativeRate, EnergyPort port) {
        return bypass(port) ? Long.MAX_VALUE : nativeRate;
    }
}
