package com.gtohjs.integration.jade;

import com.gtocore.common.machine.multiblock.storage.WirelessEnergySubstationMachine;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gtohjs.GTOHJS;
import com.gtohjs.methods.InfiniteEnergyPresentation;
import java.math.BigInteger;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;

public enum InfiniteEnergyJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final ResourceLocation UID = GTOHJS.id("infinite_energy_jade");
    private static final ResourceLocation ORIGINAL = GTCEu.id("electric_container_provider");
    private static final BigInteger SIC_THRESHOLD = BigInteger.valueOf(1_000_000_000_000L);

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public int getDefaultPriority() {
        // Jade's larger priorities append later, after GTCEu's default row.
        return 100;
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(MetaMachine.getMachine(accessor.getBlockEntity()) instanceof WirelessEnergySubstationMachine station)) return;
        var energy = station.getEnergyInfo();
        if (!InfiniteEnergyPresentation.isInfinite(energy.capacity())) return;
        var capacityData = new CompoundTag();
        capacityData.putBoolean("infinite", true);
        capacityData.putByteArray("stored", energy.stored().toByteArray());
        capacityData.putByteArray("capacity", energy.capacity().toByteArray());
        data.put(UID.toString(), capacityData);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        var data = accessor.getServerData().getCompound(UID.toString());
        if (!data.getBoolean("infinite")) return;
        BigInteger stored = new BigInteger(data.getByteArray("stored"));
        BigInteger capacity = new BigInteger(data.getByteArray("capacity"));
        if (!InfiniteEnergyPresentation.isInfinite(capacity)) return;
        tooltip.remove(ORIGINAL);
        var helper = tooltip.getElementHelper();
        tooltip.add(helper.progress(
                (float) (stored.doubleValue() / capacity.doubleValue()),
                Component.translatable("gtceu.jade.energy_stored", FormattingUtil.formatNumberOrSic(stored, SIC_THRESHOLD),
                        InfiniteEnergyPresentation.infiniteText()),
                helper.progressStyle().color(0xFFEEE600, 0xFFEEE600).textColor(-1),
                Util.make(BoxStyle.DEFAULT, style -> style.borderColor = 0xFF555555),
                true));
    }
}
