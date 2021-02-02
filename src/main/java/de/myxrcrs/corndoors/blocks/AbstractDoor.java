package de.myxrcrs.corndoors.blocks;

import java.util.ArrayList;
import java.util.Iterator;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import de.myxrcrs.corndoors.util.Matrix;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.Property;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

/**
 * Corn Doors which rotates about a fixed center.
 */
public abstract class AbstractDoor extends AbstractIndividualDoor implements IRotateDoor {

    /**
     * Center of rotation is at the corner of hinge (false) or at the center of hinge (true).
     * <p>
     * <img src="https://img.imgdb.cn/item/600102cf3ffa7d37b318a216.png" alt="the center of hinge">
     * the center of hinge
     * <p>
     * <img src="https://img.imgdb.cn/item/600102dd3ffa7d37b318a7dd.png" alt="the center of hinge">
     * the corner of hinge
     */
    @Nullable public AbstractDualDoorEdge correspondingDualDoorEdgeBlock;
    

    public AbstractDoor(Properties props, Property<Integer> horizontalPosProp, Property<Integer> verticalPosProp, double thickness, boolean isMiddle){
        this(props, horizontalPosProp, verticalPosProp, thickness, isMiddle, null);
    }

    public AbstractDoor(Properties props, Property<Integer> horizontalPosProp, Property<Integer> verticalPosProp, AbstractDualDoorEdge correspondingDualDoorEdgeBlock){
        this(props, horizontalPosProp, verticalPosProp, correspondingDualDoorEdgeBlock.thickness, correspondingDualDoorEdgeBlock.isMiddle, correspondingDualDoorEdgeBlock);
    }

    protected AbstractDoor(Properties props, Property<Integer> horizontalPosProp, Property<Integer> verticalPosProp, double thickness, boolean isMiddle, @Nullable AbstractDualDoorEdge correspondingDualDoorEdgeBlock){
        super(props,horizontalPosProp,verticalPosProp,thickness, isMiddle);
        if(correspondingDualDoorEdgeBlock!=null&&(getSize(correspondingDualDoorEdgeBlock.VERTICAL_POS)!=getHeight(getDefaultState()))){
            throw new IllegalArgumentException("Corresponding block not match.");
        }
        this.correspondingDualDoorEdgeBlock = correspondingDualDoorEdgeBlock;
        if(this.correspondingDualDoorEdgeBlock!=null){
            this.correspondingDualDoorEdgeBlock.setCorrespondingDoorBlock(this);
        }
    }

    public RotateTarget getRotateTarget(BlockState state, BlockPos pos){
        return getRotateTarget(state.get(FACING), state.get(HORIZONTAL_POS), state.get(HINGE), state.get(IS_OPENED), pos);
    }

    /**
     * Get the target position of rotation.
     * @param facing The direction along which the door faces
     * @param horizontalPos The horizontal position of current block.
     * @param side The hinge side.
     * @param isOpened Whether the door is opened (determines CW/CCW). 
     * @param pos The position of current block.
     * @return Rotation target.
     */
    public RotateTarget getRotateTarget(Direction facing, int horizontalPos, DoorHingeSide side, boolean isOpened, BlockPos pos){
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
        double[][] u = Matrix.horizontalDirectionToMatrix(facing);
        boolean flag = side == DoorHingeSide.LEFT;
        boolean flag2 = isOpened;
        double[][] v = Matrix.getHingeVector(u, side);
        double[][] center = Matrix.add(a,Matrix.mul(v,horizontalPos));
        if(!isMiddle){
            center = Matrix.add(center,Matrix.mul(Matrix.add(u,v),0.5));
        }
        double[][] target = Matrix.mul(new double[][]{{pos.getX(),pos.getZ(),center[0][0],center[0][1]}},flag!=flag2?rL:rR);
        return new RotateTarget(Matrix.matrixToHorizontalDirection(Matrix.mul(v,flag2?-1:1)),new BlockPos(target[0][0],pos.getY(),target[0][1]),side);
    }

    public BlockPos getNeighborDualDoorEdgePos(BlockPos pos, BlockState state, DoorHingeSide side){
        double[][] a = {{pos.getX(),pos.getZ()}};
        double[][] u = Matrix.horizontalDirectionToMatrix(state.get(FACING));
        double[][] v = Matrix.getHingeVector(u, side == DoorHingeSide.LEFT ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT);
        double[][] b = Matrix.add(a, v);
        return new BlockPos(b[0][0],pos.getY(),b[0][1]);
    }

