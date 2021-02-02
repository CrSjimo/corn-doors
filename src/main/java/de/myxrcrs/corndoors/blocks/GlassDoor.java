package de.myxrcrs.corndoors.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;

public class GlassDoor extends AbstractDoor {
    
    public static final Property<Integer> HORIZONTAL_POS = createHorizontalPosProperty(2);
    public static final Property<Integer> VERTICAL_POS = createVerticalPosProperty(5);
    
    public GlassDoor(){
        super(Properties.create(Material.IRON).notSolid(),HORIZONTAL_POS,VERTICAL_POS,3,true);
        this.setDefaultState(this.getDefaultState()
            .with(HORIZONTAL_POS,0)
            .with(VERTICAL_POS,0));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HORIZONTAL_POS,VERTICAL_POS);
    }
}
