package uk.me.msb.cinemashow.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Property;
import uk.me.msb.cinemashow.ShowProperties;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * An implementation of a property for the state of a screen block.
 * My understanding is that there is exactly one of these.
 */
public class ScreenStateProperty extends Property<ScreenState> {

   /**
    * A map of all possible `ScreenState` values mapped by their string representation.
    * TODO: I don't see why this can't be static.
    */
   private final static Map<String, ScreenState> STATE_MAP = new HashMap<>();

   static {
      for (int x = 0; x < ShowProperties.BLOCKS_X_MAX; x ++) {
         for (int y = 0; y < ShowProperties.BLOCKS_Y_MAX; y ++) {
            for (Direction horizontal : Facing.HORIZONTAL) {
               for (Direction vertical : Facing.VERTICAL) {
                  ScreenState screenState = new ScreenState(x, y, horizontal, vertical);
                  STATE_MAP.put(screenState.toString(), screenState);
               }
            }
         }
      }
   }

   public static ScreenStateProperty create() {
      return new ScreenStateProperty();
   }

   protected ScreenStateProperty() {
      super("cinema", ScreenState.class);
   }

   /**
    * @return a collection of all possible `ScreenState` values
    */
   public Collection<ScreenState> getPossibleValues() {
      return ScreenStateProperty.STATE_MAP.values();
   }

   /**
    * @param screenState a screen state string representation
    * @return a screen state object matching the string representation
    */
   public Optional<ScreenState> getValue(String screenState) {
      return Optional.ofNullable(ScreenStateProperty.STATE_MAP.get(screenState));
   }

   /**
    * @param screenState a screen state object
    * @return the object's string representation
    */
   public String getName(ScreenState screenState) {
      return screenState.toString();
   }
}