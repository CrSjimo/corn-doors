package de.myxrcrs.corndoors.blocks;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import de.myxrcrs.corndoors.util.DoorRange;
import de.myxrcrs.corndoors.util.Matrix;
import de.myxrcrs.corndoors.util.RotateTarget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class AbstractDualDoorEdge extends AbstractTemplateDoor implements IRotateDoor {

    public static EnumProperty<DualDoorEdgePart> PART = EnumProperty.create("part", DualDoorEdgePart.class);

    public final Property<Integer> VERTICAL_POS;

    public AbstractDualDoorEdge(Properties props, Property<Integer> VERTICAL_POS, double thickness){
        super(props,thickness);
        this.VERTICAL_POS = VERTICAL_POS;
    }

    @Nullable protected AbstractDoor correspondingDoorBlock = null;
    public AbstractDoor getCorrespondingDoorBlock(){
        return correspondingDoorBlock;
    }

    public void setCorrespondingDoorBlock(AbstractDoor door){
        if(correspondingDoorBlock==null){
            correspondingDoorBlock = door;
        }else{
            throw new UnsupportedOperationException("Corresponding door already set.");
        }
    }

    public BlockPos getNeighborDoorPos(BlockPos pos, BlockState state, DoorHingeSide side){
        double[][] a = {{pos.getX(),pos.getZ()}};
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

    @Override
    public boolean toggleDoor(World world, BlockPos pos, BlockState state, DoorHingeSide side){
        BlockPos neighborPos = getNeighborDoorPos(pos, state, side);
        BlockState neighborState = world.getBlockState(neighborPos);
        Block neighborBlock = neighborState.getBlock();
        LOGGER.info(neighborPos);
        LOGGER.info(neighborBlock.getClass().getName());
        if(neighborBlock == correspondingDoorBlock){
            LOGGER.info(neighborBlock.getClass().getName()+" matches");
            return ((AbstractDoor)neighborBlock).toggleDoor(world, neighborPos, neighborState, side);
        }else{
            onHarvested(world, state, pos);
            return false;
        }
    }

    @Override
    public boolean canTogglePos(World world, BlockState state, BlockPos target){
        BlockState targetState = world.getBlockState(target);
        if(targetState.getBlock()!=this){
            return super.canTogglePos(world, state, target);
        }else{
            return state.get(PART).getComplement() == targetState.get(PART);
        }
    }

    public boolean toggleDoorPos(World world, BlockPos pos, BlockState state, RotateTarget rotateTarget){
        DualDoorEdgePart part = DualDoorEdgePart.fromDoorHingeSide(rotateTarget.side);
        return world.setBlockState(rotateTarget.pos, state.with(FACING, rotateTarget.facing).with(PART,part))
            && world.setBlockState(pos, removePartFrom(state,part));
    }

    public static DoorHingeSide getSideFromHit(BlockState state, BlockPos pos, Vec3d hitVec){
        Vec3d vec = hitVec.subtract(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5);
        double[][] v = Matrix.getHingeVector(Matrix.horizontalDirectionToMatrix(state.get(FACING)),DoorHingeSide.LEFT);
        double t = v[0][0]*vec.getX()+v[0][1]*vec.getZ();
        // LOGGER.info(vec);
        // LOGGER.info("["+v[0][0]+' '+v[0][1]+"]");
        // LOGGER.info(t);
        return t>=0?DoorHingeSide.LEFT:DoorHingeSide.RIGHT;
        
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(handIn == Hand.OFF_HAND)return ActionResultType.PASS;
        try{
            LOGGER.info(getSideFromHit(state, pos, hit.getHitVec()));
            if(toggleDoor(worldIn, pos, state, getSideFromHit(state, pos, hit.getHitVec()))){
                worldIn.playEvent(player, getToggleSound(state), pos, 0);
                return ActionResultType.SUCCESS;
            }else{
                return ActionResultType.CONSUME;
            }
        }catch(Exception e){
            LOGGER.error(e);
            return ActionResultType.CONSUME;
        }
    }

    public void triggerNeighborHarvest(World world, BlockState state, BlockPos pos, DoorHingeSide side){
        BlockPos neighborPos = getNeighborDoorPos(pos, state, side);
        BlockState neighborState = world.getBlockState(neighborPos);
        Block neighborBlock = neighborState.getBlock();
        if(neighborBlock == correspondingDoorBlock){
            LOGGER.info(neighborBlock.getClass().getName()+" "+side);
            ((AbstractDoor)neighborBlock).onHarvested(world, neighborState, neighborPos);
        }
    }

    @Override
    public void onHarvested(World world, BlockState state, BlockPos pos){
        triggerNeighborHarvest(world, state, pos, DoorHingeSide.LEFT);
        triggerNeighborHarvest(world, state, pos, DoorHingeSide.RIGHT);
    }

    public void fillRange(World world, DoorRange range, BlockState stateTemplate){
        range.iterateRange((x,y,z)->{
            int verticalPos = y-range.getFrom().getY();
            world.setBlockState(new BlockPos(x,y,z), stateTemplate.with(VERTICAL_POS, verticalPos));
        });
    }

    @Override
    public void onPlaced(BlockItemUseContext context, BlockState stateTemplate){
        Direction facing = context.getPlacementHorizontalFacing().getOpposite();
        BlockPos pos = context.getPos();
        int width = correspondingDoorBlock.getWidth();
        int height = correspondingDoorBlock.getHeight();
        double[][] a1 = {{pos.getX(),pos.getZ()}};
        double[][] v = Matrix.getHingeVector(Matrix.horizontalDirectionToMatrix(facing), DoorHingeSide.RIGHT);
        double[][] a2 = Matrix.add(a1, Matrix.mul(v, width));
        double[][] a3 = Matrix.add(a2, Matrix.mul(v, width));
        DoorRange rangeLeft = getDoorRange(facing, pos, DoorHingeSide.LEFT, width ,height, 0, 0);
        DoorRange rangeMiddle = getDoorRange(facing, new BlockPos(a2[0][0],pos.getY(),a2[0][1]), DoorHingeSide.LEFT, 1 ,height, 0, 0);
        DoorRange rangeRight = getDoorRange(facing, new BlockPos(a3[0][0],pos.getY(),a3[0][1]), DoorHingeSide.RIGHT, width ,height, 0, 0);
        boolean canFill = canFillRange(context.getWorld(), rangeLeft) && canFillRange(context.getWorld(), rangeMiddle) && canFillRange(context.getWorld(), rangeRight);
        if(canFill){
            correspondingDoorBlock.fillRange(context.getWorld(), rangeLeft, correspondingDoorBlock.getDefaultState()
                .with(AbstractDoor.FACING, facing)
                .with(AbstractDoor.HINGE, DoorHingeSide.LEFT));
            correspondingDoorBlock.fillRange(context.getWorld(), rangeRight, correspondingDoorBlock.getDefaultState()
                .with(AbstractDoor.FACING, facing)
                .with(AbstractDoor.HINGE, DoorHingeSide.RIGHT));
            fillRange(context.getWorld(), rangeMiddle, stateTemplate);
        }
    }


}
