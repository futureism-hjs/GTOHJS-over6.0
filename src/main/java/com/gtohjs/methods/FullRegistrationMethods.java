package com.gtohjs.methods;

import com.gtohjs.coremod.ProofLog;
import com.gtohjs.machines.*;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import net.minecraft.resources.ResourceLocation;
import java.util.HashSet;
import java.util.Set;

/** Cold lifecycle dispatch and ownership for the complete current baseline. */
public final class FullRegistrationMethods {
    private static final Set<ResourceLocation> OWNED=new HashSet<>();
    private static final String[] MULTIBLOCKS={"hyperdimensional_forge","hyperdimensional_steam_furnace","hyperdimensional_smelter",
        "hyperdimensional_chemical_factory","hyperdimensional_biochemical_factory","universal_steam_factory",
        "one_stop_rare_earth_processing_plant","advanced_generator_array","advanced_alchemy_cauldron","steam_array",
        "advanced_steam_array","large_petal_apothecary","large_fragment_world_collection_machine",
        "neutron_control_factory","platinum_refining_matrix","dragon_field_proliferation_core",
        "plasma_machine_tool","hadron_catalytic_refinery","quantum_mass_spectrum_array",
        "superconducting_fusion_assembler"};
    private static final String[] PARTS={"electromagnetic_thermal_control_hatch","electromagnetic_thermal_control_machine",
        "advanced_infinite_intake_hatch","ultimate_infinite_intake_hatch","me_input_assembly","me_stocking_input_assembly",
        "me_super_pattern_buffer","me_super_wildcard_pattern_buffer","me_super_pattern_buffer_proxy"};
    static {
        for(String name:MULTIBLOCKS) OWNED.add(new ResourceLocation("gtocore",name));
        for(String name:PARTS) OWNED.add(new ResourceLocation("gtocore",name));
        OWNED.add(new ResourceLocation("gtocore","ulv_fragment_world_collection_machine"));
    }
    private FullRegistrationMethods() {}
    public static boolean ownsMachine(ResourceLocation id) { return OWNED.contains(id); }
    private static void requireFree(String... names) {
        for(String name:names) if(GTRegistries.MACHINES.get(new ResourceLocation("gtocore",name))!=null)
            throw new IllegalStateException("HJS machine collision: "+name);
    }
    public static void registerThermalAndIntake() {
        requireFree(PARTS[0],PARTS[1],PARTS[2],PARTS[3]); ThermalAndIntakeHatchRegistration.register();
    }
    public static void registerAE() {
        requireFree(PARTS[4],PARTS[5],PARTS[6],PARTS[7],PARTS[8]);
        MEInputAssemblyRegistration.register(); MESuperPatternBufferRegistration.register(); MESuperWildcardPatternBufferRegistration.register();
    }
    public static void verifyLoaded() {
        ThermalAndIntakeHatchRegistration.validateLoaded(); MEInputAssemblyRegistration.validateLoaded();
        MESuperPatternBufferRegistration.validateLoaded(); MESuperWildcardPatternBufferRegistration.validateLoaded();
        VacuumCoverRegistration.validateLoaded();
        if(OWNED.size()!=30) throw new IllegalStateException("Expected 30 HJS machine IDs");
        for(var id:OWNED) if(GTRegistries.MACHINES.get(id)==null) throw new IllegalStateException("HJS machine missing: "+id);
        ProofLog.record("fix3 30 machine IDs and vacuum cover verified; added-by tooltip ownership ready");
    }
}
