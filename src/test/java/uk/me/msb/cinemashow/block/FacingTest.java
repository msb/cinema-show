package uk.me.msb.cinemashow.block;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests related to `Facing`
 */
public class FacingTest {
    /**
     * Tests/defines the behaviour of `Facing.north.getOpposite()`
     */
    @Test
    public void testGetOpposite() {
        Set<Facing> remaining = new HashSet<>(Arrays.stream(Facing.values()).toList());
        assertOppositeFacingEquals(remaining, Facing.SOUTH, Facing.NORTH);
        assertOppositeFacingEquals(remaining, Facing.WEST, Facing.EAST);
        assertOppositeFacingEquals(remaining, Facing.NORTH, Facing.SOUTH);
        assertOppositeFacingEquals(remaining, Facing.EAST, Facing.WEST);
        assertOppositeFacingEquals(remaining, Facing.SOUTH_UP, Facing.NORTH_DOWN);
        assertOppositeFacingEquals(remaining, Facing.WEST_UP, Facing.EAST_DOWN);
        assertOppositeFacingEquals(remaining, Facing.NORTH_UP, Facing.SOUTH_DOWN);
        assertOppositeFacingEquals(remaining, Facing.EAST_UP, Facing.WEST_DOWN);
        assertOppositeFacingEquals(remaining, Facing.SOUTH_DOWN, Facing.NORTH_UP);
        assertOppositeFacingEquals(remaining, Facing.WEST_DOWN, Facing.EAST_UP);
        assertOppositeFacingEquals(remaining, Facing.NORTH_DOWN, Facing.SOUTH_UP);
        assertOppositeFacingEquals(remaining, Facing.EAST_DOWN, Facing.WEST_UP);
        // check all the values have been tested
        Assertions.assertEquals(0, remaining.size());
    }

    /**
     * Helper method to assert that a `Facing` and it's opposite match.
     *
     * @param remaining the remaining facings
     * @param expected the expected facing
     * @param actual the actual opposite facing
     */
    public static void assertOppositeFacingEquals(Set<Facing> remaining, Facing expected, Facing actual) {
        remaining.remove(expected);
        Assertions.assertEquals(expected, actual.getOpposite());
    }
}
