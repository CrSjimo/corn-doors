package de.myxrcrs.corndoors.blocks;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import de.myxrcrs.corndoors.util.Matrix;
import de.myxrcrs.corndoors.util.BooleanRangeIterationConsumer;
import de.myxrcrs.corndoors.util.DoorRange;
import de.myxrcrs.corndoors.util.Zero;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;

public abstract class AbstractTemplateDoor extends Block {

    protected static Property<Integer> createHorizontalPosProperty(int width){
        if(width>1)
            return IntegerProperty.create("horizontal_pos", 0, width-1);
        else
            return Zero.create("horizontal_pos");
    }

    protected static Property<Integer> createVerticalPosProperty(int height){
        if(height>1)
            return IntegerProperty.create("vertical_pos", 0, height-1);
        else
            return Zero.create("vertical_pos");
    }

    public static final BooleanProperty IS_OPENED = BooleanProperty.create("is_opened");
    public static final EnumProperty<Direction> FACING = HorizontalBlock.HORIZONTAL_FACING;

    public final double thickness;

    public AbstractTemplateDoor(Properties props,double thickness){
        super(props);
        this.thickness = thickness;
    }

    public VoxelShape generateBoundaryBox(BlockState state, boolean isMiddle){
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

    public int getToggleSound(BlockState stateBeforeToggled){
        boolean opened = stateBeforeToggled.get(IS_OPENED);
        if(stateBeforeToggled.getMaterial()==Material.IRON){
            return opened ? 1011 : 1005;
        }else{
            return opened ? 1012 : 1006;
        }
    }

    abstract public boolean toggleDoor(World world, BlockPos pos, BlockState state, DoorHingeSide side);

    abstract public void onHarvested(World world, BlockState state, BlockPos pos);

    abstract public void onPlaced(BlockItemUseContext context, BlockState stateTemplate);

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        try{
            onHarvested(worldIn, state, pos);
        }catch(Exception e){
            LOGGER.error(e);
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context){
        try{
            onPlaced(context,this.getDefaultState());
            return null;
        }catch(Exception e){
            LOGGER.error(e);
            return null;
        }
    }

    public static boolean canTogglePos(World world, BlockPos target){
        return world.isAirBlock(target);
    }

    public static boolean canFillRange(World world, DoorRange range){
        return range.iterateRange((x,y,z)->{
            if(!canTogglePos(world, new BlockPos(x,y,z))){
                return false;
            }
            return true;
        });
    }

    public DoorRange getDoorRange(Direction facing, BlockPos pos, DoorHingeSide side, int width, int height, int horizontalPos, int verticalPos) {
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
            return DoorRange.of(fromPos, toPos,v);
    }

    public int getSize(Property<Integer> p){
        if(p.getAllowedValues().contains(114514))return 1;
        else return p.getAllowedValues().size();
    }
}
