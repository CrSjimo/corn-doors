package de.myxrcrs.corndoors.blocks;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import de.myxrcrs.corndoors.init.InitItems;
import de.myxrcrs.corndoors.items.NaiveDoorItem;
import de.myxrcrs.corndoors.util.RotateTarget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

public class DNaiveDoor extends AbstractDoor {

    public static final Property<Integer> HORIZONTAL_POS = createHorizontalPosProperty(1);
    public static final Property<Integer> VERTICAL_POS = createVerticalPosProperty(4);
    public static final EnumProperty<DoorWindowType> WINDOW = EnumProperty.create("window", DoorWindowType.class);
    
    public DNaiveDoor(){
        super(Block.Properties.create(Material.WOOD).notSolid(),false,HORIZONTAL_POS,VERTICAL_POS,3);
        this.setDefaultState(this.getDefaultState()
            .with(IS_OPENED, false)
            .with(FACING, Direction.NORTH)
            .with(HORIZONTAL_POS,0)
            .with(VERTICAL_POS,0)
            .with(HINGE,DoorHingeSide.LEFT)
            .with(WINDOW,DoorWindowType.GLASS));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context){
        DoorWindowType doorWindowType = ((NaiveDoorItem)context.getItem().getItem()).doorWindowType;
        try{
            onPlaced(context,this.getDefaultState().with(WINDOW, doorWindowType));
            return null;
        }catch(Exception e){
            LOGGER.error(e);
            return null;
        }
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        switch(state.get(WINDOW)){
            case GLASS:default: return new ItemStack(InitItems.NAIVE_DOOR_GLASS.get());
            case FILM: return new ItemStack(InitItems.NAIVE_DOOR_FILM.get());
            case NONE: return new ItemStack(InitItems.NAIVE_DOOR_NONE.get());
        }
     }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(IS_OPENED,FACING,VERTICAL_POS,HORIZONTAL_POS,HINGE,WINDOW);
    }

}
