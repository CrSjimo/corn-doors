package de.myxrcrs.corndoors.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;

public class ChannelInnerDualDoor extends AbstractDualDoorEdge {
    public static Property<Integer> VERTICAL_POS = createVerticalPosProperty(5);

    public ChannelInnerDualDoor(){
        super(Properties.create(Material.IRON), VERTICAL_POS, 3, true);
        this.setDefaultState(this.getDefaultState()
            .with(VERTICAL_POS, 0));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(VERTICAL_POS);
    }
}
