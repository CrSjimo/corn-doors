package de.myxrcrs.corndoors.items;

import de.myxrcrs.corndoors.blocks.DoorWindowType;
import de.myxrcrs.corndoors.init.InitBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;


public class NaiveDoorItem extends BlockItem {
    public NaiveDoorItem(DoorWindowType doorWindowType, Block Door, Properties props){
        super(Door, props);
        this.doorWindowType = doorWindowType;
    }
    public final DoorWindowType doorWindowType;
    
    @Override
    public String getTranslationKey() {
        return getDefaultTranslationKey();
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
           items.add(new ItemStack(this));
        }
    }
}
