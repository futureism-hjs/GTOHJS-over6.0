package com.gtohjs.items;
import com.gtohjs.methods.RecipeDraftWriter;
import com.gtohjs.methods.RecipeSourceGenerator;
import com.gtohjs.methods.StructureDraftWriter;

import com.google.common.collect.Tables;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.WidgetUtils;
import com.gregtechceu.gtceu.api.gui.editor.EditableMachineUI;
import com.gregtechceu.gtceu.api.gui.editor.IEditableUI;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TabsWidget;
import com.gregtechceu.gtceu.api.gui.widget.PhantomSlotWidget;
import com.gregtechceu.gtceu.api.gui.widget.ScrollablePhantomFluidWidget;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.gui.widget.TankWidget;
import com.gregtechceu.gtceu.api.item.component.IItemUIFactory;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.recipe.handler.IO;
import com.gregtechceu.gtceu.api.recipe.info.ContentRecipeInfo;
import com.gregtechceu.gtceu.api.recipe.info.FluidRecipeInfo;
import com.gregtechceu.gtceu.api.recipe.info.ItemRecipeInfo;
import com.gregtechceu.gtceu.api.recipe.info.RecipeInfo;
import com.gregtechceu.gtceu.api.transfer.key.KeyInventory;
import com.gregtechceu.gtceu.api.recipe.ui.GTRecipeTypeUI;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.integration.ae2.gui.widget.AETextInputButtonWidget;
import com.gregtechceu.gtceu.integration.xei.widgets.GTRecipeWidget;
import com.gto.datasynclib.datastream.DataComponentMap;
import com.gtohjs.data.GTOHJSItems;
import com.gtolib.api.machine.DummyMachine;
import com.gtolib.utils.FluidUtils;
import com.gtolib.utils.ItemUtils;
import com.gtolib.utils.StringConverter;
import com.gtolib.utils.StringIndex;
import com.gtohjs.GTOHJS;
import com.gtohjs.methods.ModLog;
import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.DialogWidget;
import com.lowdragmc.lowdraglib.gui.widget.ProgressWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fluids.FluidStack;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

public final class GTOHJSRecipeEditorBehavior implements IItemUIFactory {
    public static final GTOHJSRecipeEditorBehavior INSTANCE = new GTOHJSRecipeEditorBehavior();
    private static final int SET_AMOUNT_ACTION_ID = 65_538;
    private static final int MIDDLE_MOUSE_BUTTON = 2;
    private static final int MAX_ITEM_AMOUNT = Byte.MAX_VALUE;
    private static final ResourceLocation CRAFTING_TABLE_ID =
            new ResourceLocation("minecraft", "crafting_table");
    private static final ResourceLocation CRAFTING_RECIPE_TYPE_ID =
            new ResourceLocation("minecraft", "crafting");

    private final Map<UUID, EditorSession> sessions = new ConcurrentHashMap<>();

    private GTOHJSRecipeEditorBehavior() {
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack itemStack, UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        MetaMachine metaMachine = MetaMachine.getMachine(context.getLevel(), context.getClickedPos());
        EditorSession session;
        if (metaMachine instanceof IRecipeLogicMachine) {
            session = new EditorSession(DummyMachine.createDummyMachine(metaMachine),
                    metaMachine.getDefinition().getId(), EditorMode.GT_MACHINE);
        } else if (context.getLevel().getBlockState(context.getClickedPos()).getBlock()
                instanceof CraftingTableBlock) {
            DummyMachine craftingGrid = DummyMachine.createDummyMachine(
                    BlockEntityType.CHEST,
                    context.getClickedPos(),
                    GTMachines.ASSEMBLER[1].defaultBlockState(),
                    GTRecipeTypes.ASSEMBLER_RECIPES);
            session = new EditorSession(craftingGrid, CRAFTING_TABLE_ID, EditorMode.CRAFTING_TABLE);
        } else {
            if (!context.getLevel().isClientSide) {
                player.sendSystemMessage(Component.translatable("gtohjs.recipe_editor.not_recipe_machine"));
            }
            return InteractionResult.PASS;
        }

        sessions.put(player.getUUID(), session);
        IItemUIFactory.super.use(context.getItemInHand().getItem(), context.getLevel(), player, context.getHand());
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Item item, Level level, Player player, InteractionHand usedHand) {
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder holder, Player player) {
        EditorSession session = sessions.get(player.getUUID());
        if (session == null) {
            throw new IllegalStateException("Recipe editor UI opened without a selected machine");
        }
        return new ModularUI(176, 166, holder, player)
                .widget(new FancyMachineUIWidget(new EditorUI(session, player), 176, 166));
    }

