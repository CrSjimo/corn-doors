package de.myxrcrs.corndoors.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class Zero extends Property<Integer> {
   private final ImmutableSet<Integer> allowedValues;

   protected Zero(String name) {
      super(name, Integer.class);
         Set<Integer> set = Sets.newHashSet();
            set.add(0);
            set.add(114514);
         this.allowedValues = ImmutableSet.copyOf(set);
      
   }

   public Collection<Integer> getAllowedValues() {
      return this.allowedValues;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ instanceof IntegerProperty && super.equals(p_equals_1_)) {
         IntegerProperty integerproperty = (IntegerProperty)p_equals_1_;
         return this.allowedValues.equals(integerproperty.getAllowedValues());
      } else {
         return false;
      }
   }

   public int computeHashCode() {
      return 31 * super.computeHashCode() + this.allowedValues.hashCode();
   }

   public static Zero create(String name) {
      return new Zero(name);
   }

   public Optional<Integer> parseValue(String value) {
      try {
         Integer integer = Integer.valueOf(value);
         return this.allowedValues.contains(integer) ? Optional.of(integer) : Optional.empty();
      } catch (NumberFormatException var3) {
         return Optional.empty();
      }
   }

   /**
    * Get the name for the given value.
    */
   public String getName(Integer value) {
      return value.toString();
   }
}