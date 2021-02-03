package de.myxrcrs.corndoors.init;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import de.myxrcrs.corndoors.CornDoors;
import de.myxrcrs.corndoors.blocks.DoorWindowType;
import de.myxrcrs.corndoors.blocks.GlassPanel.BeltType;
import de.myxrcrs.corndoors.items.PropertiedBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.RegistryObject;

public enum InitItems {

    NAIVE_PANEL(InitBlocks.NAIVE_PANEL),

    GLASS_PANEL_NONE(InitBlocks.GLASS_PANEL,BeltType.NONE),
    GLASS_PANEL_BLUE(InitBlocks.GLASS_PANEL,BeltType.BLUE),

    CORN_IRON_PANEL(InitBlocks.CORN_IRON_PANEL),

    NAIVE_DOOR_GLASS(InitBlocks.NAIVE_DOOR,DoorWindowType.GLASS),
    NAIVE_DOOR_FILM(InitBlocks.NAIVE_DOOR,DoorWindowType.FILM),
    NAIVE_DOOR_NONE(InitBlocks.NAIVE_DOOR,DoorWindowType.NONE),

    GLASS_DOOR(InitBlocks.GLASS_DOOR),

    D_NAIVE_DOOR_GLASS(InitBlocks.D_NAIVE_DOOR,DoorWindowType.GLASS),
    D_NAIVE_DOOR_FILM(InitBlocks.D_NAIVE_DOOR,DoorWindowType.FILM),
    D_NAIVE_DOOR_NONE(InitBlocks.D_NAIVE_DOOR,DoorWindowType.NONE),

    STRETCH_GATE(InitBlocks.STRETCH_GATE),

    CHANNEL_INNER_DOOR(InitBlocks.CHANNEL_INNER_DOOR_EDGE);

    public static final void initialize(){
        // You NEVER need to know how this method works.
        InitItems __tmp = NAIVE_DOOR_GLASS;
    }

    private static class MapHolder{

        public static final Map<String,Map<String,InitItems>> lookupMap = new HashMap<>();

        public static void add(String blockName,String propName,InitItems itemReg){
            Map<String,InitItems> map = lookupMap.get(blockName);
            if(map==null){
                lookupMap.put(blockName,map=new TreeMap<String,InitItems>());
            }
            map.put(propName,itemReg);
        }

        @Nullable
        public static InitItems get(String blockName,String propName){
            Map<String,InitItems> map = lookupMap.get(blockName);
            if(map==null)return null;
            else return map.get(propName);
        }
    }
    

    @Nullable
    public static Item find(Block block,IStringSerializable property){
        InitItems itemReg = MapHolder.get(block.getRegistryName().toString(), property.getName());
        return itemReg == null ? null : itemReg.get();
    }

    InitItems(InitBlocks blockReg){
        this(
            blockReg.reg.getId().getPath(),
            ()->new BlockItem(blockReg.get(),new Item.Properties().group(CornDoors.ITEM_GROUP))
        );
    }

    <T extends IStringSerializable>InitItems(InitBlocks blockReg,T property){
        this(
            blockReg.reg.getId().getPath()+'_'+property.getName(),
            ()->new PropertiedBlockItem<T>(property,blockReg.get(),new Item.Properties().group(CornDoors.ITEM_GROUP))
        );
        MapHolder.add(blockReg.reg.getId().toString(), property.getName(), this);
    }

    InitItems(String name,Supplier<? extends Item> sup){
        reg = CornDoors.ITEMS.register(name, sup);
    }

    public final RegistryObject<Item> reg;

    public Item get(){
        return reg.get();
    }
}