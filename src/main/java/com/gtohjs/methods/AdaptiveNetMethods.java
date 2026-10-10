package com.gtohjs.methods;

import com.gtohjs.adaptivenet.AdaptiveNetHatchPartMachine;
import com.gtohjs.adaptivenet.AdaptiveNetTerminalPartMachine;
import com.gtohjs.adaptivenet.AdaptiveTemplateRegistry;
import com.gtohjs.adaptivenet.FrequencyRegistry;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Read-only adaptive-net parameters; reservation and active state are distinct. */
public final class AdaptiveNetMethods {
    public record TerminalParameters(long frequency, boolean towerOnline, @Nullable UUID owner,
                                     long accountRate,
                                     Map<AdaptiveTemplateRegistry.Family, AdaptiveNetTerminalPartMachine.Power> power) {
        public AdaptiveNetTerminalPartMachine.Power power(AdaptiveTemplateRegistry.Family family) {
            return power.get(family);
        }
    }

    public record HatchParameters(long frequency, boolean connected, AdaptiveTemplateRegistry.Family family,
                                  AdaptiveNetTerminalPartMachine.Power power, long stored,
                                  long capacity, int priority) {}

    private AdaptiveNetMethods() {}

    public static TerminalParameters terminalParameters(AdaptiveNetTerminalPartMachine terminal) {
        var tower = terminal.tower();
        UUID owner = tower == null ? null : tower.getOwnerUUID();
        var powers = new EnumMap<AdaptiveTemplateRegistry.Family, AdaptiveNetTerminalPartMachine.Power>(AdaptiveTemplateRegistry.Family.class);
        for (var family : AdaptiveTemplateRegistry.Family.values()) powers.put(family, terminal.power(family));
        return new TerminalParameters(terminal.frequency(), tower != null, owner,
                WirelessGridMethods.rate(owner), Map.copyOf(powers));
    }

    public static HatchParameters hatchParameters(AdaptiveNetHatchPartMachine hatch) {
        return new HatchParameters(hatch.frequency(), hatch.connected(), hatch.family(), hatch.appliedPower(),
                hatch.bufferedEnergy(), hatch.bufferCapacity(), hatch.priority());
    }

    public static @Nullable FrequencyRegistry.Entry reservation(MinecraftServer server, long frequency) {
        return FrequencyRegistry.get(server).reserved(frequency);
    }

    public static @Nullable AdaptiveNetTerminalPartMachine activeTerminal(MinecraftServer server, long frequency) {
        return FrequencyRegistry.get(server).active(frequency, server);
    }

    public static List<Long> frequenciesOf(MinecraftServer server, UUID team, ResourceKey<Level> dimension) {
        return FrequencyRegistry.get(server).frequenciesOf(team, dimension);
    }

    public static @Nullable AdaptiveTemplateRegistry.Spec templateSpec(ItemStack stack, AdaptiveTemplateRegistry.Family family) {
        return AdaptiveTemplateRegistry.of(stack, family);
    }

    public static @Nullable GlobalPos terminalPosition(MinecraftServer server, long frequency) {
        var entry = reservation(server, frequency);
        return entry == null ? null : entry.terminal();
    }
}
