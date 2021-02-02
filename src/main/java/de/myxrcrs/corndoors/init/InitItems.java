package de.myxrcrs.corndoors.init;

import java.util.function.Supplier;

import de.myxrcrs.corndoors.CornDoors;
import de.myxrcrs.corndoors.blocks.DoorWindowType;
import de.myxrcrs.corndoors.items.NaiveDoorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;

public enum InitItems {

    NAIVE_PANEL(InitBlocks.NAIVE_PANEL),

    NAIVE_DOOR_GLASS("naive_door_glass",()->new NaiveDoorItem(DoorWindowType.GLASS,InitBlocks.NAIVE_DOOR.get(),new Item.Properties().group(CornDoors.ITEM_GROUP))),
    NAIVE_DOOR_FILM("naive_door_film",()->new NaiveDoorItem(DoorWindowType.FILM,InitBlocks.NAIVE_DOOR.get(),new Item.Properties().group(CornDoors.ITEM_GROUP))),
    NAIVE_DOOR_NONE("naive_door_none",()->new NaiveDoorItem(DoorWindowType.NONE,InitBlocks.NAIVE_DOOR.get(),new Item.Properties().group(CornDoors.ITEM_GROUP))),

    GLASS_DOOR(InitBlocks.GLASS_DOOR),

    D_NAIVE_DOOR_GLASS("d_naive_door_glass",()->new NaiveDoorItem(DoorWindowType.GLASS,InitBlocks.D_NAIVE_DOOR_EDGE.get(),new Item.Properties().group(CornDoors.ITEM_GROUP))),
    D_NAIVE_DOOR_FILM("d_naive_door_film",()->new NaiveDoorItem(DoorWindowType.FILM,InitBlocks.D_NAIVE_DOOR_EDGE.get(),new Item.Properties().group(CornDoors.ITEM_GROUP))),
    D_NAIVE_DOOR_NONE("d_naive_door_none",()->new NaiveDoorItem(DoorWindowType.NONE,InitBlocks.D_NAIVE_DOOR_EDGE.get(),new Item.Properties().group(CornDoors.ITEM_GROUP))),

    STRETCH_GATE(InitBlocks.STRETCH_GATE),

    CHANNEL_INNER_DOOR(InitBlocks.CHANNEL_INNER_DOOR);

    public static final void initialize(){
        // You NEVER need to know how this method works.
        InitItems __tmp = NAIVE_DOOR_GLASS;
    }

    InitItems(InitBlocks blockReg){
        this(
            blockReg.reg.getId().getPath(),
            ()->new BlockItem(blockReg.get(),new Item.Properties().group(CornDoors.ITEM_GROUP))
        );
    }

    InitItems(String name,Supplier<? extends Item> sup){
        reg = CornDoors.ITEMS.register(name, sup);
    }

    public final RegistryObject<Item> reg;

    public Item get(){
        return reg.get();
    }
}