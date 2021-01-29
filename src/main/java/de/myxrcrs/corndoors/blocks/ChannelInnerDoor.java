package de.myxrcrs.corndoors.blocks;

import de.myxrcrs.corndoors.init.InitBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.Direction;

public class ChannelInnerDoor extends AbstractDoorWithDualDoorEdge {
    
    public static Property<Integer> HORIZONTAL_POS = createHorizontalPosProperty(2);
    public static Property<Integer> VERTICAL_POS = createVerticalPosProperty(5);

    public ChannelInnerDoor(){
        super(Properties.create(Material.IRON), true, HORIZONTAL_POS, VERTICAL_POS, 3, (AbstractDualDoorEdge) InitBlocks.CHANNEL_INNER_DOOR_EDGE.get());
        this.setDefaultState(this.getDefaultState()
            .with(IS_OPENED, false)
            .with(FACING, Direction.NORTH)
            .with(HORIZONTAL_POS,0)
            .with(VERTICAL_POS,0)
            .with(HINGE,DoorHingeSide.LEFT));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(IS_OPENED,FACING,VERTICAL_POS,HORIZONTAL_POS,HINGE);
    }

}
