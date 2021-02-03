package de.myxrcrs.corndoors.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IWorld;

public class CornIronPanel extends AbstractPanel {

    public static final BooleanProperty NEAR_ANDESITE = BooleanProperty.create("near_andesite");

    public CornIronPanel(){
        super(Properties.create(Material.IRON),3,false);
    }

    public boolean getNearAndesite(IWorld world,BlockPos pos,Direction facing){
        BlockPos targetPos = pos.offset(facing).down();
        if(world.getBlockState(targetPos).getBlock()==Blocks.POLISHED_ANDESITE){
            return true;
        }
        return false;
    }

    @Override
    public VoxelShape generateBoundaryBox(BlockState state){
        if(!state.get(NEAR_ANDESITE))return super.generateBoundaryBox(state);
        Direction facing = state.get(FACING);
        switch(facing){
            case NORTH:
            default:
                return Block.makeCuboidShape(0, -16, 0, 16, 16, thickness);
            case SOUTH:
                return Block.makeCuboidShape(0, -16, 16-thickness, 16, 16, 16);
            case EAST:
                return Block.makeCuboidShape(16-thickness, -16, 0, 16, 16, 16);
            case WEST:
                return Block.makeCuboidShape(0, -16, 0, thickness, 16, 16);
        }
    }

    @Override
    public BlockState onPlacedThenSetState(BlockItemUseContext context,BlockState stateTemplate){
        BlockState state = super.onPlacedThenSetState(context, stateTemplate);
        return state.with(NEAR_ANDESITE, getNearAndesite(context.getWorld(), context.getPos(), state.get(FACING)));
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return stateIn.with(NEAR_ANDESITE,getNearAndesite(worldIn, currentPos, stateIn.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(NEAR_ANDESITE);
    }
}
