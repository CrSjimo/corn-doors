package de.myxrcrs.corndoors.items;

import de.myxrcrs.corndoors.blocks.DoorWindowType;
import de.myxrcrs.corndoors.init.InitBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;


public class NaiveDoorItem extends BlockItem {
    public NaiveDoorItem(DoorWindowType doorWindowType, Block Door){
        super(Door, new Item.Properties());
        this.doorWindowType = doorWindowType;
    }
    public final DoorWindowType doorWindowType;
    
    @Override
    public String getTranslationKey() {
        return getDefaultTranslationKey();
    }
}