    private enum EditorMode {
        GT_MACHINE,
        CRAFTING_TABLE
    }

    private record EditorSession(DummyMachine machine, ResourceLocation targetId, EditorMode mode) {
        private boolean isGTMachine() {
            return mode == EditorMode.GT_MACHINE;
        }
    }

    private static final class EditorUI implements IFancyUIProvider {
        private final EditorSession session;
        private final Player player;

        private EditorUI(EditorSession session, Player player) {
            this.session = session;
            this.player = player;
        }

        @Override
        public Widget createMainPage(FancyMachineUIWidget widget) {
            DummyMachine machine = session.machine();
            GTRecipeTypeUI recipeUI = new GTRecipeTypeUI(machine.recipeType) {
                protected WidgetGroup addInventorySlotGroup(boolean isOutputs, boolean isSteam,
                                                             boolean isHighPressure,
                                                             BiPredicate<Boolean, RecipeInfo> predicate) {
                    int maxCount = 0;
                    int totalRows = 0;
                    TreeMap<RecipeInfo, Integer> slotsByCapability = new TreeMap<>(RecipeInfo.COMPARATOR);
                    Map<RecipeInfo, Integer> limits = isOutputs ? machine.recipeType.maxOutputs :
                            machine.recipeType.maxInputs;
                    for (var entry : limits.entrySet()) {
                        if (!entry.getKey().doRenderSlot) continue;
                        if (!session.isGTMachine() && entry.getKey() != ItemRecipeInfo.INSTANCE) continue;
                        int count = entry.getValue();
                        maxCount = Math.max(maxCount, Math.min(count, 3));
                        totalRows += (count + 2) / 3;
                        slotsByCapability.put(entry.getKey(), count);
                    }

                    WidgetGroup group = new WidgetGroup(0, 0, maxCount * 18 + 8, totalRows * 18 + 8);
                    int index = 0;
                    for (var entry : slotsByCapability.entrySet()) {
                        if (!(entry.getKey() instanceof ContentRecipeInfo capability) ||
                                capability.getWidgetClass() == null) {
                            continue;
                        }
                        boolean itemCapability = capability == ItemRecipeInfo.INSTANCE;
                        if (!session.isGTMachine() && !itemCapability) continue;
                        int capabilityCount = entry.getValue();
                        for (int slotIndex = 0; slotIndex < capabilityCount; slotIndex++) {
                            int tankIndex = slotIndex;
                            Widget slot = itemCapability ? new ServerSafePhantomSlotWidget() :
                                    new AmountEditablePhantomFluidWidget(
                                            () -> fluidStack(isOutputs ? machine.exportFluids : machine.importFluids, tankIndex),
                                            fluid -> setFluid(isOutputs ? machine.exportFluids : machine.importFluids,
                                                    tankIndex, fluid));
                            slot.setSelfPosition(new Position((index % 3) * 18 + 4, (index / 3) * 18 + 4));
                            slot.setBackground(getSlotOverlay(isOutputs, capability, slotIndex == capabilityCount - 1));
                            slot.setId(capability.slotName(isOutputs ? IO.OUT : IO.IN, slotIndex));
                            slot.appendHoverTooltips(Component.translatable(itemCapability ?
                                    "gtohjs.recipe_editor.amount.middle_click_item" :
                                    "gtohjs.recipe_editor.amount.middle_click_fluid"));
                            group.addWidget(slot);
                            index++;
                        }
                        index += (3 - (index % 3)) % 3;
                    }
                    return group;
                }

                @Override
                public IEditableUI<WidgetGroup, RecipeHolder> createEditableUITemplate(boolean isSteam,
                                                                                        boolean isHighPressure) {
                    return new IEditableUI.Normal<>(() -> {
                        WidgetGroup inputs = addInventorySlotGroup(false, isSteam, isHighPressure,
                                (ignored, info) -> true);
                        WidgetGroup outputs = addInventorySlotGroup(true, isSteam, isHighPressure,
                                (ignored, info) -> true);
                        int maxWidth = Math.max(inputs.getSize().width, outputs.getSize().width);
                        WidgetGroup group = new WidgetGroup(0, 0, 2 * maxWidth + 40,
                                Math.max(inputs.getSize().height, outputs.getSize().height));
                        var size = group.getSize();
                        inputs.addSelfPosition((maxWidth - inputs.getSize().width) / 2,
                                (size.height - inputs.getSize().height) / 2);
                        outputs.addSelfPosition(maxWidth + 40 + (maxWidth - outputs.getSize().width) / 2,
                                (size.height - outputs.getSize().height) / 2);
                        group.addWidget(inputs);
                        group.addWidget(outputs);
                        ProgressWidget progress = new ProgressWidget(ProgressWidget.JEIProgress,
                                maxWidth + 10, size.height / 2 - 10, 20, 20, getProgressBarTexture());
                        progress.setId("progress");
                        progress.setProgressTexture(getProgressBarTexture());
                        group.addWidget(progress);
                        return group;
                    }, (template, recipeHolder) -> {
                        for (var capabilityEntry : recipeHolder.storages().rowMap().entrySet()) {
                            IO io = capabilityEntry.getKey();
                            for (var storageEntry : capabilityEntry.getValue().entrySet()) {
                                if (!(storageEntry.getKey() instanceof ContentRecipeInfo capability)) continue;
                                Class<? extends Widget> widgetClass = capability.getWidgetClass();
                                if (widgetClass == null) continue;
                                Object storage = storageEntry.getValue();
                                WidgetUtils.widgetByIdForEach(template,
                                        "^%s_[0-9]+$".formatted(capability.slotName(io)), widgetClass, child -> {
                                            int index = WidgetUtils.widgetIdIndex(child);
                                            capability.applyWidgetInfo(child, index, false, io, recipeHolder,
                                                    machine.recipeType, null, null, -1, storage, 0, 0);
                                            if (child instanceof TankWidget tank) {
                                                tank.setAllowClickDrained(true).setAllowClickFilled(true);
                                            } else if (child instanceof SlotWidget slot) {
                                                slot.setCanTakeItems(true).setCanPutItems(true);
                                            }
                                        });
                            }
                        }
                    });
                }
            };

            EditableMachineUI editableUI = new EditableMachineUI("simple", GTOHJS.id("recipe_editor"), () -> {
                WidgetGroup original = recipeUI.createEditableUITemplate(false, false).createDefault();
                WidgetGroup group = new WidgetGroup(0, 0, original.getSize().width,
                        Math.max(original.getSize().height, 78));
                original.setSelfPosition(new Position(0,
                        (group.getSize().height - original.getSize().height) / 2));
                group.addWidget(original);
                return group;
            }, (template, ignoredMachine) -> {
                var storages = Tables.newCustomTable(new EnumMap<>(IO.class),
                        LinkedHashMap<RecipeInfo, Object>::new);
                storages.put(IO.IN, ItemRecipeInfo.INSTANCE, machine.importItems);
                storages.put(IO.OUT, ItemRecipeInfo.INSTANCE, machine.exportItems);
                if (session.isGTMachine()) {
                    storages.put(IO.IN, FluidRecipeInfo.INSTANCE, machine.importFluids);
                    storages.put(IO.OUT, FluidRecipeInfo.INSTANCE, machine.exportFluids);
                }
                recipeUI.createEditableUITemplate(false, false).setupUI(template,
                        new GTRecipeTypeUI.RecipeHolder(() -> 0, storages, new DataComponentMap(),
                                Collections.emptyList(), false, false));
            });

            WidgetGroup template = editableUI.createCustomUI();
            if (template == null) template = editableUI.createDefault();
            editableUI.setupUI(template, machine);

            int x = template.getSize().width - machine.recipeType.getRecipeUI().getSlotAreaOverflow(machine.recipeType.defaultDefinition) - 18;
            int y = template.getSize().height - 10;
            addTextInput(template, x - 48, y - 70, 76, machine.id, machine::setId,
                    "gtohjs.recipe_editor.field.id");
            if (session.isGTMachine()) {
                addTextInput(template, x - 36, y - 55, 64, String.valueOf(machine.circuit), machine::setCircuit,
                        "gtohjs.recipe_editor.field.circuit");
                addTextInput(template, x - 36, y - 40, 64, String.valueOf(machine.eut), machine::setEUt,
                        "gtohjs.recipe_editor.field.eut");
                addTextInput(template, x - 36, y - 25, 64, String.valueOf(machine.duration), machine::setDuration,
                        "gtohjs.recipe_editor.field.duration");
                addTextInput(template, x - 36, y - 10, 64, String.valueOf(machine.temp), machine::sett,
                        "gtohjs.recipe_editor.field.temperature");
                addTextInput(template, x - 36, y + 5, 64, String.valueOf(machine.manat), machine::setMANAt,
                        "gtohjs.recipe_editor.field.mana");
            }

            ButtonWidget save = new ButtonWidget(x, y, 16, 16,
                    new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("S")), click -> {
                        if (!click.isRemote) saveDraft(machine);
                    });
            save.setHoverTooltips(Component.translatable(session.isGTMachine() ?
                    "gtohjs.recipe_editor.save" : "gtohjs.recipe_editor.save_crafting"));
            template.addWidget(save);
            return template;
        }

