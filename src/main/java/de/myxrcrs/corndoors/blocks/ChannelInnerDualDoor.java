package de.myxrcrs.corndoors.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;

public class ChannelInnerDualDoor extends AbstractDualDoorEdge {
    public static Property<Integer> VERTICAL_POS = createVerticalPosProperty(5);

    public ChannelInnerDualDoor(){
        super(Properties.create(Material.IRON), VERTICAL_POS, true, 3);
        this.setDefaultState(this.getDefaultState()
            .with(IS_OPENED, false)
            .with(FACING, Direction.NORTH)
            .with(VERTICAL_POS, 0)
            .with(PART, DualDoorEdgePart.ALL));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(IS_OPENED,FACING,PART,VERTICAL_POS);
    }
}
