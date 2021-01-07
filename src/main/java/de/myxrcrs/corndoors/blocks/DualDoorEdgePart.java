package de.myxrcrs.corndoors.blocks;

import javax.annotation.Nullable;

import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.IStringSerializable;

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

    public DualDoorEdgePart add(DualDoorEdgePart part){
        if(part==ALL)return ALL;
        switch(this){
            case ALL: return ALL;
            case LEFT: return part==RIGHT?ALL:LEFT;
            case RIGHT: return part==LEFT?ALL:RIGHT;
            default: return this;
        }
    }

    @Nullable
    public DualDoorEdgePart getComplement(){
        switch(this){
            case ALL: return null;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            default: return this;
        }
    }

    @Nullable
    public DualDoorEdgePart remove(DualDoorEdgePart part){
        if(part==ALL)return null;
        switch(this){
            case ALL: return part==LEFT?RIGHT:LEFT;
            case LEFT: return part==RIGHT?LEFT:null;
            case RIGHT: return part==LEFT?RIGHT:null;
            default: return this;
        }
    }

    public static DualDoorEdgePart fromDoorHingeSide(DoorHingeSide side){
        if(side == DoorHingeSide.LEFT)return LEFT;
        else return RIGHT;
    }

}
