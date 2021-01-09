package de.myxrcrs.corndoors.blocks;

import de.myxrcrs.util.RotateTarget;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRotateDoor {
    public boolean toggleDoorPos(World world, BlockPos pos, BlockState state, RotateTarget rotateTarget);
}
