package com.gtohjs.bootstrap;

import com.gtohjs.coremod.ProofLog;
import com.gtohjs.adaptivenet.AdaptiveNetRegistration;
import com.gtohjs.machines.ULVFragmentWorldCollectionMachine;
import com.gtohjs.methods.Fix2RegistrationMethods;
import com.gtohjs.methods.FullRegistrationMethods;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;

/** Cold lifecycle callbacks for the current machine-only adaptation node. */
public final class GTOHJSBootstrap {
    public static final int MACHINE = 0;
    public static final int RECIPE_TYPE = 1;
    public static final int RECIPE = 2;
    public static final int AE = 3;
    private static final String[] NAMES = {"machine", "recipe-type", "recipe", "AE"};
    private static final AtomicIntegerArray CALLS = new AtomicIntegerArray(NAMES.length);
    private static final AtomicBoolean COMPLETE = new AtomicBoolean();

    private GTOHJSBootstrap() {}

    public static void machineBridge() { ULVFragmentWorldCollectionMachine.register(); Fix2RegistrationMethods.registerMachines(); FullRegistrationMethods.registerThermalAndIntake(); AdaptiveNetRegistration.register(); observe(MACHINE); }
    public static void recipeTypeBridge() { ULVFragmentWorldCollectionMachine.registerInterfaceType(); Fix2RegistrationMethods.registerTypes(); observe(RECIPE_TYPE); }
    public static void recipeBridge() { observe(RECIPE); }
    public static void aeBridge() { FullRegistrationMethods.registerAE(); observe(AE); }

    private static void observe(int bridge) {
        if (CALLS.incrementAndGet(bridge) != 1) {
            throw new IllegalStateException("GTOHJS duplicate " + NAMES[bridge] + " lifecycle bridge");
        }
        ProofLog.record(NAMES[bridge] + " bridge executed: 1/1");
        if (allExecuted() && COMPLETE.compareAndSet(false, true)) {
            ProofLog.record("all four lifecycle bridges executed: PASS");
        }
    }

    public static boolean allExecuted() {
        for (int index = 0; index < NAMES.length; index++) {
            if (CALLS.get(index) != 1) return false;
        }
        return true;
    }

    public static void requireExecuted(int bridge) {
        if (CALLS.get(bridge) != 1) {
            throw new IllegalStateException("GTOHJS " + NAMES[bridge] +
                    " bridge has not executed exactly once");
        }
    }
}
