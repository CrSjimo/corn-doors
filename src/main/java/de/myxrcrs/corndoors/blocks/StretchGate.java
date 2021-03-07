package de.myxrcrs.corndoors.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StretchGate extends AbstractStretchDoor {

    public enum HorizontalModelType implements IStringSerializable{
        SIDE("side"),
        NORMAL("normal");
        protected String name;
        HorizontalModelType(String name){
            this.name=name;
        }
        public String getString(){
            return name;
        }
        public String toString(){
            return name;
        }
    }

    public enum VerticalModelType implements IStringSerializable{
        TOP("top"),
        BODY("body"),
        BOTTOM("bottom");
        protected String name;
        VerticalModelType(String name){
            this.name=name;
        }
        public String getString(){
            return name;
        }
        public String toString(){
            return name;
        }
    }

    public static final Property<Integer> HORIZONTAL_POS = createHorizontalPosProperty(22);
    public static final Property<Integer> VERTICAL_POS = createVerticalPosProperty(3);
    public static final EnumProperty<HorizontalModelType> HORIZONTAL_MODEL_TYPE = EnumProperty.create("h_type", HorizontalModelType.class);
    public static final EnumProperty<VerticalModelType> VERTICAL_MODEL_TYPE = EnumProperty.create("v_type", VerticalModelType.class);
    public StretchGate(){
        super(Properties.create(Material.IRON),HORIZONTAL_POS,VERTICAL_POS,8,true,6);
        this.setDefaultState(this.getDefaultState()
            .with(HORIZONTAL_POS,0)
            .with(VERTICAL_POS,0)
            .with(HORIZONTAL_MODEL_TYPE, HorizontalModelType.NORMAL)
            .with(VERTICAL_MODEL_TYPE, VerticalModelType.BODY));
        
    }

    @Override
    public BlockState onWillSetNewState(World world,BlockState state,BlockPos pos){
        if(state.getBlock()!=this)return state;
        if(state.get(HORIZONTAL_POS)==(state.get(IS_OPENED)?fixedPartSize:getWidth(state))-1){
            state = state.with(HORIZONTAL_MODEL_TYPE, HorizontalModelType.SIDE);
        }else{
            state = state.with(HORIZONTAL_MODEL_TYPE, HorizontalModelType.NORMAL);
        }
        if(state.get(VERTICAL_POS)==0){
            state = state.with(VERTICAL_MODEL_TYPE, VerticalModelType.BOTTOM);
        }else if(state.get(VERTICAL_POS)==getHeight(state)-1){
            state = state.with(VERTICAL_MODEL_TYPE, VerticalModelType.TOP);
        }else{
            state = state.with(VERTICAL_MODEL_TYPE,VerticalModelType.BODY);
        }
        return state;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HORIZONTAL_POS,VERTICAL_POS,HORIZONTAL_MODEL_TYPE,VERTICAL_MODEL_TYPE);
    }
}