    @Override
    public boolean toggleDoor(World world, BlockPos pos, BlockState state, DoorHingeSide side) {
        
        LOGGER.debug("doorpos = "+pos);
        LOGGER.debug("paramside = "+side);
        LOGGER.debug("stateside = "+state.get(HINGE));

        int horizontalPos = state.get(HORIZONTAL_POS);
        int verticalPos = state.get(VERTICAL_POS);
        int width = getWidth(state);
        int height = getHeight(state);

        DoorRange range = getDoorRange(state.get(FACING), pos, side, width, height, horizontalPos, verticalPos);
        ArrayList<Triple<BlockPos,BlockState,RotateTarget>> rotates = new ArrayList<>();
        BlockState stateTemplate = world.getBlockState(range.getFrom());
        if(!range.iterateRange((x,y,z)->{
            int currentHorizontalPos = Math.abs(x-range.getFrom().getX())+Math.abs(z-range.getFrom().getZ());
            int currentVerticalPos = y-range.getFrom().getY();
            BlockPos currentPos = new BlockPos(x,y,z);
            BlockState currentState = setVerticalPos(setHorizontalPos(stateTemplate, currentHorizontalPos),currentVerticalPos);
            if(currentState.getBlock()!=this){
                LOGGER.debug("Harv 1");
                this.onHarvested(world, state, pos);
                return false;
            }
            RotateTarget rotateTarget = getRotateTarget(currentState,currentPos);
            if(!rotateTarget.pos.equals(currentPos)&&!canTogglePos(world, currentState,rotateTarget.pos))return false;
            if(correspondingDualDoorEdgeBlock != null && currentHorizontalPos == width-1){
                BlockPos edgePos = getNeighborDualDoorEdgePos(currentPos, currentState, side);
                BlockState edgeState = world.getBlockState(edgePos);
                if(edgeState.getBlock()!=correspondingDualDoorEdgeBlock){
                    LOGGER.debug("Harv 2");
                    this.onHarvested(world, state, pos);
                    return false;
                }
                RotateTarget edgeRotateTarget = getRotateTarget(currentState.get(FACING), currentHorizontalPos+1, side, currentState.get(IS_OPENED), edgePos);
                if(!correspondingDualDoorEdgeBlock.canTogglePos(world, edgeState,edgeRotateTarget.pos))return false;
                rotates.add(Triple.of(edgePos,edgeState,edgeRotateTarget));
                if(!correspondingDualDoorEdgeBlock.onDidCheck(world, edgeState, edgePos))return false;
            }
            rotates.add(Triple.of(currentPos,currentState,rotateTarget));
            if(!onDidCheck(world,currentState,currentPos))return false;
            return true;
        }))return false;
        
        Iterator<Triple<BlockPos,BlockState,RotateTarget>> ptr = rotates.iterator();
        while(ptr.hasNext()){
            Triple<BlockPos,BlockState,RotateTarget> p = ptr.next();
            IRotateDoor door = (IRotateDoor) p.getMiddle().getBlock();
            door.toggleDoorPos(world, p.getLeft(), p.getMiddle(), p.getRight());
        }
        return true;
    }

    public boolean toggleDoorPos(World world, BlockPos pos, BlockState state, RotateTarget rotateTarget){
        BlockState newState = state.with(FACING, rotateTarget.facing).cycle(IS_OPENED);
        newState = onWillSetNewState(world, newState, rotateTarget.pos);
        return world.setBlockState(pos, Blocks.AIR.getDefaultState())
            && world.setBlockState(rotateTarget.pos, newState);
    }

    @Override
    public void onHarvested(World world, BlockState state, BlockPos pos){
        DoorRange range = getDoorRange(state.get(FACING), pos, state.get(HINGE), getWidth(state), getHeight(state), state.get(HORIZONTAL_POS), state.get(VERTICAL_POS));
        int width = getWidth(state);
        DoorHingeSide side = state.get(HINGE);
        range.iterateRange((x,y,z)->{
            BlockPos currentPos = new BlockPos(x,y,z);
            BlockState currentState = world.getBlockState(currentPos);
            int currentHorizontalPos = Math.abs(x-range.getFrom().getX())+Math.abs(z-range.getFrom().getZ());
            if(currentState.getBlock()==this){
                world.setBlockState(currentPos, Blocks.AIR.getDefaultState());
                if(correspondingDualDoorEdgeBlock != null && currentHorizontalPos == width-1){
                    BlockPos edgePos = getNeighborDualDoorEdgePos(currentPos, currentState, side);
                    BlockState edgeState = world.getBlockState(edgePos);
                    if(edgeState.getBlock()==correspondingDualDoorEdgeBlock){
                        correspondingDualDoorEdgeBlock.triggerNeighborHarvest(world, edgeState, edgePos, side == DoorHingeSide.LEFT ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT);
                        world.setBlockState(edgePos, Blocks.AIR.getDefaultState());
                        correspondingDualDoorEdgeBlock.onDidHarvest(world, edgeState, edgePos);
                    }
                }
                onDidHarvest(world,currentState,currentPos);
            }
        });
    }

}
