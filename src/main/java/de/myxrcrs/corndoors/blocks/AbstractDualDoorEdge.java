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
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class AbstractDualDoorEdge extends AbstractTemplateDoor implements IRotateDoor {

    public static EnumProperty<DualDoorEdgePart> PART = EnumProperty.create("part", DualDoorEdgePart.class);

    public final Property<Integer> VERTICAL_POS;

    public final boolean rotateWithinHinge;

    public AbstractDualDoorEdge(Properties props, Property<Integer> VERTICAL_POS, boolean rotateWithinHinge,double thickness){
        super(props,thickness);
        this.rotateWithinHinge = rotateWithinHinge;
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

    @Override
    public int getWidth(BlockState state){
        return 1;
    }

    @Override
    public int getHeight(BlockState state){
        return getSize(VERTICAL_POS);
    }

    @Override
    public VoxelShape generateBoundaryBox(BlockState state, boolean isMiddle){
        Direction facing = state.get(FACING);
        if(state.get(PART)==DualDoorEdgePart.LEFT){
            switch(facing){
                case NORTH:
                default:
                    return isMiddle ? Block.makeCuboidShape(8, 0, 8-0.5*thickness, 16, 16, 8+0.5*thickness) : Block.makeCuboidShape(8, 0, 0, 16, 16, thickness);
                case SOUTH:
                    return isMiddle ? Block.makeCuboidShape(0, 0, 8-0.5*thickness, 8, 16, 8+0.5*thickness) : Block.makeCuboidShape(0, 0, 16-thickness, 8, 16, 16);
                case EAST:
                    return isMiddle ? Block.makeCuboidShape(8-0.5*thickness, 0, 8, 8+0.5*thickness, 16, 16) : Block.makeCuboidShape(16-thickness, 0, 8, 16, 16, 16);
                case WEST:
                    return isMiddle ? Block.makeCuboidShape(8-0.5*thickness, 0, 0, 8+0.5*thickness, 16, 8) : Block.makeCuboidShape(0, 0, 0, thickness, 16, 8);
            }
        }else if(state.get(PART)==DualDoorEdgePart.RIGHT){
            switch(facing){
                case NORTH:
                default:
                    return isMiddle ? Block.makeCuboidShape(0, 0, 8-0.5*thickness, 8, 16, 8+0.5*thickness) : Block.makeCuboidShape(0, 0, 0, 8, 16, thickness);
                case SOUTH:
                    return isMiddle ? Block.makeCuboidShape(8, 0, 8-0.5*thickness, 16, 16, 8+0.5*thickness) : Block.makeCuboidShape(8, 0, 16-thickness, 16, 16, 16);
                case EAST:
                    return isMiddle ? Block.makeCuboidShape(8-0.5*thickness, 0, 0, 8+0.5*thickness, 16, 8) : Block.makeCuboidShape(16-thickness, 0, 0, 16, 16, 8);
                case WEST:
                    return isMiddle ? Block.makeCuboidShape(8-0.5*thickness, 0, 8, 8+0.5*thickness, 16, 16) : Block.makeCuboidShape(0, 0, 8, thickness, 16, 16);
            }
        }else{
            return super.generateBoundaryBox(state, isMiddle);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return generateBoundaryBox(state,rotateWithinHinge);
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
        if(side==null){
            BlockPos neighborPosL = getNeighborDoorPos(pos, state, DoorHingeSide.LEFT);
            BlockState neighborStateL = world.getBlockState(neighborPosL);
            Block neighborBlockL = neighborStateL.getBlock();
            if(neighborBlockL == correspondingDoorBlock){
                side=DoorHingeSide.LEFT;
            }else{
                side=DoorHingeSide.RIGHT;
            }
        }
        BlockPos neighborPos = getNeighborDoorPos(pos, state, side);
        BlockState neighborState = world.getBlockState(neighborPos);
        Block neighborBlock = neighborState.getBlock();
        LOGGER.debug("ngbpos = "+neighborPos);
        LOGGER.debug("ngbclass = "+neighborBlock.getClass().getName());
        if(neighborBlock == correspondingDoorBlock){
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
        DualDoorEdgePart targetStatePart = addPartTo(world.getBlockState(rotateTarget.pos),part).get(PART);
        BlockState newState = state.with(FACING, rotateTarget.facing).with(PART,targetStatePart);
        newState = onWillSetNewState(world, newState, rotateTarget.pos);
        return world.setBlockState(rotateTarget.pos, newState)
            && world.setBlockState(pos, removePartFrom(state,part));
    }

    @Nullable
    public static DoorHingeSide getSideFromHit(BlockState state, BlockPos pos, Vec3d hitVec){
        Vec3d vec = hitVec.subtract(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5);
        double[][] v = Matrix.getHingeVector(Matrix.horizontalDirectionToMatrix(state.get(FACING)),DoorHingeSide.LEFT);
        double t = v[0][0]*vec.getX()+v[0][1]*vec.getZ();
        LOGGER.debug("hitvec = "+vec);
        // LOGGER.debug("["+v[0][0]+' '+v[0][1]+"]");
        LOGGER.debug("hitflag = "+t);
        return t>0?DoorHingeSide.LEFT:t<0?DoorHingeSide.RIGHT:null;
        
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(handIn == Hand.OFF_HAND)return ActionResultType.PASS;
        try{
            LOGGER.debug("hitside = "+getSideFromHit(state, pos, hit.getHitVec()));
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
            LOGGER.debug(neighborBlock.getClass().getName()+" "+side);
            ((AbstractDoor)neighborBlock).onHarvested(world, neighborState, neighborPos);
        }
    }

    @Override
    public void onHarvested(World world, BlockState state, BlockPos pos){
        triggerNeighborHarvest(world, state, pos, DoorHingeSide.LEFT);
        triggerNeighborHarvest(world, state, pos, DoorHingeSide.RIGHT);
    }

    @Override
    public void fillRange(World world, DoorRange range, BlockState stateTemplate, BlockItemUseContext context){
        range.iterateRange((x,y,z)->{
            int verticalPos = y-range.getFrom().getY();
            BlockPos pos = new BlockPos(x,y,z);
            BlockState state = stateTemplate.with(VERTICAL_POS, verticalPos);
            world.setBlockState(pos, onWillSetNewState(world, state, pos));
        });
    }

    @Override
    public void onPlaced(BlockItemUseContext context, BlockState stateTemplate){
        Direction facing = context.getPlacementHorizontalFacing().getOpposite();
        BlockPos pos = context.getPos();
        int width = correspondingDoorBlock.getWidth(null);
        int height = correspondingDoorBlock.getHeight(null);
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
                .with(AbstractDoor.HINGE, DoorHingeSide.LEFT),
                context
            );
            correspondingDoorBlock.fillRange(context.getWorld(), rangeRight, correspondingDoorBlock.getDefaultState()
                .with(AbstractDoor.FACING, facing)
                .with(AbstractDoor.HINGE, DoorHingeSide.RIGHT),
                context
            );
            fillRange(context.getWorld(), rangeMiddle, stateTemplate.with(FACING,facing),context);
        }
    }


}
