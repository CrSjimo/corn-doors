package de.myxrcrs.corndoors.blocks;

import de.myxrcrs.corndoors.init.InitItems;
import de.myxrcrs.corndoors.items.NaiveDoorItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class DNaiveDualDoorEdge extends AbstractDualDoorEdge {
    
    public static final Property<Integer> VERTICAL_POS = createVerticalPosProperty(4);
    public static final EnumProperty<DoorWindowType> WINDOW = DNaiveDoor.WINDOW;

    public DNaiveDualDoorEdge(){
        super(Properties.create(Material.WOOD),VERTICAL_POS,3,false);
        this.setDefaultState(this.getDefaultState()
            .with(VERTICAL_POS, 0)
            .with(WINDOW, DoorWindowType.GLASS));
    }

    @Override
    public void fillRange(World world, DoorRange range, BlockState stateTemplate, BlockItemUseContext context){
        DoorWindowType doorWindowType = ((NaiveDoorItem)context.getItem().getItem()).doorWindowType;
        super.fillRange(world, range, stateTemplate.with(WINDOW, doorWindowType), context);
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        switch(state.get(WINDOW)){
            case GLASS:default: return new ItemStack(InitItems.D_NAIVE_DOOR_GLASS.get());
            case FILM: return new ItemStack(InitItems.D_NAIVE_DOOR_FILM.get());
            case NONE: return new ItemStack(InitItems.D_NAIVE_DOOR_NONE.get());
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(VERTICAL_POS,WINDOW);
    }

}