        private void addTextInput(WidgetGroup template, int x, int y, int width, String value,
                                  java.util.function.Consumer<String> setter, String tooltipKey) {
            template.addWidget(new AETextInputButtonWidget(x, y, width, 12)
                    .setText(value)
                    .setOnConfirm(setter)
                    .setButtonTooltips(Component.translatable(tooltipKey)));
        }

        private void saveDraft(DummyMachine machine) {
            String recipeId = resolveRecipeId(machine);
            if (recipeId.isEmpty()) {
                player.sendSystemMessage(Component.translatable("gtohjs.recipe_editor.missing_id"));
                return;
            }
            if (!recipeId.matches("[a-z0-9/._-]+")) {
                player.sendSystemMessage(Component.translatable("gtohjs.recipe_editor.invalid_id", recipeId));
                return;
            }
            if (session.isGTMachine() && machine.duration <= 0) {
                player.sendSystemMessage(Component.translatable("gtohjs.recipe_editor.missing_duration"));
                return;
            }

            String source;
            ResourceLocation recipeTypeId;
            if (session.isGTMachine()) {
                source = buildMachineRecipeSource(machine, recipeId);
                recipeTypeId = machine.recipeType.registryName;
            } else {
                ItemStack outputStack = findCraftingOutput(machine);
                if (outputStack.isEmpty()) {
                    player.sendSystemMessage(Component.translatable(
                            "gtohjs.recipe_editor.missing_crafting_output"));
                    return;
                }
                if (!hasCraftingInput(machine)) {
                    player.sendSystemMessage(Component.translatable(
                            "gtohjs.recipe_editor.missing_crafting_input"));
                    return;
                }
                source = buildCraftingRecipeSource(machine, outputStack, recipeId);
                recipeTypeId = CRAFTING_RECIPE_TYPE_ID;
            }
            try {
                String className = generatedRecipeClassName(recipeId);
                Path output = RecipeDraftWriter.write(session.targetId(), recipeTypeId,
                        recipeId, className, source);
                player.sendSystemMessage(Component.translatable("gtohjs.recipe_editor.saved",
                        output.toAbsolutePath().toString()));
            } catch (IOException error) {
                ModLog.error("Failed to write recipe source draft", error);
                player.sendSystemMessage(Component.translatable("gtohjs.recipe_editor.save_failed",
                        error.getMessage()));
            }
        }

