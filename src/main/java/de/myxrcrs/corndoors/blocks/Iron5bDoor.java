package de.myxrcrs.corndoors.blocks;

import de.myxrcrs.corndoors.init.InitBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;

public class Iron5bDoor extends AbstractDoorWithDualDoorEdge {
    public static Property<Integer> HORIZONTAL_POS = createHorizontalPosProperty(1);
    public static Property<Integer> VERTICAL_POS = createVerticalPosProperty(5);

    public Iron5bDoor(){
        super(Properties.create(Material.IRON), HORIZONTAL_POS, VERTICAL_POS, (AbstractDualDoorEdge) InitBlocks.IRON_5B_DOOR_EDGE.get());
        this.setDefaultState(this.getDefaultState()
            .with(HORIZONTAL_POS,0)
            .with(VERTICAL_POS,0));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HORIZONTAL_POS,VERTICAL_POS);
    }
}
