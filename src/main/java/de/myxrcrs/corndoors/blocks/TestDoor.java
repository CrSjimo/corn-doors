package de.myxrcrs.corndoors.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class TestDoor extends Block {

    public static final IntegerProperty A = IntegerProperty.create("a", 1, 8);
    public static final IntegerProperty B = IntegerProperty.create("b", 0, 1);
    
    public TestDoor(){
        super(Block.Properties.create(Material.WOOD));
        this.setDefaultState(this.getDefaultState().with(A, 1).with(B, 0));
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(A,B);
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
