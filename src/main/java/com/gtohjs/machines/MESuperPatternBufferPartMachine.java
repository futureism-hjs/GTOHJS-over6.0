package com.gtohjs.machines;

import com.gtohjs.config.MEPatternBufferConfig;
import com.gtocore.common.machine.multiblock.part.ae.PatternBufferType;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;

/** Configurable high-capacity ME pattern buffer that retains GTO's native AE behavior. */
public final class MESuperPatternBufferPartMachine extends MEOutputCapablePatternBufferPartMachine {
    public MESuperPatternBufferPartMachine(MetaMachineBlockEntity holder, PatternBufferType type) {
        super(holder, type);
    }
}
