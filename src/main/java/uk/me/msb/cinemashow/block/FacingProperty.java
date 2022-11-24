package uk.me.msb.cinemashow.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * An implementation of a state property for the state for `Facing`.
 * My understanding is that there is exactly one of these.
 */
public class FacingProperty extends Property<Facing> {

   /**
    * A map of all possible `Facing` values mapped by their string representation.
    * TODO: I don't see why this can't be static.
    */
   private final static Map<String, Facing> STATE_MAP = new HashMap<>();

   static {
      for (Direction horizontal : Facing.HORIZONTAL) {
         for (Direction vertical : Facing.VERTICAL) {
            Facing facing = new Facing(horizontal, vertical);
            STATE_MAP.put(facing.toString(), facing);
         }
      }
   }

   public static FacingProperty create() {
      return new FacingProperty();
   }

   protected FacingProperty() {
      // TODO "facing" is already used as a property name - I don't believe this is a problem.
      super("facing", Facing.class);
   }

   /**
    * @return a collection of all possible `Facing` values
    */
   public Collection<Facing> getPossibleValues() {
      return FacingProperty.STATE_MAP.values();
   }

   /**
    * @param facing a facing state string representation
    * @return a facing object matching the string representation
    */
   public Optional<Facing> getValue(String facing) {
      return Optional.ofNullable(FacingProperty.STATE_MAP.get(facing));
   }

   /**
    * @param facing a facing object
    * @return the object's string representation
    */
   public String getName(Facing facing) {
      return facing.toString();
   }
}