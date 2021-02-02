package de.myxrcrs.corndoors.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.Property;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class AbstractStretchDoor extends AbstractIndividualDoor {

    public final int fixedPartSize;

    public AbstractStretchDoor(Properties props, Property<Integer> HORIZONTAL_POS, Property<Integer> VERTICAL_POS, double thickness, boolean isMiddle, int fixedPartSize) {
        super(props, HORIZONTAL_POS, VERTICAL_POS, thickness, isMiddle);
        this.fixedPartSize = fixedPartSize;
    }

    public boolean isShrinkPart(BlockState state) {
        return state.get(HORIZONTAL_POS) >= fixedPartSize;
    }

    public BlockState setHorizontalPos(BlockState state, int pos) {
        return state.with(HORIZONTAL_POS, pos);
    }

    public BlockState setVerticalPos(BlockState state, int pos) {
        return state.with(VERTICAL_POS, pos);
    }

    @Override
    public boolean toggleDoor(World world, BlockPos pos, BlockState state, DoorHingeSide side) {
        int horizontalPos = state.get(HORIZONTAL_POS);
        int verticalPos = state.get(VERTICAL_POS);
        int width = getWidth(state);
        int height = getHeight(state);

        DoorRange range = getDoorRange(state.get(FACING), pos, side, width, height, horizontalPos, verticalPos);
        BlockState stateTemplate = world.getBlockState(range.getFrom());
        if(!range.iterateRange((x,y,z)->{
            int currentHorizontalPos = Math.abs(x-range.getFrom().getX())+Math.abs(z-range.getFrom().getZ());
            int currentVerticalPos = y-range.getFrom().getY();
            BlockPos currentPos = new BlockPos(x,y,z);
            BlockState currentState = setVerticalPos(setHorizontalPos(stateTemplate, currentHorizontalPos),currentVerticalPos);
            if(!state.get(IS_OPENED)||!isShrinkPart(currentState)){
                if(currentState.getBlock()!=this){
                    this.onHarvested(world, state, pos);
                    return false;
                }
            }
            if(!canTogglePos(world, currentState, currentPos))return false;
            return true;
        })){
            return false;
        }
        return range.iterateRange((x,y,z)->{
            int currentHorizontalPos = Math.abs(x-range.getFrom().getX())+Math.abs(z-range.getFrom().getZ());
            int currentVerticalPos = y-range.getFrom().getY();
            BlockState currentState = setVerticalPos(setHorizontalPos(stateTemplate, currentHorizontalPos),currentVerticalPos);
            BlockPos currentPos = new BlockPos(x,y,z);
            return toggleDoorPos(world, currentState, currentPos);
        });
    }

    @Override
    public boolean canTogglePos(World world, BlockState state, BlockPos target) {
        return state.getBlock()==this||world.isAirBlock(target);
    }

    public boolean toggleDoorPos(World world, BlockState state, BlockPos pos){
        BlockState newState;
        if(state.get(IS_OPENED)){
            newState = state.with(IS_OPENED,false);
            return world.setBlockState(pos, onWillSetNewState(world, newState, pos));
        }else{
            if(isShrinkPart(state)){
                newState = Blocks.AIR.getDefaultState();
                return world.setBlockState(pos, onWillSetNewState(world, newState, pos));
            }else{
                newState = state.with(IS_OPENED,true);
                return world.setBlockState(pos, onWillSetNewState(world, newState, pos));
            }
            
        }
    }

    @Override
    public void onHarvested(World world, BlockState state, BlockPos pos) {
        DoorRange range = getDoorRange(state.get(FACING), pos, state.get(HINGE), getWidth(state), getHeight(state), state.get(HORIZONTAL_POS), state.get(VERTICAL_POS));
        range.iterateRange((x,y,z)->{
            BlockPos currentPos = new BlockPos(x,y,z);
            BlockState currentState = world.getBlockState(currentPos);
            if(currentState.getBlock()==this){
                world.setBlockState(currentPos, Blocks.AIR.getDefaultState());
                onDidHarvest(world, currentState, pos);
            }
        });

    }

}
