package com.gtohjs.adaptivenet;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.client.renderer.machine.WorkableCasingMachineRenderer;
import com.gtocore.utils.register.MachineRegisterUtils;
import com.gtohjs.client.renderer.AdaptiveNetRenderer;
import net.minecraft.resources.ResourceLocation;

/** Definitions are inserted in the native GTO machine registration window. */
public final class AdaptiveNetRegistration {
    public static final PartAbility TERMINAL = new PartAbility("adaptive_net_terminal", "gtocore.part_ability.adaptive_net_terminal");
    private static final String[] IDS = {"adaptive_net_energy_input_hatch", "adaptive_net_energy_output_hatch",
            "adaptive_net_laser_target_hatch", "adaptive_net_laser_source_hatch", "adaptive_net_energy_terminal"};

    private AdaptiveNetRegistration() {}

    public static void register() {
        for (String id : IDS) if (GTRegistries.MACHINES.get(ResourceLocation.fromNamespaceAndPath("gtocore", id)) != null)
            throw new IllegalStateException("Adaptive network machine collision: " + id);
        hatch(IDS[0], "自适配电网能源仓", "Adaptive Grid Energy Input Hatch", AdaptiveTemplateRegistry.Family.ENERGY_INPUT,
                PartAbility.INPUT_ENERGY, "gtmthings", "block/overlay/machine/overlay_energy_on_wireless");
        hatch(IDS[1], "自适配电网动力仓", "Adaptive Grid Power Output Hatch", AdaptiveTemplateRegistry.Family.POWER_OUTPUT,
                PartAbility.OUTPUT_ENERGY, "gtmthings", "block/overlay/machine/overlay_energy_on_wireless");
        hatch(IDS[2], "自适配电网激光靶仓", "Adaptive Grid Laser Target Hatch", AdaptiveTemplateRegistry.Family.LASER_TARGET,
                PartAbility.INPUT_LASER, "gtmthings", "block/overlay/machine/overlay_energy_on_wireless_laser");
        hatch(IDS[3], "自适配电网激光源仓", "Adaptive Grid Laser Source Hatch", AdaptiveTemplateRegistry.Family.LASER_SOURCE,
                PartAbility.OUTPUT_LASER, "gtmthings", "block/overlay/machine/overlay_energy_on_wireless_laser");
        MachineRegisterUtils.machine(IDS[4], "自适配电网系统终端", AdaptiveNetTerminalPartMachine::new)
                .langValue("Adaptive Grid Terminal").tier(GTValues.LV).allRotation().abilities(TERMINAL)
                .renderer(() -> renderer("gtmthings", "block/machines/wireless_energy_monitor")).register();
    }

    private static MachineDefinition hatch(String id, String chinese, String english,
                                           AdaptiveTemplateRegistry.Family family, PartAbility ability,
                                           String overlayNamespace, String overlayPath) {
        return MachineRegisterUtils.machine(id, chinese, holder -> new AdaptiveNetHatchPartMachine(holder, family))
                .langValue(english).tier(GTValues.LV).allRotation().abilities(ability)
                .renderer(() -> new AdaptiveNetRenderer(ResourceLocation.fromNamespaceAndPath(overlayNamespace, overlayPath))).register();
    }

    private static WorkableCasingMachineRenderer renderer(String namespace, String overlay) {
        return new WorkableCasingMachineRenderer(ResourceLocation.fromNamespaceAndPath("gtocore", "block/manipulator"),
                ResourceLocation.fromNamespaceAndPath(namespace, overlay));
    }
}
