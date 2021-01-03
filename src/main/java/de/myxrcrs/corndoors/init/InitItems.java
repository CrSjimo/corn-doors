package de.myxrcrs.corndoors.init;

import de.myxrcrs.corndoors.CornDoors;
import de.myxrcrs.corndoors.blocks.DoorWindowType;
import de.myxrcrs.corndoors.items.NaiveDoorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitItems {
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, CornDoors.MOD_ID);
    public static final RegistryObject<BlockItem> NAIVE_DOOR_GLASS = ITEMS.register("naive_door_glass",()->new NaiveDoorItem(DoorWindowType.GLASS));
    public static final RegistryObject<BlockItem> NAIVE_DOOR_FILM = ITEMS.register("naive_door_film",()->new NaiveDoorItem(DoorWindowType.FILM));
    public static final RegistryObject<BlockItem> NAIVE_DOOR_NONE = ITEMS.register("naive_door_none",()->new NaiveDoorItem(DoorWindowType.NONE));
}