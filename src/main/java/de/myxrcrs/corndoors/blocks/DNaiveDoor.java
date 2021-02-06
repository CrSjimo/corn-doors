package de.myxrcrs.corndoors.blocks;

import de.myxrcrs.corndoors.init.InitBlocks;
import de.myxrcrs.corndoors.items.PropertiedBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.world.World;

public class DNaiveDoor extends AbstractDoorWithDualDoorEdge implements INaiveDoor {

    public static final Property<Integer> HORIZONTAL_POS = createHorizontalPosProperty(1);
    public static final Property<Integer> VERTICAL_POS = createVerticalPosProperty(4);
    
    public DNaiveDoor(){
        super(Block.Properties.create(Material.WOOD).notSolid(),HORIZONTAL_POS,VERTICAL_POS,(AbstractDualDoorEdge)InitBlocks.D_NAIVE_DOOR_EDGE.get());
        this.setDefaultState(this.getDefaultState()
            .with(HORIZONTAL_POS,0)
            .with(VERTICAL_POS,0)
            .with(WINDOW,DoorWindowType.GLASS));
    }

    @Override
    public void fillRange(World world, DoorRange range, BlockState stateTemplate, BlockItemUseContext context){
        DoorWindowType doorWindowType = ((PropertiedBlockItem<DoorWindowType>)context.getItem().getItem()).property;
        super.fillRange(world, range, stateTemplate.with(WINDOW, doorWindowType), context);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HORIZONTAL_POS,VERTICAL_POS,WINDOW);
    }

}
