package com.gtohjs.adaptivenet;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.capability.GTCapability;
import com.gregtechceu.gtceu.api.machine.feature.IMachineLife;
import com.gregtechceu.gtceu.api.machine.multiblock.part.WorkableTieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableLaserContainer;
import com.gregtechceu.gtceu.api.recipe.handler.IO;
import com.gregtechceu.gtceu.uipro.LayoutStyle;
import com.gregtechceu.gtceu.uipro.UIElement;
import com.gregtechceu.gtceu.uipro.elements.NumberField;
import com.gregtechceu.gtceu.uiwidgets.display.MachineDisplay;
import com.gtocore.api.wireless.energy.EnergyPort;
import com.gtocore.api.wireless.energy.PortKind;
import com.gto.datasynclib.annotations.SaveToDisk;
import com.gto.datasynclib.annotations.SyncToClient;
import com.hepdd.gtmthings.utils.TeamUtil;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/** One shared dev11 container/port implementation; the four registrations select direction and capability. */
public final class AdaptiveNetHatchPartMachine extends WorkableTieredIOPartMachine
        implements IAdaptiveGridBypass, IMachineLife {
    private final AdaptiveTemplateRegistry.Family family;
    @SaveToDisk private final NotifiableEnergyContainer container;
    @SaveToDisk @SyncToClient private long frequency;
    @SaveToDisk @SyncToClient private boolean connected;
    @SaveToDisk private UUID placedOwner;
    @SaveToDisk private int priority = 2;
    private @Nullable UUID gridOwner;
    private @Nullable EnergyPort port;
    private @Nullable TickableSubscription networkSubscription;
    private AdaptiveNetTerminalPartMachine.Power applied = new AdaptiveNetTerminalPartMachine.Power(0, 0, 0, 0);

    public AdaptiveNetHatchPartMachine(MetaMachineBlockEntity holder, AdaptiveTemplateRegistry.Family family) {
        super(holder, 1, family == AdaptiveTemplateRegistry.Family.POWER_OUTPUT ||
                family == AdaptiveTemplateRegistry.Family.LASER_SOURCE ? IO.OUT : IO.IN);
        this.family = family;
        container = family == AdaptiveTemplateRegistry.Family.LASER_SOURCE ||
                family == AdaptiveTemplateRegistry.Family.LASER_TARGET
                ? new NotifiableLaserContainer(this, 0, 0, 0, 0, 0)
                : new NotifiableEnergyContainer(this, 0, 0, 0, 0, 0);
        container.setCapabilityValidator(side -> side == null);
        container.setSideInputCondition(side -> false);
        container.setSideOutputCondition(side -> false);
    }

    public long frequency() { return frequency; }
    public boolean connected() { return connected; }
    public AdaptiveTemplateRegistry.Family family() { return family; }

    @Override public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return placedOwner == null || placedOwner.equals(player.getUUID()) || sameTeam(player.getUUID());
    }

    @Override public @Nullable <T> Object getGTCapability(Class<T> cap, @Nullable Direction side) {
        if (side == null && cap == (family == AdaptiveTemplateRegistry.Family.LASER_SOURCE ||
                family == AdaptiveTemplateRegistry.Family.LASER_TARGET ? GTCapability.LASER : GTCapability.ENERGY_CONTAINER)) {
            return container;
        }
        return super.getGTCapability(cap, side);
    }

    @Override public void onLoad() {
        super.onLoad();
        if (!isRemote()) {
            if (placedOwner == null) placedOwner = getOwnerUUID();
            networkSubscription = subscribeServerTick(networkSubscription, this::tickNetwork, 10);
        }
    }

    @Override public void onUnload() {
        if (port != null) { port.release(); port = null; }
        if (networkSubscription != null) { networkSubscription.unsubscribe(); networkSubscription = null; }
        super.onUnload();
    }

    @Override public void onMachineRemoved() {
        if (port != null) { port.release(); port = null; }
        if (networkSubscription != null) { networkSubscription.unsubscribe(); networkSubscription = null; }
    }

    @Override public void onMachinePlaced(@Nullable LivingEntity placer, ItemStack stack) {
        if (placer != null) placedOwner = placer.getUUID();
    }

    public void setFrequency(Player player, long next) {
        if (!(getLevel() instanceof ServerLevel level) || next < 0) return;
        var entry = FrequencyRegistry.get(level.getServer()).reserved(next);
        if (next != 0 && (entry == null || entry.owner() == null || !sameTeam(entry.owner()) ||
                !TeamUtil.getTeamUUID(player.getUUID()).equals(TeamUtil.getTeamUUID(entry.owner())))) return;
        changeFrequency(next);
    }

    private void changeFrequency(long next) {
        if (!(getLevel() instanceof ServerLevel level) || next < 0) return;
        var entry = FrequencyRegistry.get(level.getServer()).reserved(next);
        if (next != 0 && (entry == null || !sameTeam(entry.owner()))) return;
        frequency = next;
        disconnect();
        onChanged();
        requestSync();
        tickNetwork();
    }

    private boolean sameTeam(@Nullable UUID owner) {
        if (owner == null || placedOwner == null) return owner == null;
        UUID first = TeamUtil.getTeamUUID(owner);
        UUID second = TeamUtil.getTeamUUID(placedOwner);
        return first != null && first.equals(second);
    }

    private void tickNetwork() {
        if (!(getLevel() instanceof ServerLevel level)) return;
        var terminal = FrequencyRegistry.get(level.getServer()).active(frequency, level.getServer());
        if (terminal == null || !sameTeam(terminal.towerOwner())) { disconnect(); return; }
        UUID owner = terminal.towerOwner();
        if (owner == null) { disconnect(); return; }
        var spec = terminal.power(family);
        if (!connected || !owner.equals(gridOwner) || !spec.equals(applied)) {
            connected = true;
            gridOwner = owner;
            applySpec(spec);
            requestSync();
        }
        if (port != null && isWorkingEnabled()) port.wake();
    }

    private void disconnect() {
        if (port != null) { port.release(); port = null; }
        if (connected || container.getEnergyStored() != 0) {
            connected = false;
            gridOwner = null;
            container.setEnergyStored(0);
            applySpec(new AdaptiveNetTerminalPartMachine.Power(0, 0, 0, 0));
            requestSync();
        }
    }

    private void applySpec(AdaptiveNetTerminalPartMachine.Power spec) {
        if (port != null) { port.release(); port = null; }
        applied = spec;
        long capacity;
        try { capacity = Math.multiplyExact(spec.euPerTick(), 64); }
        catch (ArithmeticException ignored) { capacity = Long.MAX_VALUE; }
        if (io == IO.IN) container.resetBasicInfo(capacity, spec.voltage(), spec.amperage(), 0, 0);
        else container.resetBasicInfo(capacity, 0, 0, spec.voltage(), spec.amperage());
        if (container.getEnergyStored() > capacity) container.setEnergyStored(capacity);
        if (connected && spec.voltage() > 0) {
            port = new EnergyPort(PortKind.HATCH, new EnergyPort.Host() {
                @Override public @Nullable UUID owner() { return gridOwner; }
                @Override public @Nullable Level level() { return getLevel(); }
                @Override public AdaptiveNetHatchPartMachine machine() { return AdaptiveNetHatchPartMachine.this; }
            }, spec.tier());
            port.setPriority(priority);
            port.setService(this::serve);
            port.wake();
        }
        for (var controller : getControllers()) controller.onStructureFormed();
        onChanged();
    }

    private int serve(EnergyPort activePort) {
        if (!connected || !isWorkingEnabled() || applied.euPerTick() == 0) return 20;
        long stored = container.getEnergyStored();
        if (io == IO.IN) {
            long want = Math.min(applied.euPerTick(), container.getEnergyCapacity() - stored);
            if (want <= 0) return 20;
            long got = activePort.pull(want);
            if (got > 0) container.setEnergyStored(stored + got);
            return 1;
        }
        long want = Math.min(stored, applied.euPerTick());
        if (want <= 0) return 20;
        long sent = activePort.push(want);
        if (sent > 0) container.setEnergyStored(stored - sent);
        return 1;
    }

    public void setPriority(int next) {
        priority = Math.max(0, Math.min(4, next));
        if (port != null) port.setPriority(priority);
        onChanged();
    }

    @Override public Widget createUIWidget() {
        UIElement page = MachineDisplay.page(this, lines -> {
            lines.add(Component.translatable("gtocore.adaptive_net.frequency", frequency));
            lines.add(Component.translatable(connected ? "gtocore.adaptive_net.connected" : "gtocore.adaptive_net.disconnected"));
            lines.add(Component.translatable("gtocore.adaptive_net.capacity", container.getEnergyCapacity()));
            lines.add(Component.translatable("gtocore.adaptive_net.throughput", applied.euPerTick()));
        });
        page.addChild(NumberField.ofLong(LayoutStyle.AUTO, this::frequency, this::changeFrequency, 0, Long.MAX_VALUE));
        page.addChild(NumberField.ofInt(LayoutStyle.AUTO, () -> priority, this::setPriority, 0, 4));
        return page;
    }
}
