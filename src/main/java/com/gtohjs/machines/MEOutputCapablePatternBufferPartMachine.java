package com.gtohjs.machines;

import com.gtocore.common.machine.multiblock.part.ae.MEPatternBufferPartMachine;
import com.gtocore.common.machine.multiblock.part.ae.PatternBufferType;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.handler.IO;
import com.gregtechceu.gtceu.api.recipe.handler.IRecipeHandler;
import com.gregtechceu.gtceu.api.recipe.handler.PlanScratch;
import com.gregtechceu.gtceu.api.recipe.handler.RecipeHandlerUnit;
import com.gregtechceu.gtceu.integration.ae2.utils.KeyStorage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNodeListener;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.StorageHelper;
import com.gto.datasynclib.annotations.SaveToDisk;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds long-capacity item and fluid output buffers to GTO's pattern-buffer input
 * implementation. Outputs are inserted into the connected ME network immediately;
 * anything the network cannot currently accept remains persisted for retry.
 */
public abstract class MEOutputCapablePatternBufferPartMachine extends MEPatternBufferPartMachine
        implements MEPatternBufferOutputAccess {
    private static final String ITEM_OUTPUT_TAG = "gtohjsOutputItems";
    private static final String FLUID_OUTPUT_TAG = "gtohjsOutputFluids";

    @SaveToDisk
    private final KeyStorage gtohjsOutputItems = new KeyStorage();
    @SaveToDisk
    private final KeyStorage gtohjsOutputFluids = new KeyStorage();

    private final IRecipeHandler gtohjsOutputHandler;
    private final List<RecipeHandlerUnit> gtohjsRecipeHandlers;
    @Nullable
    private TickableSubscription gtohjsOutputSubscription;

    protected MEOutputCapablePatternBufferPartMachine(MetaMachineBlockEntity holder, PatternBufferType type) {
        super(holder, type);
        gtohjsOutputHandler = new OutputHandler(this);
        var handlers = new ArrayList<>(super.getRecipeHandlers());
        handlers.add(RecipeHandlerUnit.of(IO.OUT, this, List.of(gtohjsOutputHandler)));
        gtohjsRecipeHandlers = List.copyOf(handlers);
    }

    @Override
    public final List<RecipeHandlerUnit> getRecipeHandlers() {
        return gtohjsRecipeHandlers;
    }

    @Override
    public final IRecipeHandler gtohjs$getOutputHandler() {
        return gtohjsOutputHandler;
    }

    @Override
    public com.lowdragmc.lowdraglib.gui.widget.Widget createUIWidget() {
        int width = com.gtohjs.config.MEPatternBufferConfig.uiWidthFor(this) - 14;
        var root = com.gregtechceu.gtceu.uipro.UIElement.column(width)
                .layout(style -> style.gapAll(com.gregtechceu.gtceu.uipro.styletemplate.UISizes.SECTION_GAP));
        buildUI(root);
        return root;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        gtohjsFlushOutputs();
    }

    @Override
    public void onUnload() {
        gtohjsStopOutputSubscription();
        super.onUnload();
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        super.onMainNodeStateChanged(reason);
        gtohjsFlushOutputs();
    }

    @Override
    public void onMachineRemoved() {
        gtohjsFlushOutputs();
        super.onMachineRemoved();
    }

    @Override
    public void saveToItem(CompoundTag tag) {
        super.saveToItem(tag);
        tag.put(ITEM_OUTPUT_TAG, gtohjsWriteStorage(gtohjsOutputItems));
        tag.put(FLUID_OUTPUT_TAG, gtohjsWriteStorage(gtohjsOutputFluids));
    }

    /** Loads GTO's variable-length slot cache safely, then restores pending outputs. */
    @Override
    public void loadFromItem(CompoundTag tag) {
        Tag patternTag = tag.get("p");
        if (patternTag != null) {
            getPatternInventory().deserializeNBT(patternTag);
        }
        setCustomName(tag.getString("n"));

        ListTag savedSlots = tag.getList("i", Tag.TAG_COMPOUND);
        var slots = getInternalInventory();
        int slotCount = Math.min(slots.length, savedSlots.size());
        for (int index = 0; index < slotCount; index++) {
            slots[index].deserializeNBT(savedSlots.getCompound(index));
        }

        Tag sharedItems = tag.get("si");
        if (sharedItems != null) {
            shareInventory.storage.deserializeNBT(sharedItems);
        }
        Tag sharedFluids = tag.get("st");
        if (sharedFluids instanceof ListTag legacyTanks) {
            int tankCount = Math.min(shareTank.storage.size(), legacyTanks.size());
            for (int index = 0; index < tankCount; index++) {
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(legacyTanks.getCompound(index));
                if (!fluid.isEmpty()) shareTank.storage.set(index, AEFluidKey.of(fluid), fluid.getAmount());
            }
        } else if (sharedFluids != null) {
            shareTank.storage.deserializeNBT(sharedFluids);
        }
        Tag circuit = tag.get("ci");
        if (circuit != null) {
            circuitInventorySimulated.storage.deserializeNBT(circuit);
        }

        gtohjsReadStorage(tag.getList(ITEM_OUTPUT_TAG, Tag.TAG_COMPOUND), gtohjsOutputItems, AEItemKey.class);
        gtohjsReadStorage(tag.getList(FLUID_OUTPUT_TAG, Tag.TAG_COMPOUND), gtohjsOutputFluids, AEFluidKey.class);
    }

    private void gtohjsAcceptOutput(AEKey key, long amount, KeyStorage overflow) {
        if (key == null || amount < 1) {
            return;
        }
        overflow.lock.lock();
        try {
            overflow.storage.insert(key, amount);
            overflow.onChanged();
        } finally {
            overflow.lock.unlock();
        }
        // KeyStorage tracks its own sync state, but the block entity must also
        // be marked dirty so pending output is written to the world save.
        onChanged();
        gtohjsUpdateOutputSubscription();
    }

    private void gtohjsFlushOutputs() {
        if (isRemote()) {
            return;
        }
        IGrid grid = getMainNode().getGrid();
        if (grid != null) {
            gtohjsFlushStorage(grid, gtohjsOutputItems);
            gtohjsFlushStorage(grid, gtohjsOutputFluids);
        }
        gtohjsUpdateOutputSubscription();
    }

    private void gtohjsFlushStorage(IGrid grid, KeyStorage storage) {
        boolean changed = false;
        storage.lock.lock();
        try {
            for (var iterator = storage.iterator(); iterator.hasNext();) {
                var entry = iterator.next();
                AEKey key = entry.getKey();
                long amount = entry.getLongValue();
                if (key == null || amount < 1) {
                    iterator.remove();
                    changed = true;
                    continue;
                }
                long inserted = StorageHelper.poweredInsert(
                        grid.getEnergyService(),
                        grid.getStorageService().getInventory(),
                        key,
                        amount,
                        getActionSource());
                if (inserted > 0) {
                    changed = true;
                    if (inserted >= amount) {
                        iterator.remove();
                    } else {
                        entry.setValue(amount - inserted);
                    }
                }
            }
            if (changed) {
                storage.onChanged();
            }
        } finally {
            storage.lock.unlock();
        }
        if (changed) {
            onChanged();
        }
    }

    private void gtohjsUpdateOutputSubscription() {
        if (isRemote()) {
            return;
        }
        if (!gtohjsOutputItems.isEmpty() || !gtohjsOutputFluids.isEmpty()) {
            gtohjsOutputSubscription = subscribeServerTick(
                    gtohjsOutputSubscription,
                    this::gtohjsFlushOutputs,
                    20);
        } else {
            gtohjsStopOutputSubscription();
        }
    }

    private void gtohjsStopOutputSubscription() {
        if (gtohjsOutputSubscription != null) {
            gtohjsOutputSubscription.unsubscribe();
            gtohjsOutputSubscription = null;
        }
    }

    private static ListTag gtohjsWriteStorage(KeyStorage storage) {
        ListTag result = new ListTag();
        storage.lock.lock();
        try {
            for (var entry : storage) {
                if (entry.getKey() == null || entry.getLongValue() < 1) {
                    continue;
                }
                CompoundTag value = new CompoundTag();
                value.put("key", entry.getKey().toTagGeneric());
                value.putLong("amount", entry.getLongValue());
                result.add(value);
            }
        } finally {
            storage.lock.unlock();
        }
        return result;
    }

    private static void gtohjsReadStorage(ListTag values, KeyStorage storage, Class<? extends AEKey> keyType) {
        storage.lock.lock();
        try {
            storage.storage.clear();
            for (Tag value : values) {
                if (!(value instanceof CompoundTag entry)) {
                    continue;
                }
                AEKey key = AEKey.fromTagGeneric(entry.getCompound("key"));
                long amount = entry.getLong("amount");
                if (keyType.isInstance(key) && amount > 0) {
                    storage.storage.insert(key, amount);
                }
            }
            storage.onChanged();
        } finally {
            storage.lock.unlock();
        }
    }

    private static final class OutputHandler implements IRecipeHandler {
        private final MEOutputCapablePatternBufferPartMachine owner;

        private OutputHandler(MEOutputCapablePatternBufferPartMachine owner) {
            this.owner = owner;
        }

        @Override
        public boolean handlesItems() {
            return true;
        }

        @Override
        public boolean handlesFluids() {
            return true;
        }

        @Override
        public boolean isInfiniteCapacity(AEKeyType type) {
            return type == AEKeyType.items() || type == AEKeyType.fluids();
        }

        @Override
        public long reserveOutput(PlanScratch plan, int member, AEKeyType type, int entry, AEKey key, long amount) {
            return amount > 0 && ((type == AEKeyType.items() && key instanceof AEItemKey) ||
                    (type == AEKeyType.fluids() && key instanceof AEFluidKey)) ? amount : 0;
        }

        @Override
        public long insertOutput(AEKeyType type, AEKey key, long amount) {
            if (amount <= 0) return 0;
            if (type == AEKeyType.items() && key instanceof AEItemKey) {
                owner.gtohjsAcceptOutput(key, amount, owner.gtohjsOutputItems);
            } else if (type == AEKeyType.fluids() && key instanceof AEFluidKey) {
                owner.gtohjsAcceptOutput(key, amount, owner.gtohjsOutputFluids);
            } else {
                return 0;
            }
            return amount;
        }

        @Override
        public void onRecipeCommitted(GTRecipe recipe) {
            owner.gtohjsFlushOutputs();
        }
    }
}
