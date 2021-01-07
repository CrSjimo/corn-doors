package de.myxrcrs.util;

import javax.annotation.Nullable;

import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class RotateTarget {
    public final Direction facing;
    public final BlockPos pos;
    public final DoorHingeSide side;

    public RotateTarget(Direction facing, BlockPos pos, @Nullable DoorHingeSide side){
        this.facing = facing;
        this.pos = pos;
        this.side = side;
    }
}
