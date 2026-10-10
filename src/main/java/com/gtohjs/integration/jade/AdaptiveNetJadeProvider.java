package com.gtohjs.integration.jade;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gtohjs.GTOHJS;
import com.gtohjs.adaptivenet.AdaptiveNetHatchPartMachine;
import com.gtohjs.adaptivenet.AdaptiveNetTerminalPartMachine;
import com.gtohjs.adaptivenet.AdaptiveTemplateRegistry;
import com.gtohjs.methods.AdaptiveNetLanguage;
import com.gtohjs.methods.AdaptiveNetMethods;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/** Server-derived status and template tier for the adaptive network blocks. */
public enum AdaptiveNetJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final ResourceLocation UID = GTOHJS.id("adaptive_net_jade");

    @Override public ResourceLocation getUid() { return UID; }

    @Override public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        MetaMachine machine = MetaMachine.getMachine(accessor.getBlockEntity());
        CompoundTag status = new CompoundTag();
        if (machine instanceof AdaptiveNetTerminalPartMachine terminal) {
            var parameters = AdaptiveNetMethods.terminalParameters(terminal);
            status.putString("kind", "terminal");
            status.putLong("frequency", parameters.frequency());
            status.putBoolean("connected", parameters.towerOnline());
            for (var family : AdaptiveTemplateRegistry.Family.values()) {
                var power = parameters.power(family);
                status.putInt(family.name() + "_tier", power.tier());
                status.putLong(family.name() + "_amps", power.amperage());
            }
        } else if (machine instanceof AdaptiveNetHatchPartMachine hatch) {
            var parameters = AdaptiveNetMethods.hatchParameters(hatch);
            status.putString("kind", hatch.family().name());
            status.putLong("frequency", parameters.frequency());
            status.putBoolean("connected", parameters.connected());
        } else return;
        data.put(UID.toString(), status);
    }

    @Override public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag status = accessor.getServerData().getCompound(UID.toString());
        if (!status.contains("kind")) return;
        tooltip.add(AdaptiveNetLanguage.component("gtocore.adaptive_net.frequency", status.getLong("frequency")));
        tooltip.add(AdaptiveNetLanguage.component(status.getBoolean("connected") ?
                "gtocore.adaptive_net.connected" : "gtocore.adaptive_net.no_tower"));
        if ("terminal".equals(status.getString("kind"))) {
            for (var family : AdaptiveTemplateRegistry.Family.values()) {
                tooltip.add(AdaptiveNetLanguage.component("gtocore.adaptive_net.template_tier",
                        AdaptiveNetLanguage.component("gtocore.adaptive_net.family." + family.name().toLowerCase(java.util.Locale.ROOT)),
                        status.getInt(family.name() + "_tier"), status.getLong(family.name() + "_amps")));
            }
        }
    }
}
