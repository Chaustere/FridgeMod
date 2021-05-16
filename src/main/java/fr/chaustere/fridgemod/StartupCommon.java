package fr.chaustere.fridgemod;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class StartupCommon
{
    public static Block fridgeBlock;
    public static BlockItem itemFridgeBlock;

    public static TileEntityType<TileEntityFridge> tileEntityTypeFridge;
    public static ContainerType<ContainerBasic> containerTypeContainerBasic;

    @SubscribeEvent
    public static void onBlocksRegistration(final RegistryEvent.Register<Block> blockRegisterEvent) {
        fridgeBlock = new FridgeBlock().setRegistryName("fridge_block");
        blockRegisterEvent.getRegistry().register(fridgeBlock);
    }

    @SubscribeEvent
    public static void onItemsRegistration(final RegistryEvent.Register<Item> itemRegisterEvent) {
        final int MAXIMUM_STACK_SIZE = 1;  // player can only hold 1 of this block in their hand at once

        Item.Properties itemSimpleProperties = new Item.Properties()
                .maxStackSize(MAXIMUM_STACK_SIZE)
                .group(ItemGroup.BUILDING_BLOCKS);  // which inventory tab?
        itemFridgeBlock = new BlockItem(fridgeBlock, itemSimpleProperties);
        itemFridgeBlock.setRegistryName(fridgeBlock.getRegistryName());
        itemRegisterEvent.getRegistry().register(itemFridgeBlock);
    }

    @SubscribeEvent
    public static void onTileEntityTypeRegistration(final RegistryEvent.Register<TileEntityType<?>> event) {
        tileEntityTypeFridge = TileEntityType.Builder.create(TileEntityFridge::new, fridgeBlock)
                .build(null);

        tileEntityTypeFridge.setRegistryName("fridgemod:fridge_block");
        event.getRegistry().register(tileEntityTypeFridge);
    }

    @SubscribeEvent
    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event)
    {
        containerTypeContainerBasic = IForgeContainerType.create(ContainerBasic::createContainerClientSide);
        containerTypeContainerBasic.setRegistryName("fridge_block");
        event.getRegistry().register(containerTypeContainerBasic);
    }

}
