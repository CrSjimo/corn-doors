package de.myxrcrs.corndoors.blocks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import de.myxrcrs.util.Matrix;
import de.myxrcrs.util.RangeIterationConsumer;
import de.myxrcrs.util.RotateTarget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class AbstractDoor extends Block {

    protected static IntegerProperty createHorizontalPosProperty(int width){
        return IntegerProperty.create("horizontal_pos", 0, width-1);
    }

    protected static IntegerProperty createVerticalPosProperty(int height){
        return IntegerProperty.create("vertical_pos", 0, height-1);
    }

    public static final BooleanProperty IS_OPENED = BooleanProperty.create("is_opened");
    public static final EnumProperty<Direction> FACING = HorizontalBlock.HORIZONTAL_FACING;

    public boolean rotateWithinHinge;

    public AbstractDoor(Properties props, boolean rotateWithinHinge){
        super(props);
        this.rotateWithinHinge = rotateWithinHinge;
    }

    public RotateTarget getRotateTarget(BlockState state, BlockPos pos, int horizontalPos, DoorHingeSide side)throws Exception{
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
        double[][] a = {{pos.getX(),pos.getZ()}};
        double[][] u = Matrix.horizontalDirectionToMatrix(state.get(FACING));
        boolean flag = side == DoorHingeSide.LEFT;
        boolean flag2 = state.get(IS_OPENED);
        double[][] v = Matrix.mul(u,flag?hL:hR);
        double[][] center = Matrix.add(a,Matrix.mul(v,horizontalPos));
        if(!rotateWithinHinge){
            center = Matrix.add(center,Matrix.mul(Matrix.add(u,v),0.5));
        }
        double[][] target = Matrix.mul(new double[][]{{pos.getX(),pos.getZ(),center[0][0],center[0][1]}},flag!=flag2?rL:rR);
        return new RotateTarget(Matrix.matrixToHorizontalDirection(Matrix.mul(v,flag2?-1:1)),new BlockPos(target[0][0],pos.getY(),target[0][1]));
    }

    public static VoxelShape generateBoundaryBox(BlockState state, double thickness){
        Direction facing = state.get(FACING);
        switch(facing){
            case NORTH:
            default:
                return Block.makeCuboidShape(0, 0, 0, 16, 16, thickness);
            case SOUTH:
                return Block.makeCuboidShape(0, 0, 16-thickness, 16, 16, 16);
            case EAST:
                return Block.makeCuboidShape(16-thickness, 0, 0, 16, 16, 16);
            case WEST:
                return Block.makeCuboidShape(0, 0, 0, thickness, 16, 16);
        }
    }

    public Triple<BlockPos,BlockPos,double[][]> getDoorRange(Direction facing, BlockPos pos, DoorHingeSide side, int width, int height, int horizontalPos, int verticalPos)throws Exception {
        final double[][] hL = 
            {{ 0, 1},
            { -1, 0}};
        final double[][] hR = 
            {{ 0,-1},
            {  1, 0}};
            double[][] a = {{pos.getX(),pos.getZ()}};
            double[][] u = Matrix.horizontalDirectionToMatrix(facing);
            boolean flag = side == DoorHingeSide.LEFT;
            double[][] v = Matrix.mul(u,flag?hL:hR);
            double[][] center = Matrix.add(a,Matrix.mul(v,horizontalPos));
            BlockPos fromPos = new BlockPos(center[0][0],pos.getY()-verticalPos,center[0][1]);
            //double[][] edge = Matrix.add(a,Matrix.mul(v,horizontalPos-width+1));
            double[][] edge = Matrix.add(a,Matrix.mul(v,horizontalPos-width));
            BlockPos toPos = new BlockPos(edge[0][0],pos.getY()+height-verticalPos,edge[0][1]);
            return Triple.of(fromPos, toPos,v);
    }

    public boolean toggleDoor(World world, BlockPos pos, BlockState state, IntegerProperty HORIZONTAL_POS, IntegerProperty VERTICAL_POS, DoorHingeSide side)throws Exception {
        

        int horizontalPos = state.get(HORIZONTAL_POS);
        int verticalPos = state.get(VERTICAL_POS);
        int width = getSize(HORIZONTAL_POS);
        int height = getSize(VERTICAL_POS);

        Triple<BlockPos,BlockPos,double[][]> range = getDoorRange(state.get(FACING), pos, side, width, height, horizontalPos, verticalPos);
        ArrayList<Triple<BlockPos,BlockState,RotateTarget>> rotates = new ArrayList<>();
        if(!iterateRange(range, (x,y,z)->{
            LOGGER.info(x+" "+y+" "+z);
            BlockPos currentPos = new BlockPos(x,y,z);
            BlockState currentState = world.getBlockState(currentPos);
            //TODO check whether current state is air and trigger block harvest
            RotateTarget rotateTarget = getRotateTarget(currentState,currentPos,currentState.get(HORIZONTAL_POS),side);
            if(!canTogglePos(world, rotateTarget.pos))return false;
            rotates.add(Triple.of(currentPos,currentState,rotateTarget));
            return true;
        }))return false;
        
        LOGGER.info(rotates);
        Iterator<Triple<BlockPos,BlockState,RotateTarget>> ptr = rotates.iterator();
        while(ptr.hasNext()){
            Triple<BlockPos,BlockState,RotateTarget> p = ptr.next();
            toggleDoorPos(world, p.getLeft(), p.getMiddle(), p.getRight());
        }
        return true;
    }

    public boolean iterateRange(Triple<BlockPos,BlockPos,double[][]>range, RangeIterationConsumer func)throws Exception{
        BlockPos fromPos = range.getLeft();
        BlockPos toPos = range.getMiddle();
        double[][] v = range.getRight();
        boolean xOnce = false;
        for(int x = fromPos.getX() ; x != toPos.getX() || (v[0][0]==0&&!xOnce) ; x-=(int)v[0][0]){
            for(int y = fromPos.getY() ; y != toPos.getY() ; y++){
                boolean zOnce = false;
                for(int z = fromPos.getZ(); z != toPos.getZ() || (v[0][1]==0&&!zOnce) ; z-=(int)v[0][1]){
                    if(!func.iterate(x, y, z))return false;
                    zOnce = true;
                }
            }
            xOnce = true;
        }
        return true;
    }

    public boolean canTogglePos(World world, BlockPos target){
        return world.isAirBlock(target);
    }

    public void toggleDoorPos(World world, BlockPos pos, BlockState state, RotateTarget rotateTarget){
        world.setBlockState(rotateTarget.pos, state.with(FACING, rotateTarget.facing).cycle(IS_OPENED));
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
    }

    public int getSize(IntegerProperty p){
        return p.getAllowedValues().size();
    }

    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
     }
   
    public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }
   
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }
}
