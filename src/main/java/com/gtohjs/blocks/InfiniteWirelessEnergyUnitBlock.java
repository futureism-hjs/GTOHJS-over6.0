package com.gtohjs.blocks;

import com.gtocore.common.block.WirelessEnergyUnitBlock;
import com.gtocore.common.data.machines.MultiBlockG;
import com.gregtechceu.gtceu.api.GTValues;
import com.gtohjs.methods.InfiniteEnergyPresentation;
import java.math.BigInteger;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class InfiniteWirelessEnergyUnitBlock extends WirelessEnergyUnitBlock {
    public static final BigInteger CAPACITY = BigInteger.ONE.shiftLeft(126).subtract(BigInteger.ONE);

    public InfiniteWirelessEnergyUnitBlock(Properties properties) {
        super(properties, GTValues.LV);
    }

    @Override
    public BigInteger getCapacity() {
        return CAPACITY;
    }

    @Override
    public int getLoss() {
        return 0;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("gtocore.machine.tooltip.upgrade_action",
                MultiBlockG.WIRELESS_ENERGY_SUBSTATION.get().getName(),
                Component.translatable("gtocore.adv_terminal.block_map.wireless_energy_unit"))
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("gtohjs.wireless_energy.capacity", InfiniteEnergyPresentation.infiniteText()));
        tooltip.add(Component.translatable("gtohjs.wireless_energy.loss"));
    }
}
