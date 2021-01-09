package de.myxrcrs.corndoors.blocks;

import javax.annotation.Nullable;

import de.myxrcrs.util.Matrix;
import de.myxrcrs.util.RotateTarget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractDualDoorEdge extends AbstractTemplateDoor implements IRotateDoor {

    public static EnumProperty<Direction> FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static EnumProperty<DualDoorEdgePart> PART = EnumProperty.create("part", DualDoorEdgePart.class);

    public AbstractDualDoorEdge(Properties props, AbstractDoor correspondingDoorBlock){
        super(props);
        this.correspondingDoorBlock = correspondingDoorBlock;
    }

    protected AbstractDoor correspondingDoorBlock;
    public AbstractDoor getCorrespondingDoorBlock(){
        return correspondingDoorBlock;
    }

    public BlockPos getNeighborDoorPos(BlockPos pos, BlockState state, DoorHingeSide side)throws Exception{
        double[][] a = {{pos.getX(),pos.getY()}};
        double[][] u = Matrix.horizontalDirectionToMatrix(state.get(FACING));
        double[][] v = Matrix.getHingeVector(u, side);
        double[][] b = Matrix.add(a, v);
        return new BlockPos(b[0][0],pos.getY(),b[0][1]);
    }

    @Nullable
    public BlockState addPartTo(BlockState state, DualDoorEdgePart part){
        if(state.getBlock()==this){
            return state.with(PART, state.get(PART).add(part));
        }else if(state.getMaterial()==Material.AIR){
            return this.getDefaultState().with(PART, part);
        }else{
            return null;
        }
    }

    @Nullable
    public BlockState removePartFrom(BlockState state, DualDoorEdgePart part){
        if(state.getBlock()!=this)return null;
        @Nullable DualDoorEdgePart newPart = state.get(PART).remove(part);
        if(newPart == null){
            return Blocks.AIR.getDefaultState();
        }else{
            return state.with(PART, newPart);
        }
    }

    public boolean toggleDoorPos(World world, BlockPos pos, BlockState state, RotateTarget rotateTarget){
        DualDoorEdgePart part = DualDoorEdgePart.fromDoorHingeSide(rotateTarget.side);
        return world.setBlockState(rotateTarget.pos, addPartTo(state, part).with(FACING, rotateTarget.facing))
            && world.setBlockState(pos, removePartFrom(world.getBlockState(pos),part.getComplement()));
    }


}
