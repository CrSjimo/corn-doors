package de.myxrcrs.corndoors.blocks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import de.myxrcrs.corndoors.util.Matrix;
import de.myxrcrs.corndoors.util.BooleanRangeIterationConsumer;
import de.myxrcrs.corndoors.util.DoorRange;
import de.myxrcrs.corndoors.util.RotateTarget;
import de.myxrcrs.corndoors.util.Zero;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class AbstractDoor extends AbstractTemplateDoor implements IRotateDoor {
    
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;

    public boolean rotateWithinHinge;
    @Nullable public AbstractDualDoorEdge correspondingDualDoorEdgeBlock;

    public final Property<Integer> HORIZONTAL_POS;
    public final Property<Integer> VERTICAL_POS;
    

    public AbstractDoor(Properties props, boolean rotateWithinHinge, Property<Integer> horizontalPosProp, Property<Integer> verticalPosProp, double thickness){
        this(props, rotateWithinHinge, horizontalPosProp, verticalPosProp, thickness, null);
    }

    public AbstractDoor(Properties props, boolean rotateWithinHinge, Property<Integer> horizontalPosProp, Property<Integer> verticalPosProp, double thickness, @Nullable AbstractDualDoorEdge correspondingDualDoorEdgeBlock){
        super(props,thickness);
        this.HORIZONTAL_POS = horizontalPosProp;
        this.VERTICAL_POS = verticalPosProp;
        this.rotateWithinHinge = rotateWithinHinge;
        this.correspondingDualDoorEdgeBlock = correspondingDualDoorEdgeBlock;
    }

    public RotateTarget getRotateTarget(BlockState state, BlockPos pos){
        return getRotateTarget(state.get(FACING), state.get(HORIZONTAL_POS), state.get(HINGE), state.get(IS_OPENED), pos);
    }

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
        if(!rotateWithinHinge){
            center = Matrix.add(center,Matrix.mul(Matrix.add(u,v),0.5));
        }
        double[][] target = Matrix.mul(new double[][]{{pos.getX(),pos.getZ(),center[0][0],center[0][1]}},flag!=flag2?rL:rR);
        return new RotateTarget(Matrix.matrixToHorizontalDirection(Matrix.mul(v,flag2?-1:1)),new BlockPos(target[0][0],pos.getY(),target[0][1]),side);
    }

    public int getWidth(){
        return getSize(HORIZONTAL_POS);
    }

    public int getHeight(){
        return getHeight();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return generateBoundaryBox(state,rotateWithinHinge);
    }

    public BlockPos getNeighborDualDoorEdgePos(BlockPos pos, BlockState state, DoorHingeSide side){
        double[][] a = {{pos.getX(),pos.getY()}};
        double[][] u = Matrix.horizontalDirectionToMatrix(state.get(FACING));
        double[][] v = Matrix.getHingeVector(u, side == DoorHingeSide.LEFT ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT);
        double[][] b = Matrix.add(a, v);
        return new BlockPos(b[0][0],pos.getY(),b[0][1]);
    }

    @Override
    public boolean toggleDoor(World world, BlockPos pos, BlockState state, DoorHingeSide side) {
        

        int horizontalPos = state.get(HORIZONTAL_POS);
        int verticalPos = state.get(VERTICAL_POS);
        int width = getWidth();
        int height = getHeight();

        DoorRange range = getDoorRange(state.get(FACING), pos, side, width, height, horizontalPos, verticalPos);
        ArrayList<Triple<BlockPos,BlockState,RotateTarget>> rotates = new ArrayList<>();
        BlockState stateTemplate = world.getBlockState(range.getFrom());
        if(!range.iterateRange((x,y,z)->{
            int currentHorizontalPos = Math.abs(x-range.getFrom().getX())+Math.abs(z-range.getFrom().getZ());
            int currentVerticalPos = y-range.getFrom().getY();
            BlockPos currentPos = new BlockPos(x,y,z);
            BlockState currentState = stateTemplate.with(HORIZONTAL_POS, currentHorizontalPos).with(VERTICAL_POS, currentVerticalPos);
            if(currentState.getBlock()!=this){
                this.onHarvested(world, state, pos);
                return false;
            }
            RotateTarget rotateTarget = getRotateTarget(currentState,currentPos);
            if(!rotateTarget.pos.equals(currentPos)&&!canTogglePos(world, rotateTarget.pos))return false;
            if(correspondingDualDoorEdgeBlock != null && currentHorizontalPos == width){
                BlockPos edgePos = getNeighborDualDoorEdgePos(currentPos, currentState, side);
                BlockState edgeState = world.getBlockState(edgePos);
                if(edgeState.getBlock()!=correspondingDualDoorEdgeBlock){
                    this.onHarvested(world, state, pos);
                    return false;
                }
                RotateTarget edgeRotateTarget = getRotateTarget(currentState.get(FACING), horizontalPos+1, side, currentState.get(IS_OPENED), edgePos);
                if(!canTogglePos(world, edgeRotateTarget.pos))return false;
                rotates.add(Triple.of(edgePos,edgeState,edgeRotateTarget));
            }
            rotates.add(Triple.of(currentPos,currentState,rotateTarget));
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
        return world.setBlockState(pos, Blocks.AIR.getDefaultState())
            && world.setBlockState(rotateTarget.pos, state.with(FACING, rotateTarget.facing).cycle(IS_OPENED));
    }

    public void fillRange(World world, DoorRange range, BlockState stateTemplate){
        range.iterateRange((x,y,z)->{
            int horizontalPos = Math.abs(x-range.getFrom().getX())+Math.abs(z-range.getFrom().getZ());
            int verticalPos = y-range.getFrom().getY();
            world.setBlockState(new BlockPos(x,y,z), stateTemplate.with(HORIZONTAL_POS,horizontalPos).with(VERTICAL_POS, verticalPos));
        });
    }

    @Override
    public void onPlaced(BlockItemUseContext context, BlockState stateTemplate){
        Direction facing = context.getPlacementHorizontalFacing().getOpposite();
        DoorRange leftHingeRange = getDoorRange(facing, context.getPos(), DoorHingeSide.LEFT, getWidth(), getHeight(), 0, 0);
        DoorRange rightHingeRange = getDoorRange(facing, context.getPos(), DoorHingeSide.RIGHT, getWidth(), getHeight(), 0, 0);
        boolean canLeftHinge = canFillRange(context.getWorld(), leftHingeRange);
        if(canLeftHinge){
            fillRange(
                context.getWorld(),
                leftHingeRange,
                stateTemplate
                    .with(FACING, facing)
                    .with(HINGE, DoorHingeSide.LEFT)
            );
            return;
        }
        boolean canRightHinge = canFillRange(context.getWorld(), rightHingeRange);
        if(canRightHinge){
            fillRange(
                context.getWorld(),
                rightHingeRange,
                stateTemplate
                    .with(FACING, facing)
                    .with(HINGE, DoorHingeSide.RIGHT)
            );
            return;
        }
    }

    @Override
    public void onHarvested(World world, BlockState state, BlockPos pos){
        DoorRange range = getDoorRange(state.get(FACING), pos, state.get(HINGE), getWidth(), getHeight(), state.get(HORIZONTAL_POS), state.get(VERTICAL_POS));
        int width = getWidth();
        DoorHingeSide side = state.get(HINGE);
        range.iterateRange((x,y,z)->{
            BlockPos currentPos = new BlockPos(x,y,z);
            BlockState currentState = world.getBlockState(currentPos);
            int currentHorizontalPos = Math.abs(x-range.getFrom().getX())+Math.abs(z-range.getFrom().getZ());
            if(currentState.getBlock()==this){
                world.setBlockState(currentPos, Blocks.AIR.getDefaultState());
                if(correspondingDualDoorEdgeBlock != null && currentHorizontalPos == width){
                    BlockPos edgePos = getNeighborDualDoorEdgePos(currentPos, currentState, side);
                    BlockState edgeState = world.getBlockState(edgePos);
                    if(edgeState.getBlock()==correspondingDualDoorEdgeBlock){
                        world.setBlockState(edgePos, Blocks.AIR.getDefaultState());
                    }
                }
            }
        });
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(handIn == Hand.OFF_HAND)return ActionResultType.PASS;
        try{
            LOGGER.info(AbstractDualDoorEdge.getSideFromHit(state, pos, hit.getHitVec()));
            if(toggleDoor(worldIn, pos, state, state.get(HINGE))){
                worldIn.playEvent(player, getToggleSound(state), pos, 0);
                return ActionResultType.SUCCESS;
            }else{
                return ActionResultType.CONSUME;
            }
        }catch(Exception e){
            LOGGER.error(e);
            return ActionResultType.CONSUME;
        }
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
