package de.myxrcrs.corndoors.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public abstract class AbstractDoorWithDualDoorEdge extends AbstractDoor {
    public AbstractDoorWithDualDoorEdge(Properties props, boolean rotateWithinHinge,
            Property<Integer> horizontalPosProp, Property<Integer> verticalPosProp, double thickness) {
        super(props, rotateWithinHinge, horizontalPosProp, verticalPosProp, thickness);
    }

    public AbstractDoorWithDualDoorEdge(Properties props, boolean rotateWithinHinge,
            Property<Integer> horizontalPosProp, Property<Integer> verticalPosProp, double thickness,
            AbstractDualDoorEdge correspondingDualDoorEdgeBlock) {
        super(props, rotateWithinHinge, horizontalPosProp, verticalPosProp, thickness, correspondingDualDoorEdgeBlock);
    }

    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state){
        return correspondingDualDoorEdgeBlock.getItem(worldIn, pos, state);
    }
}
