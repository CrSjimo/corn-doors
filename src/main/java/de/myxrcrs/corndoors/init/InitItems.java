package de.myxrcrs.corndoors.init;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import de.myxrcrs.corndoors.CornDoors;
import de.myxrcrs.corndoors.blocks.INaiveDoor.DoorWindowType;
import de.myxrcrs.corndoors.blocks.GlassPanel.BeltType;
import de.myxrcrs.corndoors.blocks.CornIronPanel.CornIronPanelType;
import de.myxrcrs.corndoors.items.PropertiedBlockItem;
import de.myxrcrs.corndoors.blocks.*;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.state.Property;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.RegistryObject;

public enum InitItems {

    NAIVE_PANEL(InitBlocks.NAIVE_PANEL),

    GLASS_PANEL(InitBlocks.GLASS_PANEL,GlassPanel.BELT_TYPE),

    CORN_IRON_PANEL(InitBlocks.CORN_IRON_PANEL,CornIronPanel.TYPE),

    NAIVE_DOOR(InitBlocks.NAIVE_DOOR,INaiveDoor.WINDOW),

    GLASS_DOOR(InitBlocks.GLASS_DOOR),

    D_NAIVE_DOOR_GLASS(InitBlocks.D_NAIVE_DOOR_EDGE,INaiveDoor.WINDOW),

    STRETCH_GATE(InitBlocks.STRETCH_GATE),

    CHANNEL_INNER_DOOR(InitBlocks.CHANNEL_INNER_DOOR_EDGE),

    CHANNEL_OUTER_DOOR(InitBlocks.CHANNEL_OUTER_DOOR_EDGE),
    
    IRON_4B_DOOR(InitBlocks.IRON_4B_DOOR_EDGE),
    
    IRON_5B_DOOR(InitBlocks.IRON_5B_DOOR_EDGE),;

    public static final void initialize(){
        // You NEVER need to know how this method works.
        InitItems __tmp = NAIVE_DOOR;
    }

    private static class MapHolder{

        public static final Map<String,InitItems> map = new TreeMap<>();

        public static void add(String blockName,InitItems itemReg){
            map.put(blockName,itemReg);
        }

        @Nullable
        public static InitItems get(String blockName){
            return map.get(blockName);
        }
    }
    

    @Nullable
    public static <T> Item find(Block block,T property){
        InitItems itemReg = MapHolder.get(block.getRegistryName().toString());
        return itemReg == null ? null : itemReg.get(property);
    }

    InitItems(InitBlocks blockReg){
        this(
            blockReg.reg.getId().getPath(),
            ()->new BlockItem(blockReg.get(),new Item.Properties().group(CornDoors.ITEM_GROUP))
        );
        MapHolder.add(blockReg.reg.getId().toString(), this);
    }

    <T extends Comparable<T>>InitItems(InitBlocks blockReg,Property<T> property){
        this(blockReg, property.getAllowedValues());
    }

    <T>InitItems(InitBlocks blockReg,Collection<T> values){
        Map<T,RegistryObject<Item>> map = new HashMap<>();
        for(T property:values){
            map.put(property,CornDoors.ITEMS.register(
                blockReg.reg.getId().getPath()+'_'+property.toString(),
                ()->new PropertiedBlockItem<T>(property,blockReg.get(),new Item.Properties().group(CornDoors.ITEM_GROUP))
            ));
        }
        regMap = map;
        MapHolder.add(blockReg.reg.getId().toString(), this);
        reg = null;
    }

    InitItems(String name,Supplier<? extends Item> sup){
        reg = CornDoors.ITEMS.register(name, sup);
        regMap = null;
    }

    public final RegistryObject<Item> reg;

    public final Map<?,RegistryObject<Item>> regMap;

    public Item get(){
        Objects.requireNonNull(reg,"Cannot get registered item of a propertied item registry");
        return reg.get();
    }

    public <T> Item get(T property){
        if(regMap==null)return get();
        return regMap.get(property).get();
    }
}