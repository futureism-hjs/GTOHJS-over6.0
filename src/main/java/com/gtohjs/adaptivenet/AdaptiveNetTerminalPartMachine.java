package com.gtohjs.adaptivenet;

import appeng.api.stacks.AEItemKey;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IMachineLife;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableInventory;
import com.gregtechceu.gtceu.api.recipe.handler.IO;
import com.gregtechceu.gtceu.api.transfer.key.KeyInventory;
import com.gregtechceu.gtceu.uipro.LayoutStyle;
import com.gregtechceu.gtceu.uipro.UIElement;
import com.gregtechceu.gtceu.uipro.elements.ItemSlot;
import com.gregtechceu.gtceu.uipro.elements.NumberField;
import com.gregtechceu.gtceu.uipro.elements.IconToggle;
import com.gregtechceu.gtceu.uipro.data.SyncValue;
import com.gregtechceu.gtceu.uiwidgets.icon.WidgetIcons;
import com.gtocore.api.wireless.energy.WirelessGrid;
import com.gtohjs.client.renderer.AdaptiveNetFrequencyIcon;
import com.gregtechceu.gtceu.uiwidgets.display.MachineDisplay;
import com.gtocore.common.machine.multiblock.storage.WirelessEnergySubstationMachine;
import com.gto.datasynclib.annotations.SaveToDisk;
import com.gto.datasynclib.annotations.SyncToClient;
import com.hepdd.gtmthings.utils.TeamUtil;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/** Tower-only part. Physical template stacks set the specifications of hatches on its frequency. */
public final class AdaptiveNetTerminalPartMachine extends MultiblockPartMachine implements IMachineLife {
    @SaveToDisk @SyncToClient private long frequency;
    @SaveToDisk private final NotifiableInventory<AEItemKey> powerOutput;
    @SaveToDisk private final NotifiableInventory<AEItemKey> energyInput;
    @SaveToDisk private final NotifiableInventory<AEItemKey> laserSource;
    @SaveToDisk private final NotifiableInventory<AEItemKey> laserTarget;
    private boolean frequencyPage;
    private @Nullable TickableSubscription reservationSubscription;

    public AdaptiveNetTerminalPartMachine(MetaMachineBlockEntity holder) {
        super(holder);
        powerOutput = slot(AdaptiveTemplateRegistry.Family.POWER_OUTPUT);
        energyInput = slot(AdaptiveTemplateRegistry.Family.ENERGY_INPUT);
        laserSource = slot(AdaptiveTemplateRegistry.Family.LASER_SOURCE);
        laserTarget = slot(AdaptiveTemplateRegistry.Family.LASER_TARGET);
    }

    private NotifiableInventory<AEItemKey> slot(AdaptiveTemplateRegistry.Family family) {
        var storage = KeyInventory.items(1, 64, false);
        var inventory = NotifiableInventory.items(this, storage, IO.NONE, IO.NONE);
        inventory.setFilter(key -> key instanceof AEItemKey item &&
                AdaptiveTemplateRegistry.of(item.toStack(), family) != null);
        inventory.addChangedListener(this::changedTemplates);
        return inventory;
    }

    public long frequency() { return frequency; }

    public @Nullable WirelessEnergySubstationMachine tower() {
        for (IMultiController controller : getControllers()) {
            if (controller instanceof WirelessEnergySubstationMachine tower && tower.isFormed()) return tower;
        }
        return null;
    }

    public @Nullable UUID towerOwner() {
        var tower = tower();
        return tower == null ? null : tower.getOwnerUUID();
    }

    public @Nullable GlobalPos globalPos() {
        return getLevel() instanceof ServerLevel level ? GlobalPos.of(level.dimension(), getPos()) : null;
    }

    @Override public void addedToController(IMultiController controller) {
        super.addedToController(controller);
        if (controller instanceof WirelessEnergySubstationMachine) changedTemplates();
    }

    @Override public void removedFromController(IMultiController controller) {
        super.removedFromController(controller);
        if (controller instanceof WirelessEnergySubstationMachine) changedTemplates();
    }

    @Override public void onLoad() {
        super.onLoad();
        if (getLevel() instanceof ServerLevel level && frequency > 0) {
            var position = globalPos();
            if (position != null) FrequencyRegistry.get(level.getServer()).rebind(frequency, frequency,
                    new FrequencyRegistry.Entry(position, towerOwner()));
        }
        if (!isRemote()) reservationSubscription = subscribeServerTick(reservationSubscription, this::refreshReservation, 20);
    }

    @Override public void onUnload() {
        if (reservationSubscription != null) { reservationSubscription.unsubscribe(); reservationSubscription = null; }
        super.onUnload();
    }

    private void refreshReservation() {
        if (!(getLevel() instanceof ServerLevel level) || frequency <= 0) return;
        UUID owner = towerOwner();
        GlobalPos position = globalPos();
        if (owner == null || position == null) return;
        var registry = FrequencyRegistry.get(level.getServer());
        var saved = registry.reserved(frequency);
        if (saved != null && saved.terminal().equals(position) && !owner.equals(saved.owner()))
            registry.rebind(frequency, frequency, new FrequencyRegistry.Entry(position, owner));
    }

