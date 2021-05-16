package fr.chaustere.fridgemod;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Predicate;

public class FridgeContents implements IInventory
{
    public static FridgeContents createForTileEntity(int size,
                                 Predicate<PlayerEntity> canPlayerAccessInventoryLambda,
                                 Notify markDirtyNotificationLambda) {
        return new FridgeContents(size, canPlayerAccessInventoryLambda, markDirtyNotificationLambda);
    }

    public static FridgeContents createForClientSideContainer(int size) {
        return new FridgeContents(size);
    }

    public CompoundNBT serializeNBT()  {
        return fridgeContents.serializeNBT();
    }

    public void deserializeNBT(CompoundNBT nbt)   {
        fridgeContents.deserializeNBT(nbt);
    }

    public void setCanPlayerAccessInventoryLambda(Predicate<PlayerEntity> canPlayerAccessInventoryLambda) {
        this.canPlayerAccessInventoryLambda = canPlayerAccessInventoryLambda;
    }

    public void setMarkDirtyNotificationLambda(Notify markDirtyNotificationLambda) {
        this.markDirtyNotificationLambda = markDirtyNotificationLambda;
    }

    public void setOpenInventoryNotificationLambda(Notify openInventoryNotificationLambda) {
        this.openInventoryNotificationLambda = openInventoryNotificationLambda;
    }

    public void setCloseInventoryNotificationLambda(Notify closeInventoryNotificationLambda) {
        this.closeInventoryNotificationLambda = closeInventoryNotificationLambda;
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return canPlayerAccessInventoryLambda.test(player);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return fridgeContents.isItemValid(index, stack);
    }

    @FunctionalInterface
    public interface Notify {
        void invoke();
    }

    @Override
    public void markDirty() {
        markDirtyNotificationLambda.invoke();
    }

    @Override
    public void openInventory(PlayerEntity player) {
        openInventoryNotificationLambda.invoke();
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        closeInventoryNotificationLambda.invoke();
    }

    @Override
    public int getSizeInventory() {
        return fridgeContents.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < fridgeContents.getSlots(); ++i) {
            if (!fridgeContents.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return fridgeContents.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return fridgeContents.extractItem(index, count, false);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        int maxPossibleItemStackSize = fridgeContents.getSlotLimit(index);
        return fridgeContents.extractItem(index, maxPossibleItemStackSize, false);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        fridgeContents.setStackInSlot(index, stack);
    }

    @Override
    public void clear() {
        for (int i = 0; i < fridgeContents.getSlots(); ++i) {
            fridgeContents.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    private FridgeContents(int size) {
        this.fridgeContents = new ItemStackHandler(size);
    }
    private FridgeContents(int size, Predicate<PlayerEntity> canPlayerAccessInventoryLambda, Notify markDirtyNotificationLambda) {
        this.fridgeContents = new ItemStackHandler(size);
        this.canPlayerAccessInventoryLambda = canPlayerAccessInventoryLambda;
        this.markDirtyNotificationLambda = markDirtyNotificationLambda;
    }

    private Predicate<PlayerEntity> canPlayerAccessInventoryLambda = x-> true;
    private Notify markDirtyNotificationLambda = ()->{};
    private Notify openInventoryNotificationLambda = ()->{};
    private Notify closeInventoryNotificationLambda = ()->{};

    private final ItemStackHandler fridgeContents;
}
