package com.gtohjs.adaptivenet;

import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gtohjs.methods.AdaptiveNetLanguage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Portable, owner-checked frequency copy between a tower terminal and a hatch. */
public final class NetDataStickBehavior implements IInteractionItem, IAddInformation {
    public static final NetDataStickBehavior INSTANCE = new NetDataStickBehavior();
    private static final String FREQUENCY = "adaptive_net_frequency";

    private NetDataStickBehavior() {}

    @Override public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (level.isClientSide || player == null) return InteractionResult.PASS;
        MetaMachine machine = MetaMachine.getMachine(level, context.getClickedPos());
        if (machine instanceof AdaptiveNetTerminalPartMachine terminal && !player.isShiftKeyDown()) {
            if (!terminal.canConfigure(player)) {
                player.displayClientMessage(AdaptiveNetLanguage.component("gtocore.adaptive_net.permission_denied"), true);
                return InteractionResult.FAIL;
            }
            long frequency = terminal.frequency();
            if (frequency <= 0) {
                player.displayClientMessage(AdaptiveNetLanguage.component("gtocore.adaptive_net.no_frequency"), true);
                return InteractionResult.FAIL;
            }
            stack.getOrCreateTag().putLong(FREQUENCY, frequency);
            player.displayClientMessage(AdaptiveNetLanguage.component("gtocore.adaptive_net.frequency_copied", frequency), true);
            return InteractionResult.CONSUME;
        }
        if (machine instanceof AdaptiveNetHatchPartMachine hatch && player.isShiftKeyDown()) {
            long frequency = stack.getOrCreateTag().getLong(FREQUENCY);
            hatch.setFrequency(player, frequency);
            player.displayClientMessage(AdaptiveNetLanguage.component("gtocore.adaptive_net.frequency", hatch.frequency()), true);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override public InteractionResultHolder<ItemStack> use(Item item, Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                stack.getOrCreateTag().remove(FREQUENCY);
                player.displayClientMessage(AdaptiveNetLanguage.component("gtocore.adaptive_net.frequency_cleared"), true);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override public void appendTooltips(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag) {
        lines.add(AdaptiveNetLanguage.component("gtocore.adaptive_net.frequency", stack.getOrCreateTag().getLong(FREQUENCY)));
    }
}
