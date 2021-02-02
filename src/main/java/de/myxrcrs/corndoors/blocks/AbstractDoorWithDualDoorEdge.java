package de.myxrcrs.corndoors.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public abstract class AbstractDoorWithDualDoorEdge extends AbstractDoor {

    public AbstractDoorWithDualDoorEdge(Properties props, Property<Integer> horizontalPosProp, Property<Integer> verticalPosProp, AbstractDualDoorEdge correspondingDualDoorEdgeBlock) {
        super(props, horizontalPosProp, verticalPosProp, correspondingDualDoorEdgeBlock);
    }

    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state){
        return correspondingDualDoorEdgeBlock.getItem(worldIn, pos, state);
    }
}
