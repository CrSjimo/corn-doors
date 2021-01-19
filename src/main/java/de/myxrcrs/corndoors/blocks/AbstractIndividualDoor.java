package de.myxrcrs.corndoors.blocks;

import de.myxrcrs.corndoors.util.DoorRange;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public abstract class AbstractIndividualDoor extends AbstractTemplateDoor {
    
    public final Property<Integer> HORIZONTAL_POS;
    public final Property<Integer> VERTICAL_POS;
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;

    public AbstractIndividualDoor(Properties props, Property<Integer> HORIZONTAL_POS, Property<Integer> VERTICAL_POS, double thickness) {
        super(props, thickness);
        this.HORIZONTAL_POS = HORIZONTAL_POS;
        this.VERTICAL_POS = VERTICAL_POS;
    }

    @Override
    public int getWidth(BlockState state){
        return getSize(HORIZONTAL_POS);
    }

    @Override
    public int getHeight(BlockState state){
        return getSize(VERTICAL_POS);
    }

    public BlockState setHorizontalPos(BlockState state,int pos){
        return state.with(HORIZONTAL_POS,pos);
    }

    public BlockState setVerticalPos(BlockState state,int pos){
        return state.with(VERTICAL_POS,pos);
    }

    @Override
    public void fillRange(World world, DoorRange range, BlockState stateTemplate, BlockItemUseContext context){
        range.iterateRange((x,y,z)->{
            int horizontalPos = Math.abs(x-range.getFrom().getX())+Math.abs(z-range.getFrom().getZ());
            int verticalPos = y-range.getFrom().getY();
            world.setBlockState(new BlockPos(x,y,z), setVerticalPos(setHorizontalPos(stateTemplate, horizontalPos),verticalPos));
        });
    }

    @Override
    public void onPlaced(BlockItemUseContext context, BlockState stateTemplate){
        Direction facing = context.getPlacementHorizontalFacing().getOpposite();
        DoorRange leftHingeRange = getDoorRange(facing, context.getPos(), DoorHingeSide.LEFT, getWidth(stateTemplate), getHeight(stateTemplate), 0, 0);
        DoorRange rightHingeRange = getDoorRange(facing, context.getPos(), DoorHingeSide.RIGHT, getWidth(stateTemplate), getHeight(stateTemplate), 0, 0);
        boolean canLeftHinge = canFillRange(context.getWorld(), leftHingeRange);
        if(canLeftHinge){
            fillRange(
                context.getWorld(),
                leftHingeRange,
                stateTemplate
                    .with(FACING, facing)
                    .with(HINGE, DoorHingeSide.LEFT),
                context
            );
            return;
        }
        boolean canRightHinge = canFillRange(context.getWorld(), rightHingeRange);
        if(canRightHinge){
            fillRange(
                context.getWorld(),
                rightHingeRange,
                stateTemplate
                    .with(FACING, facing)
                    .with(HINGE, DoorHingeSide.RIGHT),
                context
            );
            return;
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(handIn == Hand.OFF_HAND)return ActionResultType.PASS;
        try{
            LOGGER.debug(AbstractDualDoorEdge.getSideFromHit(state, pos, hit.getHitVec()));
            if(toggleDoor(worldIn, pos, state, state.get(HINGE))){
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
}
