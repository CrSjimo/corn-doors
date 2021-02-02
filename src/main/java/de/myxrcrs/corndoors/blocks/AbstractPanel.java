package de.myxrcrs.corndoors.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

abstract public class AbstractPanel extends Block{

    public static final EnumProperty<Direction> FACING = HorizontalBlock.HORIZONTAL_FACING;

    /**
     * The thickness of the door model.
     */
    public final double thickness;

    public final boolean isMiddle;


    /**
     * 
     * @param props Block properties. As the properties in {@link net.minecraft.block.Block#Block}.
     * @param thickness The thickness of the door model.
     */
    public AbstractPanel(Properties props,double thickness,boolean isMiddle){
        super(props);
        this.thickness = thickness;
        this.isMiddle = isMiddle;
        this.setDefaultState(this.getDefaultState()
            .with(FACING, Direction.NORTH));
    }

    /**
     * Generate the boundary box of current door
     * @param state Block state.
     * @param isMiddle Whether current door is located in the middle of a block. 
     * <img src="https://img.imgdb.cn/item/600101e43ffa7d37b31841ec.png" alt="The diagram of isMiddle.">
     * @return Boundary box
     */
    public VoxelShape generateBoundaryBox(BlockState state){
        Direction facing = state.get(FACING);
        switch(facing){
            case NORTH:
            default:
                return isMiddle ? Block.makeCuboidShape(0, 0, 8-0.5*thickness, 16, 16, 8+0.5*thickness) : Block.makeCuboidShape(0, 0, 0, 16, 16, thickness);
            case SOUTH:
                return isMiddle ? Block.makeCuboidShape(0, 0, 8-0.5*thickness, 16, 16, 8+0.5*thickness) : Block.makeCuboidShape(0, 0, 16-thickness, 16, 16, 16);
            case EAST:
                return isMiddle ? Block.makeCuboidShape(8-0.5*thickness, 0, 0, 8+0.5*thickness, 16, 16) : Block.makeCuboidShape(16-thickness, 0, 0, 16, 16, 16);
            case WEST:
                return isMiddle ? Block.makeCuboidShape(8-0.5*thickness, 0, 0, 8+0.5*thickness, 16, 16) : Block.makeCuboidShape(0, 0, 0, thickness, 16, 16);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return generateBoundaryBox(state);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context){
        return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(FACING);
    }
}