    @Override public void onMachineRemoved() {
        if (reservationSubscription != null) { reservationSubscription.unsubscribe(); reservationSubscription = null; }
        if (getLevel() instanceof ServerLevel level) {
            var position = globalPos();
            if (position != null) FrequencyRegistry.get(level.getServer()).release(frequency, position);
        }
        clearInventory(powerOutput.storage);
        clearInventory(energyInput.storage);
        clearInventory(laserSource.storage);
        clearInventory(laserTarget.storage);
    }

    public boolean setFrequency(Player player, long next) {
        if (!(getLevel() instanceof ServerLevel level) || next < 0 || !canConfigure(player)) return false;
        return changeFrequency(level, next, player);
    }

    private void setFrequencyFromUI(long next) {
        if (getLevel() instanceof ServerLevel level) changeFrequency(level, next, null);
    }

    private boolean changeFrequency(ServerLevel level, long next, @Nullable Player player) {
        var position = globalPos();
        if (position == null) return false;
        if (!FrequencyRegistry.get(level.getServer()).rebind(frequency, next,
                new FrequencyRegistry.Entry(position, towerOwner()))) {
            if (player != null) player.displayClientMessage(Component.translatable("gtocore.adaptive_net.frequency_taken"), true);
            return false;
        }
        frequency = next;
        onChanged();
        requestSync();
        return true;
    }

    public boolean canConfigure(Player player) {
        UUID owner = towerOwner();
        if (owner == null && getLevel() instanceof ServerLevel level) {
            var saved = FrequencyRegistry.get(level.getServer()).reserved(frequency);
            if (saved != null) owner = saved.owner();
        }
        return owner == null || owner.equals(player.getUUID()) ||
                TeamUtil.getTeamUUID(owner).equals(TeamUtil.getTeamUUID(player.getUUID()));
    }

    @Override public boolean shouldOpenUI(Player player, net.minecraft.world.InteractionHand hand,
                                           net.minecraft.world.phys.BlockHitResult hit) {
        return canConfigure(player);
    }

    public record Power(int tier, long voltage, long amperage, long euPerTick) {}

    public Power power(AdaptiveTemplateRegistry.Family family) {
        var inventory = switch (family) {
            case POWER_OUTPUT -> powerOutput;
            case ENERGY_INPUT -> energyInput;
            case LASER_SOURCE -> laserSource;
            case LASER_TARGET -> laserTarget;
        };
        AEItemKey key = inventory.storage.keyAt(0);
        long count = inventory.storage.amountAt(0);
        if (key == null || count <= 0) return new Power(0, 0, 0, 0);
        var spec = AdaptiveTemplateRegistry.of(key.toStack(), family);
        if (spec == null) return new Power(0, 0, 0, 0);
        long amps = Math.multiplyExact(spec.amperage(), count);
        return new Power(spec.tier(), spec.voltage(), amps, Math.multiplyExact(spec.voltage(), amps));
    }

    private void changedTemplates() {
        if (!isRemote()) { onChanged(); requestSync(); }
    }

    private void display(List<Component> lines) {
        lines.add(Component.translatable("gtocore.adaptive_net.frequency", frequency));
        lines.add(Component.translatable(tower() == null ? "gtocore.adaptive_net.no_tower" : "gtocore.adaptive_net.connected"));
        UUID owner = towerOwner();
        lines.add(Component.translatable("gtocore.adaptive_net.tower_rate", WirelessGrid.accountIfPresent(owner).rate()));
        for (var family : AdaptiveTemplateRegistry.Family.values()) {
            var spec = power(family);
            lines.add(Component.translatable("gtocore.adaptive_net.template_power",
                    Component.translatable("gtocore.adaptive_net.family." + family.name().toLowerCase(java.util.Locale.ROOT)),
                    spec.amperage(), spec.euPerTick()));
        }
    }

    @Override public Widget createUIWidget() {
        UIElement page = MachineDisplay.page(this, this::display);
        page.addChild(UIElement.row(LayoutStyle.AUTO).addChildren(
                IconToggle.of(WidgetIcons.ENERGY_ON, () -> !frequencyPage, on -> frequencyPage = false),
                IconToggle.of(AdaptiveNetFrequencyIcon.INSTANCE, () -> frequencyPage, on -> frequencyPage = true)));
        UIElement templates = UIElement.row(LayoutStyle.AUTO).addChildren(
                ItemSlot.of(powerOutput.storage, 0), ItemSlot.of(energyInput.storage, 0),
                ItemSlot.of(laserSource.storage, 0), ItemSlot.of(laserTarget.storage, 0));
        UIElement frequencyInput = NumberField.ofLong(LayoutStyle.AUTO, this::frequency, this::setFrequencyFromUI, 0, Long.MAX_VALUE);
        page.addChildren(templates, frequencyInput);
        templates.setDisplay(true);
        frequencyInput.setDisplay(false);
        page.addSyncValue(SyncValue.ofBool(() -> frequencyPage, false).onChanged(showFrequency -> {
            templates.setDisplay(!showFrequency);
            frequencyInput.setDisplay(showFrequency);
        }));
        return page;
    }
}
