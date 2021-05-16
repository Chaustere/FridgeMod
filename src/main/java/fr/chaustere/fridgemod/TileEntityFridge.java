package fr.chaustere.fridgemod;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TileEntityFridge extends TileEntity implements INamedContainerProvider
{
    public static final int NUMBER_OF_SLOTS = 9;

    public TileEntityFridge() {
        super(StartupCommon.tileEntityTypeFridge);
        fridgeContents = FridgeContents.createForTileEntity(NUMBER_OF_SLOTS,
                this::canPlayerAccessInventory,
                this::markDirty);
    }

    public boolean canPlayerAccessInventory(PlayerEntity player) {
        if (this.world.getTileEntity(this.pos) != this) return false;
        final double X_CENTRE_OFFSET = 0.5;
        final double Y_CENTRE_OFFSET = 0.5;
        final double Z_CENTRE_OFFSET = 0.5;
        final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;
        return player.getDistanceSq(pos.getX() + X_CENTRE_OFFSET, pos.getY() + Y_CENTRE_OFFSET, pos.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
    }

    public static final String FRIDGECONTENTS_INVENTORY_TAB = "contents";

    public CompoundNBT write(CompoundNBT parentNBTTagCompound)
    {
        super.write(parentNBTTagCompound);
        CompoundNBT inventoryNBT = fridgeContents.serializeNBT();
        parentNBTTagCompound.put(FRIDGECONTENTS_INVENTORY_TAB, inventoryNBT);
        return parentNBTTagCompound;
    }

    @Override
    public void read(BlockState blockState, CompoundNBT parentNBTTagCompound)
    {
        super.read(blockState, parentNBTTagCompound); // The super call is required to save and load the tiles location
        CompoundNBT inventoryNBT = parentNBTTagCompound.getCompound(FRIDGECONTENTS_INVENTORY_TAB);
        fridgeContents.deserializeNBT(inventoryNBT);
        if (fridgeContents.getSizeInventory() != NUMBER_OF_SLOTS)
            throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match expected.");
    }

    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        write(nbtTagCompound);
        int tileEntityType = 42;
        return new SUpdateTileEntityPacket(this.pos, tileEntityType, nbtTagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        BlockState blockState = world.getBlockState(pos);
        read(blockState, pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        write(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(BlockState blockState, CompoundNBT tag)
    {
        this.read(blockState, tag);
    }

    public void dropAllContents(World world, BlockPos blockPos) {
        InventoryHelper.dropInventoryItems(world, blockPos, fridgeContents);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.fridgemod.fridge_block");
    }

    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return ContainerBasic.createContainerServerSide(windowID, playerInventory, fridgeContents);
    }

    private final FridgeContents fridgeContents;
}
