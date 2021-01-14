package de.myxrcrs.corndoors.init;

import de.myxrcrs.corndoors.CornDoors;
import de.myxrcrs.corndoors.blocks.DoorWindowType;
import de.myxrcrs.corndoors.items.NaiveDoorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitItems {
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, CornDoors.MOD_ID);
    public static final RegistryObject<BlockItem> NAIVE_DOOR_GLASS = ITEMS.register("naive_door_glass",()->new NaiveDoorItem(DoorWindowType.GLASS,InitBlocks.NAIVE_DOOR.get()));
    public static final RegistryObject<BlockItem> NAIVE_DOOR_FILM = ITEMS.register("naive_door_film",()->new NaiveDoorItem(DoorWindowType.FILM,InitBlocks.NAIVE_DOOR.get()));
    public static final RegistryObject<BlockItem> NAIVE_DOOR_NONE = ITEMS.register("naive_door_none",()->new NaiveDoorItem(DoorWindowType.NONE,InitBlocks.NAIVE_DOOR.get()));

    public static final RegistryObject<BlockItem> GLASS_DOOR = ITEMS.register("glass_door",()->new BlockItem(InitBlocks.GLASS_DOOR.get(), new Properties()));

    public static final RegistryObject<BlockItem> D_NAIVE_DOOR_GLASS = ITEMS.register("d_naive_door_glass",()->new NaiveDoorItem(DoorWindowType.GLASS,InitBlocks.D_NAIVE_DOOR.get()));
    public static final RegistryObject<BlockItem> D_NAIVE_DOOR_FILM = ITEMS.register("d_naive_door_film",()->new NaiveDoorItem(DoorWindowType.FILM,InitBlocks.D_NAIVE_DOOR.get()));
    public static final RegistryObject<BlockItem> D_NAIVE_DOOR_NONE = ITEMS.register("d_naive_door_none",()->new NaiveDoorItem(DoorWindowType.NONE,InitBlocks.D_NAIVE_DOOR.get()));
}