package de.myxrcrs.corndoors.blocks;

import de.myxrcrs.util.Matrix;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractDoor extends Block {

    public static final BooleanProperty IS_OPENED = BooleanProperty.create("is_opened");
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
    public static final EnumProperty<Direction> FACING = HorizontalBlock.HORIZONTAL_FACING;

    public boolean rotateWithinHinge;

    public int height;

    public int width;

    public IntegerProperty HORIZONTAL_POS;

    public IntegerProperty VERTICAL_POS;

    
    public AbstractDoor(Properties props, boolean rotateWithinHinge, int height, int width){
        super(props);
        this.rotateWithinHinge = rotateWithinHinge;
        this.height = height;
        this.width = width;
        HORIZONTAL_POS = IntegerProperty.create("horizontal_pos", 0, width-1);
        VERTICAL_POS = IntegerProperty.create("vertical_pos", 0, height-1);
        this.setDefaultState(this.getDefaultState()
            .with(IS_OPENED, false)
            .with(HINGE, DoorHingeSide.LEFT)
            .with(FACING, Direction.NORTH)
            .with(HORIZONTAL_POS, 0)
            .with(VERTICAL_POS, 0));
    }

    public BlockPos getRotateTarget(BlockState state, BlockPos pos)throws Exception{
        final double[][] hL = 
            {{ 0, 1},
            { -1, 0}};
        final double[][] hR = 
            {{ 0,-1},
            {  1, 0}};
        final double[][] rL = 
            {{ 0, 1, 0, 0},
            { -1, 0, 0, 0},
            {  1,-1, 1, 0},
            {  1, 1, 0, 1}};
        final double[][] rR = 
            {{ 0,-1, 0, 0},
            {  1, 0, 0, 0},
            {  1, 1, 1, 0},
            { -1, 1, 0, 1}};
        int n = state.get(HORIZONTAL_POS);
        double[][] a = {{pos.getX(),pos.getZ()}};
        double[][] u = Matrix.directionToMatrix(state.get(FACING));
        boolean flag = state.get(HINGE) == DoorHingeSide.LEFT;
        boolean flag2 = state.get(IS_OPENED);
        double[][] v = Matrix.mul(u,flag?hL:hR);
        double[][] center = Matrix.add(a,Matrix.mul(v,n));
        if(!rotateWithinHinge){
            center = Matrix.add(center,Matrix.mul(Matrix.add(u,v),0.5));
        }
        double[][] target = Matrix.mul(new double[][]{{pos.getX(),pos.getZ(),center[0][0],center[0][1]}},flag!=flag2?rL:rR);
        return new BlockPos(target[0][0],pos.getY(),target[0][1]);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(IS_OPENED,HINGE,FACING,HORIZONTAL_POS,VERTICAL_POS);
    }
}
