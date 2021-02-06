package de.myxrcrs.corndoors.blocks;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;

public interface INaiveDoor {

    public static final EnumProperty<DoorWindowType> WINDOW = EnumProperty.create("window", DoorWindowType.class);
    /**
     * Windows types of Corn Doors.
     */
    public static enum DoorWindowType implements IStringSerializable {
        
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

}
