package de.myxrcrs.corndoors.items;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;


public class PropertiedBlockItem<T> extends BlockItem {
    public PropertiedBlockItem(T property, Block block, Properties props){
        super(block, props);
        this.property = property;
    }
    public final T property;
    
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
