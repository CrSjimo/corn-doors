package de.myxrcrs.corndoors.blocks;

import de.myxrcrs.corndoors.init.InitItems;
import de.myxrcrs.corndoors.items.PropertiedBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class CornIronPanel extends AbstractPanel {

    public static enum CornIronPanelType implements IStringSerializable{
        TOP("top"),
        BOTTOM("bottom"),
        FULL("full"),
        NONE("none");

        private final String name;

        CornIronPanelType(String name){
            this.name=name;
        }

        @Override
        public String getString() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static final BooleanProperty NEAR_ANDESITE = BooleanProperty.create("near_andesite");

    public static final EnumProperty<CornIronPanelType> TYPE = EnumProperty.create("type", CornIronPanelType.class);

    public CornIronPanel(){
        super(Properties.create(Material.IRON),5.001,false);
        this.setDefaultState(this.getDefaultState()
            .with(NEAR_ANDESITE,false)
            .with(TYPE, CornIronPanelType.NONE));
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
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return new ItemStack(InitItems.find(this,state.get(TYPE)));
    }

    @Override
    public BlockState onPlacedThenSetState(BlockItemUseContext context,BlockState stateTemplate){
        BlockState state = super.onPlacedThenSetState(context, stateTemplate);
        CornIronPanelType type = ((PropertiedBlockItem<CornIronPanelType>)context.getItem().getItem()).property;
        return state
            .with(TYPE, type)
            .with(NEAR_ANDESITE, getNearAndesite(context.getWorld(), context.getPos(), state.get(FACING)));
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return stateIn.with(NEAR_ANDESITE,getNearAndesite(worldIn, currentPos, stateIn.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(NEAR_ANDESITE,TYPE);
    }
}
