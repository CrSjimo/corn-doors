package de.myxrcrs.corndoors.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

public class NaivePanel extends AbstractPanel {

    public enum TextureDirection implements IStringSerializable{
        HORIZONTAL("horizontal"),
        VERTICAL("vertical");
        private final String name;
        TextureDirection(String name){
            this.name = name;
        }
        public String getName(){return name;}
        public String toString(){return name;}
        public static TextureDirection fromFace(Direction face){
            if(face==Direction.UP||face==Direction.DOWN){
                return HORIZONTAL;
            }else{
                return VERTICAL;
            }
        }
    }

    public static final EnumProperty<TextureDirection> TEXTURE_DIRECTION = EnumProperty.create("texture_direction", TextureDirection.class);

    public NaivePanel(){
        super(Properties.create(Material.WOOD),3,false);
    }

    @Override
    public BlockState onPlacedThenSetState(BlockItemUseContext context,BlockState stateTemplate){
        return super.onPlacedThenSetState(context,stateTemplate.with(TEXTURE_DIRECTION, TextureDirection.fromFace(context.getFace())));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(TEXTURE_DIRECTION);
    }
}
