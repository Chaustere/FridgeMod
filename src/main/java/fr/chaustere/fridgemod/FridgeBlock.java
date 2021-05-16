package fr.chaustere.fridgemod;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class FridgeBlock extends ContainerBlock
{
    public FridgeBlock()
    {
        super(AbstractBlock.Properties.create(Material.IRON));
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return createNewTileEntity(world);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityFridge();
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (worldIn.isRemote) return ActionResultType.SUCCESS;
        INamedContainerProvider namedContainerProvider = this.getContainer(state, worldIn, pos);
        if (namedContainerProvider != null) {
            if (!(player instanceof ServerPlayerEntity)) return ActionResultType.FAIL;  // should always be true, but just in case...
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
            NetworkHooks.openGui(serverPlayerEntity, namedContainerProvider, (packetBuffer)->{});
        }
        return ActionResultType.SUCCESS;
    }
    public void onReplaced(BlockState state, World world, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = world.getTileEntity(blockPos);
            if (tileentity instanceof TileEntityFridge) {
                TileEntityFridge tileEntityFridge = (TileEntityFridge) tileentity;
                tileEntityFridge.dropAllContents(world, blockPos);
            }
            super.onReplaced(state, world, blockPos, newState, isMoving);
        }
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return false;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public BlockRenderType getRenderType(BlockState iBlockState) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return CHEST_SHAPE;
    }

    private static final Vector3d CHEST_MIN_CORNER = new Vector3d(1.0, 0.0, 1.0);
    private static final Vector3d CHEST_MAX_CORNER = new Vector3d(15.0, 16.0, 15.0);
    private static final VoxelShape CHEST_SHAPE = Block.makeCuboidShape(
            CHEST_MIN_CORNER.getX(), CHEST_MIN_CORNER.getY(), CHEST_MIN_CORNER.getZ(),
            CHEST_MAX_CORNER.getX(), CHEST_MAX_CORNER.getY(), CHEST_MAX_CORNER.getZ());
}


