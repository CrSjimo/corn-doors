package de.myxrcrs.corndoors.blocks;

import javax.annotation.Nullable;

import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.IStringSerializable;

/**
 * Enum of the part property in dual door edge blockstates.
 */
public enum DualDoorEdgePart implements IStringSerializable {
    LEFT("left"),
    RIGHT("right"),
    ALL("all");

    DualDoorEdgePart(String name){
        this.name = name;
    }

    public String name;

    public String getName(){
        return name;
    }

    public String toString(){
        return getName();
    }

    private static int getFlag(@Nullable DualDoorEdgePart part){
        switch(part){
            case ALL: return 3;
            case LEFT: return 2;
            case RIGHT: return 1;
            default: return 0;
        }
    }

    @Nullable
    private static DualDoorEdgePart fromFlag(int flag){
        if((flag&1)!=0&&(flag&2)!=0)return ALL;
        if((flag&1)!=0)return RIGHT;
        if((flag&2)!=0)return LEFT;
        return null;
    }

    public DualDoorEdgePart add(DualDoorEdgePart part){
        int p1 = getFlag(this);
        int p2 = getFlag(part);
        return fromFlag(p1|p2);
    }

    @Nullable
    public DualDoorEdgePart getComplement(){
        return fromFlag(~getFlag(this));
    }

    public boolean include(DualDoorEdgePart part){
        int p1 = getFlag(this);
        int p2 = getFlag(part);
        return (p1|p2)==p1;
    }

    @Nullable
    public DualDoorEdgePart remove(DualDoorEdgePart part){
        int p1 = getFlag(this);
        int p2 = getFlag(part);
        if((p1|p2)!=p1){
            throw new IllegalArgumentException("Part not included.");
        }
        return fromFlag(p1-p2);
    }


    public static DualDoorEdgePart fromDoorHingeSide(DoorHingeSide side){
        if(side == DoorHingeSide.LEFT)return LEFT;
        else return RIGHT;
    }

}
