package com.gtohjs.adaptivenet;

import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;

import static com.gregtechceu.gtceu.api.pattern.Predicates.abilities;

/** Called only at the tower's A casing predicate during static structure registration. */
public final class AdaptiveNetStructurePredicate {
    private AdaptiveNetStructurePredicate() {}

    public static TraceabilityPredicate towerCasing(TraceabilityPredicate casing) {
        return casing.or(abilities(AdaptiveNetRegistration.TERMINAL));
    }
}
