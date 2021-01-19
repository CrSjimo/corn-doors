package de.myxrcrs.corndoors.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.Direction;

public class StretchGate extends AbstractStretchDoor {
    public static final Property<Integer> HORIZONTAL_POS = createHorizontalPosProperty(10);
    public static final Property<Integer> VERTICAL_POS = createVerticalPosProperty(3);
    public StretchGate(){
        super(Properties.create(Material.IRON),HORIZONTAL_POS,VERTICAL_POS,false,3,3);
        this.setDefaultState(this.getDefaultState()
            .with(IS_OPENED, false)
            .with(FACING, Direction.NORTH)
            .with(HORIZONTAL_POS,0)
            .with(VERTICAL_POS,0)
            .with(HINGE,DoorHingeSide.LEFT));
    }
    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(IS_OPENED,FACING,VERTICAL_POS,HORIZONTAL_POS,HINGE);
    }
}
