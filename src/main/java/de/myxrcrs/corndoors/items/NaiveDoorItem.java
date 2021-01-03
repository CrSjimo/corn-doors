package de.myxrcrs.corndoors.items;

import de.myxrcrs.corndoors.blocks.DoorWindowType;
import de.myxrcrs.corndoors.init.InitBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;


public class NaiveDoorItem extends BlockItem {
    public NaiveDoorItem(DoorWindowType doorWindowType){
        super(InitBlocks.NAIVE_DOOR.get(), new Item.Properties());
        this.doorWindowType = doorWindowType;
    }
    public final DoorWindowType doorWindowType;
    
    @Override
    public String getTranslationKey() {
        return getDefaultTranslationKey();
    }
}
