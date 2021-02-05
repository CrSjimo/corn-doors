package de.myxrcrs.corndoors.blocks;

import de.myxrcrs.corndoors.init.InitBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

public class ChannelOuterDoor extends AbstractDoorWithDualDoorEdge {

    public static Property<Integer> HORIZONTAL_POS = createHorizontalPosProperty(2);
    public static Property<Integer> VERTICAL_POS = createVerticalPosProperty(5);

    public ChannelOuterDoor() {
        super(Properties.create(Material.IRON), HORIZONTAL_POS, VERTICAL_POS, (AbstractDualDoorEdge) InitBlocks.CHANNEL_OUTER_DOOR_EDGE.get());
        this.setDefaultState(this.getDefaultState()
            .with(HORIZONTAL_POS,0)
            .with(VERTICAL_POS,0));
    }

    @Override
    public VoxelShape generateBoundaryBox(BlockState state){
        if(!state.get(IS_OPENED))return super.generateBoundaryBox(state);
        Direction facing = state.get(FACING);
        switch(facing){
            case NORTH:
            default:
                return Block.makeCuboidShape(2.5, 0, 0, 16, 16, 3);
            case SOUTH:
                return Block.makeCuboidShape(0, 0, 13, 16, 16, 13.5);
            case EAST:
                return Block.makeCuboidShape(13, 0, 0, 13.5, 16, 16);
            case WEST:
                return Block.makeCuboidShape(2.5, 0, 0, 3, 16, 16);
        }
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HORIZONTAL_POS,VERTICAL_POS);
    }
}