        private static String resolveRecipeId(DummyMachine machine) {
            String id = machine.id == null ? "" : machine.id.trim();
            if (!id.isEmpty()) return id;
            for (int index = 0; index < machine.exportItems.size(); index++) {
                ItemStack stack = itemStack(machine.exportItems, index);
                if (!stack.isEmpty()) return ItemUtils.getIdLocation(stack.getItem()).getPath();
            }
            for (int index = 0; index < machine.exportFluids.size(); index++) {
                FluidStack stack = fluidStack(machine.exportFluids, index);
                if (!stack.isEmpty()) return FluidUtils.getIdLocation(stack.getFluid()).getPath();
            }
            return "";
        }

        private static RecipeSourceGenerator.StackSpec itemSpec(ItemStack stack) {
            return stack == null || stack.isEmpty() ? null : new RecipeSourceGenerator.StackSpec(
                    ItemUtils.getIdLocation(stack.getItem()).toString(), stack.getCount(),
                    stack.getTag() == null ? "" : stack.getTag().toString());
        }

        private static ItemStack itemStack(KeyInventory<AEItemKey> inventory, int slot) {
            AEItemKey key = inventory.keyAt(slot);
            return key == null ? ItemStack.EMPTY : key.toStack((int) Math.min(inventory.amountAt(slot), Integer.MAX_VALUE));
        }

