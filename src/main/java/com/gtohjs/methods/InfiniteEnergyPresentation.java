package com.gtohjs.methods;

import com.gtocore.api.wireless.energy.EnergyAccount;
import com.gtocore.api.wireless.energy.GridView;
import com.gtocore.common.wireless.energy.GridReadouts;
import com.gregtechceu.gtceu.uiwidgets.multiblock.MultiblockPage;
import com.gtolib.utils.StringUtils;
import com.hepdd.gtmthings.utils.FormatUtil;
import java.math.BigInteger;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import java.util.function.Function;
import java.util.function.Supplier;

public final class InfiniteEnergyPresentation {
    private static final String STORED_KEY = "gtohjs.wireless_energy.stored_infinite";
    private static final String MONITOR_VALUE_KEY = "gtohjs.wireless_energy.monitor_value";
    private static final double INFINITE_DOUBLE = InfiniteWirelessEnergyMethods.capacity().doubleValue();

    private InfiniteEnergyPresentation() {}

    public static boolean isInfinite(BigInteger capacity) {
        return InfiniteWirelessEnergyMethods.isInfiniteCapacity(capacity);
    }

    public static boolean isInfiniteSummary(GridView.Summary summary) {
        return summary.capacity() >= INFINITE_DOUBLE;
    }

    public static Component infiniteText() {
        return Component.translatable("gtohjs.wireless_energy.infinite_animated",
                Component.literal(StringUtils.full_color("Infinite")),
                Component.literal(StringUtils.full_color("无限")));
    }

    public static Component monitorStorage(EnergyAccount account) {
        if (!isInfinite(account.totalCapacity())) return GridReadouts.storage(account);
        return FormatUtil.formatWithConstantWidth("gtmthings.machine.wireless_energy_monitor.tooltip.1",
                Component.translatable(MONITOR_VALUE_KEY,
                        FormatUtil.formatBigIntegerNumberOrSic(account.totalStorage()), infiniteText()))
                .withStyle(ChatFormatting.GOLD);
    }

    public static Component summaryStorage(Component original, double stored, double capacity) {
        if (capacity < INFINITE_DOUBLE) return original;
        return Component.translatable(STORED_KEY, com.gtocore.common.wireless.energy.map.GridFormat.amount(stored), infiniteText());
    }

    public static Supplier<Component> stationCapacitySupplier(Supplier<BigInteger> source,
                                                              Function<BigInteger, Component> finiteFormatter) {
        Supplier<Component> finite = MultiblockPage.cachedRef(source, finiteFormatter);
        return () -> isInfinite(source.get())
                ? Component.translatable("gtohjs.wireless_energy.capacity_value", infiniteText())
                : finite.get();
    }
}
