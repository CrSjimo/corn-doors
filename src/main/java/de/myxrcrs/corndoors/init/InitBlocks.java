package de.myxrcrs.corndoors.init;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import de.myxrcrs.corndoors.CornDoors;
import de.myxrcrs.corndoors.blocks.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;

public enum InitBlocks {

    NAIVE_PANEL("naive_panel",NaivePanel::new),
    GLASS_PANEL("glass_panel",GlassPanel::new,RenderTypeSymbols.CUTOUT),
    CORN_IRON_PANEL("corn_iron_panel",CornIronPanel::new,RenderTypeSymbols.CUTOUT),

    NAIVE_DOOR("naive_door",NaiveDoor::new,RenderTypeSymbols.TRANSLUCENT),
    GLASS_DOOR("glass_door",GlassDoor::new,RenderTypeSymbols.CUTOUT),
    D_NAIVE_DOOR_EDGE("d_naive_door_edge", DNaiveDualDoorEdge::new,RenderTypeSymbols.TRANSLUCENT),
    D_NAIVE_DOOR("d_naive_door",DNaiveDoor::new,RenderTypeSymbols.TRANSLUCENT),
    STRETCH_GATE("stretch_gate",StretchGate::new,RenderTypeSymbols.CUTOUT),
    CHANNEL_INNER_DOOR_EDGE("channel_inner_door_edge",ChannelInnerDualDoor::new,RenderTypeSymbols.CUTOUT),
    CHANNEL_INNER_DOOR("channel_inner_door",ChannelInnerDoor::new,RenderTypeSymbols.CUTOUT),
    IRON_4B_DOOR_EDGE("iron_4b_door_edge",Iron4bDualDoor::new),
    IRON_4B_DOOR("iron_4b_door",Iron4bDoor::new),
    IRON_5B_DOOR_EDGE("iron_5b_door_edge",Iron5bDualDoor::new),
    IRON_5B_DOOR("iron_5b_door",Iron5bDoor::new),
    CHANNEL_OUTER_DOOR_EDGE("channel_outer_door_edge",ChannelOuterDualDoor::new,RenderTypeSymbols.CUTOUT),
    CHANNEL_OUTER_DOOR("channel_outer_door",ChannelOuterDoor::new,RenderTypeSymbols.CUTOUT);
    

    public static final void initialize(){
        // You NEVER need to know how this method works.
        InitBlocks __tmp = NAIVE_DOOR;
    }

    public static enum RenderTypeSymbols{

        CUTOUT,
        TRANSLUCENT;
        
        @OnlyIn(Dist.CLIENT)
        public RenderType getRenderType(){
            switch(this){
                case CUTOUT: return RenderType.getCutout();
                case TRANSLUCENT: return RenderType.getTranslucent();
                default: return RenderType.getSolid();
            }
        }
    }

    InitBlocks(String name, Supplier<? extends Block> sup){
        this(name, sup, null);
    }
    InitBlocks(String name, Supplier<? extends Block> sup, RenderTypeSymbols type){
        reg = CornDoors.BLOCKS.register(name, sup);
        renderType = type;
    }

    public final RegistryObject<Block> reg;
    
    @Nullable final RenderTypeSymbols renderType;

    public Block get(){
        return reg.get();
    }
}