        private static FluidStack fluidStack(KeyInventory<AEFluidKey> inventory, int slot) {
            AEFluidKey key = inventory.keyAt(slot);
            return key == null ? FluidStack.EMPTY : key.toStack((int) Math.min(inventory.amountAt(slot), Integer.MAX_VALUE));
        }

        private static void setFluid(KeyInventory<AEFluidKey> inventory, int slot, FluidStack fluid) {
            inventory.set(slot, fluid == null || fluid.isEmpty() ? null : AEFluidKey.of(fluid),
                    fluid == null ? 0 : fluid.getAmount());
        }

        private static RecipeSourceGenerator.StackSpec[] itemSpecs(KeyInventory<AEItemKey> inventory) {
            ItemStack[] stacks = new ItemStack[inventory.size()];
            for (int i = 0; i < stacks.length; i++) stacks[i] = itemStack(inventory, i);
            return java.util.Arrays.stream(stacks).map(EditorUI::itemSpec)
                    .filter(java.util.Objects::nonNull).toArray(RecipeSourceGenerator.StackSpec[]::new);
        }

        private static RecipeSourceGenerator.StackSpec[] fluidSpecs(java.util.List<FluidStack> stacks) {
            return stacks.stream().filter(stack -> stack != null && !stack.isEmpty())
                    .map(stack -> new RecipeSourceGenerator.StackSpec(
                            FluidUtils.getIdLocation(stack.getFluid()).toString(), stack.getAmount(),
                            stack.getTag() == null ? "" : stack.getTag().toString()))
                    .toArray(RecipeSourceGenerator.StackSpec[]::new);
        }

        private static String buildMachineRecipeSource(DummyMachine machine, String recipeId) {
            java.util.List<FluidStack> inputs = new java.util.ArrayList<>();
            java.util.List<FluidStack> outputs = new java.util.ArrayList<>();
            for (int i = 0; i < machine.importFluids.size(); i++) inputs.add(fluidStack(machine.importFluids, i));
            for (int i = 0; i < machine.exportFluids.size(); i++) outputs.add(fluidStack(machine.exportFluids, i));
            return RecipeSourceGenerator.gt(machine.recipeType.registryName.toString(), recipeId,
                    itemSpecs(machine.importItems), itemSpecs(machine.exportItems),
                    fluidSpecs(inputs), fluidSpecs(outputs), machine.eut, machine.duration,
                    machine.circuit, machine.temp, machine.manat);
        }

        private static String generatedRecipeClassName(String recipeId) {
            return RecipeSourceGenerator.className(recipeId);
        }

        private static ItemStack findCraftingOutput(DummyMachine machine) {
            for (int index = 0; index < machine.exportItems.size(); index++) {
                ItemStack stack = itemStack(machine.exportItems, index);
                if (!stack.isEmpty()) return stack;
            }
            return ItemStack.EMPTY;
        }

        private static boolean hasCraftingInput(DummyMachine machine) {
            int slotCount = Math.min(9, machine.importItems.size());
            for (int index = 0; index < slotCount; index++) {
                if (!itemStack(machine.importItems, index).isEmpty()) return true;
            }
            return false;
        }

        private static String buildCraftingRecipeSource(DummyMachine machine, ItemStack outputStack,
                                                        String recipeId) {
            RecipeSourceGenerator.StackSpec[] grid = new RecipeSourceGenerator.StackSpec[9];
            for (int i = 0; i < Math.min(9, machine.importItems.size()); i++) grid[i] = itemSpec(itemStack(machine.importItems, i));
            return RecipeSourceGenerator.crafting(recipeId, itemSpec(outputStack), grid);
        }

        @Override
        public void attachSideTabs(TabsWidget sideTabs) {
            sideTabs.setMainTab(this);
        }

        @Override
        public IGuiTexture getTabIcon() {
            return new ItemStackTexture(GTOHJSItems.RECIPE_EDITOR.get());
        }

