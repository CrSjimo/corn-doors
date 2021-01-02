package de.myxrcrs.corndoors.init;

import de.myxrcrs.corndoors.CornDoors;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitItems {
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, CornDoors.MOD_ID);
    public static final RegistryObject<BlockItem> NAIVE_DOOR = ITEMS.register("naive_door", ()->new BlockItem(InitBlocks.NAIVE_DOOR.get(), new Item.Properties()));
}