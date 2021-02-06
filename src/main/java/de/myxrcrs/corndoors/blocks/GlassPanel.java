package de.myxrcrs.corndoors.blocks;

import de.myxrcrs.corndoors.init.InitItems;
import de.myxrcrs.corndoors.items.PropertiedBlockItem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class GlassPanel extends AbstractPanel {

    public enum BeltType implements IStringSerializable{
        NONE("none"),
        BLUE("blue");
        private final String name;
        BeltType(String name){
            this.name = name;
        }
        public String getName(){return name;}
        public String toString(){return name;}
    }

    public static final EnumProperty<BeltType> BELT_TYPE = EnumProperty.create("belt_type", BeltType.class);

    public GlassPanel(){
        super(Properties.create(Material.GLASS),3,true);
        this.setDefaultState(this.getDefaultState()
            .with(BELT_TYPE,BeltType.NONE));
    }

    @Override
    public BlockState onPlacedThenSetState(BlockItemUseContext context,BlockState stateTemplate){
        BeltType beltType = ((PropertiedBlockItem<BeltType>)context.getItem().getItem()).property;
        return super.onPlacedThenSetState(context, stateTemplate.with(BELT_TYPE, beltType));
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return new ItemStack(InitItems.find(this,state.get(BELT_TYPE)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(BELT_TYPE);
    }
}
