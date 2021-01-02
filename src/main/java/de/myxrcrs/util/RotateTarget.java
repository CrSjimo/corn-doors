package de.myxrcrs.util;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class RotateTarget {
    public final Direction facing;
    public final BlockPos pos;
    public RotateTarget(Direction facing, BlockPos pos){
        this.facing = facing;
        this.pos = pos;
    }
}
