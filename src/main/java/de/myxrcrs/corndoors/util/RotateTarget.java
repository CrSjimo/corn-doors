package de.myxrcrs.corndoors.util;

import javax.annotation.Nullable;

import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

/**
 * Target of rotation.
 */
public class RotateTarget {

    /**
     * The facing direction of target blockstate.
     */
    public final Direction facing;

    /**
     * The target position.
     */
    public final BlockPos pos;

    /**
     * The target hinge side.
     */
    public final DoorHingeSide side;

    public RotateTarget(Direction facing, BlockPos pos, @Nullable DoorHingeSide side){
        this.facing = facing;
        this.pos = pos;
        this.side = side;
    }
}
