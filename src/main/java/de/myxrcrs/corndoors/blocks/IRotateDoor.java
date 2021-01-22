package de.myxrcrs.corndoors.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRotateDoor {
    /**
     * Set the original position and the target position to new blockstates according to rotation target.
     * @return Whether successful.
     */
    public boolean toggleDoorPos(World world, BlockPos pos, BlockState state, RotateTarget rotateTarget);

    /**
     * Target of rotation.
     */
    public static class RotateTarget {

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

}
