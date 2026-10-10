package com.gtohjs.methods;

import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import java.util.ArrayList;
import java.util.List;

/** The actual pattern-buffer blocks accepted by the four order machines. */
public final class PatternBufferPlacementMethods {
    private static final String[] REQUIRED = {
            "gtceu:me_pattern_buffer",
            "gtocore:me_wildcard_pattern_buffer",
            "gtocore:me_extend_pattern_buffer",
            "gtocore:me_extend_pattern_buffer_ultra",
            "gtocore:me_super_pattern_buffer",
            "gtocore:me_super_wildcard_pattern_buffer"
    };
    private static final ResourceLocation OPTIONAL_SIMPLE =
            ResourceLocation.fromNamespaceAndPath("gtocore", "me_simple_pattern_buffer");

    private PatternBufferPlacementMethods() {}

    /** Create once per structure and share between its X and Q positions. */
    public static TraceabilityPredicate patternBuffersOnly() {
        List<Block> blocks = new ArrayList<>();
        for (String id : REQUIRED) {
            blocks.add(requiredBlock(ResourceLocation.parse(id)));
        }
        var simple = GTRegistries.MACHINES.get(OPTIONAL_SIMPLE);
        if (simple != null) blocks.add(simple.get());
        return Predicates.blocks(blocks.toArray(Block[]::new))
                .setMinGlobalLimited(1).setPreviewCount(1);
    }

    private static Block requiredBlock(ResourceLocation id) {
        var definition = GTRegistries.MACHINES.get(id);
        if (definition == null) throw new IllegalStateException("Pattern buffer missing: " + id);
        return definition.get();
    }

    public static void verifyLoaded() {
        for (String id : REQUIRED) requiredBlock(ResourceLocation.parse(id));
    }
}
