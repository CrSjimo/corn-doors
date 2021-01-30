package de.myxrcrs.corndoors.init;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import de.myxrcrs.corndoors.CornDoors;
import de.myxrcrs.corndoors.blocks.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fml.RegistryObject;

public enum InitBlocks {

    NAIVE_DOOR("naive_door",NaiveDoor::new,RenderType.getTranslucent()),
    GLASS_DOOR("glass_door",GlassDoor::new,RenderType.getCutout()),
    D_NAIVE_DOOR_EDGE("d_naive_door_edge", DNaiveDualDoorEdge::new,RenderType.getTranslucent()),
    D_NAIVE_DOOR("d_naive_door",DNaiveDoor::new,RenderType.getTranslucent()),
    STRETCH_GATE("stretch_gate",StretchGate::new,RenderType.getCutout()),
    CHANNEL_INNER_DOOR_EDGE("channel_inner_door_edge",ChannelInnerDualDoor::new,RenderType.getCutout()),
    CHANNEL_INNER_DOOR("channel_inner_door",ChannelInnerDoor::new,RenderType.getCutout());

    public static final void initialize(){
        // You NEVER need to know how this method works.
        InitBlocks __tmp = NAIVE_DOOR;
    }

    InitBlocks(String name, Supplier<? extends Block> sup){
        this(name, sup, null);
    }
    InitBlocks(String name, Supplier<? extends Block> sup, RenderType type){
        reg = CornDoors.BLOCKS.register(name, sup);
        renderType = type;
    }

    public final RegistryObject<Block> reg;
    
    @Nullable final RenderType renderType;

    public Block get(){
        return reg.get();
    }
}