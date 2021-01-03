package de.myxrcrs.corndoors.blocks;

import net.minecraft.util.IStringSerializable;

public enum DoorWindowType implements IStringSerializable {
    
    GLASS("glass"),
    FILM("film"),
    NONE("none");

    DoorWindowType(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        return name;
    }

    public String name;
}
