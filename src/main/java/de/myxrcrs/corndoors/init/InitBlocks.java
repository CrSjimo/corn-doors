package de.myxrcrs.corndoors.init;

import java.util.function.Supplier;

import de.myxrcrs.corndoors.CornDoors;
import de.myxrcrs.corndoors.blocks.*;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;

public enum InitBlocks {

    NAIVE_DOOR("naive_door",NaiveDoor::new),
    GLASS_DOOR("glass_door",GlassDoor::new),
    D_NAIVE_DOOR_EDGE("d_naive_door_edge", DNaiveDualDoorEdge::new),
    D_NAIVE_DOOR("d_naive_door",DNaiveDoor::new),
    STRETCH_GATE("stretch_gate",StretchGate::new),
    CHANNEL_INNER_DOOR_EDGE("channel_inner_door_edge",ChannelInnerDualDoor::new),
    CHANNEL_INNER_DOOR("channel_inner_door",ChannelInnerDoor::new);

    public static final void initialize(){
        // You NEVER need to know how this method works.
        InitBlocks __tmp = NAIVE_DOOR;
    }

    InitBlocks(String name, Supplier<? extends Block> sup){
        reg = CornDoors.BLOCKS.register(name, sup);
    }

    public final RegistryObject<Block> reg;

    public Block get(){
        return reg.get();
    }
}