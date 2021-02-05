package de.myxrcrs.corndoors.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

public class ChannelOuterDualDoor extends AbstractDualDoorEdge {
    public static Property<Integer> VERTICAL_POS = createVerticalPosProperty(5);

    public ChannelOuterDualDoor() {
        super(Properties.create(Material.IRON), VERTICAL_POS, 3, false);
        this.setDefaultState(this.getDefaultState()
            .with(VERTICAL_POS, 0));
    }

    @Override
    public VoxelShape generateBoundaryBox(BlockState state){
        Direction facing = state.get(FACING);
        if(state.get(PART)==DualDoorEdgePart.LEFT){
            switch(facing){
                case NORTH:
                default:
                    return Block.makeCuboidShape(8, 0, 2.5, 16, 16, 3);
                case SOUTH:
                    return Block.makeCuboidShape(0, 0, 13, 8, 16, 13.5);
                case EAST:
                    return Block.makeCuboidShape(13, 0, 8, 13.5, 16, 16);
                case WEST:
                    return Block.makeCuboidShape(2.5, 0, 0, 3, 16, 8);
            }
        }else if(state.get(PART)==DualDoorEdgePart.RIGHT){
            switch(facing){
                case NORTH:
                default:
                    return Block.makeCuboidShape(0, 0, 2.5, 8, 16, 3);
                case SOUTH:
                    return Block.makeCuboidShape(8, 0, 13, 16, 16, 13.5);
                case EAST:
                    return Block.makeCuboidShape(13, 0, 0, 13.5, 16, 8);
                case WEST:
                    return Block.makeCuboidShape(2.5, 0, 8, 3, 16, 16);
            }
        }else{
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
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(VERTICAL_POS);
    }
}
