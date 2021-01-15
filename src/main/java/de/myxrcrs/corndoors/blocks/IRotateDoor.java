package de.myxrcrs.corndoors.blocks;

import de.myxrcrs.corndoors.util.RotateTarget;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRotateDoor {
    /**
     * Set the original position and the target position to new blockstates according to rotation target.
     * @return Whether successful.
     */
    public boolean toggleDoorPos(World world, BlockPos pos, BlockState state, RotateTarget rotateTarget);
}
