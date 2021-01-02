package de.myxrcrs.corndoors.blocks;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import de.myxrcrs.util.RotateTarget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
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
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class TestDoor extends AbstractDoor {

    public static final IntegerProperty HORIZONTAL_POS = createHorizontalPosProperty(2);
    public static final IntegerProperty VERTICAL_POS = createVerticalPosProperty(4);
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
    
    public TestDoor(){
        super(Block.Properties.create(Material.WOOD).notSolid(),false);
        this.setDefaultState(this.getDefaultState()
            .with(IS_OPENED, false)
            .with(FACING, Direction.NORTH)
            .with(HORIZONTAL_POS,0)
            .with(VERTICAL_POS,0)
            .with(HINGE,DoorHingeSide.LEFT));
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(handIn == Hand.OFF_HAND)return ActionResultType.PASS;
        try{
            if(toggleDoor(worldIn, pos, state, HORIZONTAL_POS, VERTICAL_POS, state.get(HINGE))){
                return ActionResultType.SUCCESS;
            }else{
                return ActionResultType.FAIL;
            }
        }catch(Exception e){
            LOGGER.error(e);
            return ActionResultType.FAIL;
        }
        
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context){
        try{
            Direction facing = context.getPlacementHorizontalFacing().getOpposite();
            Triple<BlockPos,BlockPos,double[][]> leftHingeRange = getDoorRange(facing, context.getPos(), DoorHingeSide.LEFT, getSize(HORIZONTAL_POS), getSize(VERTICAL_POS), 0, 0);
            Triple<BlockPos,BlockPos,double[][]> rightHingeRange = getDoorRange(facing, context.getPos(), DoorHingeSide.LEFT, getSize(HORIZONTAL_POS), getSize(VERTICAL_POS), 0, 0);
            boolean canLeftHinge = iterateRange(leftHingeRange,(x,y,z)->{
                if(!canTogglePos(context.getWorld(), new BlockPos(x,y,z))){
                    return false;
                }
                return true;
            });
            if(canLeftHinge){
                //TODO fill the range
                return this.getDefaultState();
            }
            boolean canRightHinge = iterateRange(rightHingeRange,(x,y,z)->{
                if(!canTogglePos(context.getWorld(), new BlockPos(x,y,z))){
                    return false;
                }
                return true;
            });
            if(canRightHinge){
                //TODO fill the range
                return this.getDefaultState();
            }
            return Blocks.AIR.getDefaultState();
        }catch(Exception e){
            LOGGER.error(e);
            return Blocks.AIR.getDefaultState();
        }
        
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return generateBoundaryBox(state, 3);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(IS_OPENED,FACING,VERTICAL_POS,HORIZONTAL_POS,HINGE);
    }

}
