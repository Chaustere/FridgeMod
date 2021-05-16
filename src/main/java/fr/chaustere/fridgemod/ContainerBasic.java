package fr.chaustere.fridgemod;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ContainerBasic extends Container
{
    public static ContainerBasic createContainerServerSide(int windowID, PlayerInventory playerInventory, FridgeContents fridgeContents) {
        return new ContainerBasic(windowID, playerInventory, fridgeContents);
    }

    public static ContainerBasic createContainerClientSide(int windowID, PlayerInventory playerInventory, net.minecraft.network.PacketBuffer extraData) {
        FridgeContents fridgeContents = FridgeContents.createForClientSideContainer(TileEntityFridge.NUMBER_OF_SLOTS);
        return new ContainerBasic(windowID, playerInventory, fridgeContents);
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;

    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = TileEntityFridge.NUMBER_OF_SLOTS;

    public static final int TILE_INVENTORY_YPOS = 20;
    public static final int PLAYER_INVENTORY_YPOS = 51;

    private ContainerBasic(int windowID, PlayerInventory playerInventory, FridgeContents fridgeContents) {
        super(StartupCommon.containerTypeContainerBasic, windowID);
        if (StartupCommon.containerTypeContainerBasic == null)
            throw new IllegalStateException("Must initialise containerBasicContainerType before constructing a ContainerBasic!");

        PlayerInvWrapper playerInventoryForge = new PlayerInvWrapper(playerInventory);

        this.fridgeContents = fridgeContents;

        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 109;
        // Add the players hotbar to the gui - the [xpos, ypos] location of each item
        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) {
            int slotNumber = x;
            addSlot(new SlotItemHandler(playerInventoryForge, slotNumber, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));
        }

        final int PLAYER_INVENTORY_XPOS = 8;
        // Add the rest of the player's inventory to the gui
        for (int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++) {
            for (int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++) {
                int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
                int xpos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
                addSlot(new SlotItemHandler(playerInventoryForge, slotNumber,  xpos, ypos));
            }
        }

        if (TE_INVENTORY_SLOT_COUNT != fridgeContents.getSizeInventory()) {
            LOGGER.warn("Mismatched slot count in ContainerBasic(" + TE_INVENTORY_SLOT_COUNT
                    + ") and TileInventory (" + fridgeContents.getSizeInventory()+")");
        }
        final int TILE_INVENTORY_XPOS = 8;
        // Add the tile inventory container to the gui
        for (int x = 0; x < TE_INVENTORY_SLOT_COUNT; x++) {
            int slotNumber = x;
            addSlot(new Slot(fridgeContents, slotNumber, TILE_INVENTORY_XPOS + SLOT_X_SPACING * x, TILE_INVENTORY_YPOS));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerEntity) {
        return fridgeContents.isUsableByPlayer(playerEntity);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerEntity, int sourceSlotIndex)
    {
        Slot sourceSlot = inventorySlots.get(sourceSlotIndex);
        if (sourceSlot == null || !sourceSlot.getHasStack()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getStack();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX && sourceSlotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!mergeItemStack(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false)){
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (sourceSlotIndex >= TE_INVENTORY_FIRST_SLOT_INDEX && sourceSlotIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!mergeItemStack(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            LOGGER.warn("Invalid slotIndex:" + sourceSlotIndex);
            return ItemStack.EMPTY;
        }

        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.putStack(ItemStack.EMPTY);
        } else {
            sourceSlot.onSlotChanged();
        }

        sourceSlot.onTake(playerEntity, sourceStack);
        return copyOfSourceStack;
    }

    private FridgeContents fridgeContents;
    private static final Logger LOGGER = LogManager.getLogger();
}
