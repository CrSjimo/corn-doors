package de.myxrcrs.corndoors.util;

import net.minecraft.util.math.BlockPos;

/**
 * The range where a door locates.
 */
public class DoorRange {

    /**
     * The position of the block in the door whose bottom is adjacent to the ground and side is the hinge.
     */
    protected final BlockPos from;

    /**
     * The position of the block on the other end of diagonal.
     */
    protected final BlockPos to;

    /**
     * The hinge vector.
     * @see {@link de.myxrcrs.corndoors.util.Matrix#getHingeVector(double[][], net.minecraft.state.properties.DoorHingeSide)}
     */
    protected final double[][] dirVec;
    
    protected DoorRange(BlockPos from, BlockPos to, double[][] dirVec){
        this.from = from;
        this.to = to;
        this.dirVec = dirVec;
    }

    public static DoorRange of(BlockPos from, BlockPos to, double[][] dirVec){
        return new DoorRange(from, to, dirVec);
    }

    public BlockPos getFrom(){
        return this.from;
    }

    public BlockPos getTo(){
        return this.to;
    }

    public double[][] getDirVec(){
        return this.dirVec;
    }

    public boolean iterateRange(RangeIterationConsumer func){
        return iterateRange((x,y,z)->{
            func.iterate(x, y, z);
            return true;
        });
    }

    public boolean iterateRange(BooleanRangeIterationConsumer func){
        boolean xOnce = false;
        for(int x = from.getX() ; x != to.getX() || (dirVec[0][0]==0&&!xOnce) ; x-=(int)dirVec[0][0]){
            for(int y = from.getY() ; y != to.getY() ; y++){
                boolean zOnce = false;
                for(int z = from.getZ(); z != to.getZ() || (dirVec[0][1]==0&&!zOnce) ; z-=(int)dirVec[0][1]){
                    if(!func.iterate(x, y, z))return false;
                    zOnce = true;
                }
            }
            xOnce = true;
        }
        return true;
    }
}