        @Override
        public Component getTitle() {
            return Component.translatable(session.isGTMachine() ?
                    "item.gtohjs.recipe_editor" : "gtohjs.recipe_editor.title_crafting");
        }
    }

    private static void showAmountEditor(Widget source, String titleKey, int currentAmount, int maxAmount,
                                         IntConsumer onConfirm) {
        if (source.getGui() == null) return;

        DialogWidget dialog = new DialogWidget(source.getGui().mainGroup, true);
        WidgetGroup content = DialogWidget.createContainer(dialog, 164, 82, titleKey);
        TextFieldWidget amountField = DialogWidget.createTextField(content, 8, 12, 148, 16)
                .setCurrentString(currentAmount);
        amountField.setValidator(value -> isAmountInput(value, maxAmount) ?
                value : amountField.getCurrentString());
        DialogWidget.createButton(content, 12, 45, 64, 15, "ldlib.gui.tips.confirm", () -> {
            String value = amountField.getCurrentString();
            if (!isValidAmount(value, maxAmount)) return;
            dialog.close();
            onConfirm.accept(Integer.parseInt(value));
        });
        DialogWidget.createButton(content, 88, 45, 64, 15, "ldlib.gui.tips.cancel", dialog::close);
        amountField.setFocus(true);
    }

    private static boolean isAmountInput(String value, int maxAmount) {
        return value != null && (value.isEmpty() || isValidAmount(value, maxAmount));
    }

    private static boolean isValidAmount(String value, int maxAmount) {
        if (value == null || value.isEmpty() || value.length() > 10) return false;
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (character < '0' || character > '9') return false;
        }
        try {
            int amount = Integer.parseInt(value);
            return amount > 0 && amount <= maxAmount;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private static final class ServerSafePhantomSlotWidget extends PhantomSlotWidget {
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == MIDDLE_MOUSE_BUTTON && isRemote() && isMouseOverElement(mouseX, mouseY) &&
                    slotReference != null && !slotReference.getItem().isEmpty()) {
                showAmountEditor(this, "gtohjs.recipe_editor.amount.item", slotReference.getItem().getCount(),
                        MAX_ITEM_AMOUNT,
                        amount -> {
                            setAmount(amount);
                            writeClientAction(SET_AMOUNT_ACTION_ID, buffer -> buffer.writeVarInt(amount));
                        });
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void handleClientAction(int id, net.minecraft.network.FriendlyByteBuf buffer) {
            if (id == SET_AMOUNT_ACTION_ID) {
                int amount = buffer.readVarInt();
                if (amount > 0 && amount <= MAX_ITEM_AMOUNT) setAmount(amount);
                return;
            }
            super.handleClientAction(id, buffer);
        }

        private void setAmount(int amount) {
            if (slotReference == null) return;
            ItemStack current = slotReference.getItem();
            if (current.isEmpty()) return;
            ItemStack updated = current.copy();
            updated.setCount(amount);
            slotReference.set(updated);
        }

        @Override
        public ItemStack slotClickPhantom(Slot slot, int mouseButton, ClickType clickType, ItemStack heldStack) {
            if (isRemote()) return slot.getItem();
            return super.slotClickPhantom(slot, mouseButton, clickType, heldStack);
        }
    }

    private static final class AmountEditablePhantomFluidWidget extends ScrollablePhantomFluidWidget {
        private final Supplier<FluidStack> fluidGetter;
        private final Consumer<FluidStack> fluidSetter;

        private AmountEditablePhantomFluidWidget(Supplier<FluidStack> fluidGetter,
                                                 Consumer<FluidStack> fluidSetter) {
            super(null, 0, 0, 0, 18, 18, fluidGetter, fluidSetter);
            this.fluidGetter = fluidGetter;
            this.fluidSetter = fluidSetter;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            FluidStack current = fluidGetter.get();
            if (button == MIDDLE_MOUSE_BUTTON && isRemote() && isMouseOverElement(mouseX, mouseY) &&
                    current != null && !current.isEmpty()) {
                showAmountEditor(this, "gtohjs.recipe_editor.amount.fluid", current.getAmount(), Integer.MAX_VALUE,
                        amount -> {
                            setAmount(amount);
                            writeClientAction(SET_AMOUNT_ACTION_ID, buffer -> buffer.writeVarInt(amount));
                        });
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void handleClientAction(int id, net.minecraft.network.FriendlyByteBuf buffer) {
            if (id == SET_AMOUNT_ACTION_ID) {
                int amount = buffer.readVarInt();
                if (amount > 0) setAmount(amount);
                return;
            }
            super.handleClientAction(id, buffer);
        }

        private void setAmount(int amount) {
            FluidStack current = fluidGetter.get();
            if (current == null || current.isEmpty()) return;
            FluidStack updated = current.copy();
            updated.setAmount(amount);
            fluidSetter.accept(updated);
        }
    }
}
