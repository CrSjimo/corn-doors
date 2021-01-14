package de.myxrcrs.corndoors.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public abstract class AbstractTemplateDoor extends Block {

    public static final BooleanProperty IS_OPENED = BooleanProperty.create("is_opened");
    public static final EnumProperty<Direction> FACING = HorizontalBlock.HORIZONTAL_FACING;

    public AbstractTemplateDoor(Properties props){
        super(props);
    }

    public int getToggleSound(BlockState stateBeforeToggled){
        boolean opened = stateBeforeToggled.get(IS_OPENED);
        if(stateBeforeToggled.getMaterial()==Material.IRON){
            return opened ? 1011 : 1005;
        }else{
            return opened ? 1012 : 1006;
        }
    }

    abstract public boolean toggleDoor(World world, BlockPos pos, BlockState state, DoorHingeSide side)throws Exception;

    abstract public void onHarvested(World world, BlockState state, BlockPos pos)throws Exception;

    abstract public void onPlaced(BlockItemUseContext context, BlockState stateTemplate)throws Exception;

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

}
