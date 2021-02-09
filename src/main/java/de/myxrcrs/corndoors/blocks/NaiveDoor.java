package de.myxrcrs.corndoors.blocks;

import de.myxrcrs.corndoors.init.InitItems;
import de.myxrcrs.corndoors.items.PropertiedBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class NaiveDoor extends AbstractDoor implements INaiveDoor {

    public static final Property<Integer> HORIZONTAL_POS = createHorizontalPosProperty(2);
    public static final Property<Integer> VERTICAL_POS = createVerticalPosProperty(4);
    
    public NaiveDoor(){
        super(Block.Properties.create(Material.WOOD).notSolid(),HORIZONTAL_POS,VERTICAL_POS,3,false);
        this.setDefaultState(this.getDefaultState()
            .with(HORIZONTAL_POS,0)
            .with(VERTICAL_POS,0)
            .with(WINDOW,DoorWindowType.GLASS));
    }

    @Override
    public void fillRange(World world, DoorRange range, BlockState stateTemplate, BlockItemUseContext context){
        DoorWindowType doorWindowType = ((PropertiedBlockItem<DoorWindowType>)context.getItem().getItem()).property;
        super.fillRange(world, range, stateTemplate.with(WINDOW, doorWindowType), context);
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return new ItemStack(InitItems.find(this,state.get(WINDOW)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HORIZONTAL_POS,VERTICAL_POS,WINDOW);
    }

}
