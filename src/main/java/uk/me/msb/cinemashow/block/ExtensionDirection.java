package uk.me.msb.cinemashow.block;

import net.minecraft.core.Direction;

import java.util.HashMap;
import java.util.Map;

/**
 * This record represents the valid directions in which a placed show block can be extended. When a block of show A is
 * placed next to an existing show A block, it's `Facing` is checked against EXTENSIONS and if the direction of the new
 * block matches either `x` or `y` that block is considered part of the screen under construction.
 *
 * @param x The direction that represents a valid extension of the screen in it's x direction.
 * @param y The direction that represents a valid extension of the screen in it's y direction.
 */
public record ExtensionDirection(Direction x, Direction y) {

    /**
     * A map of all possible `Facing` values and their valid extension directions.
     */
    private final static Map<Facing, ExtensionDirection> EXTENSIONS = new HashMap<>();

    static {
        EXTENSIONS.put(
                Facing.NORTH, new ExtensionDirection(Direction.WEST, Direction.UP)
        );
        EXTENSIONS.put(
                Facing.EAST, new ExtensionDirection(Direction.NORTH, Direction.UP)
        );
        EXTENSIONS.put(
                Facing.SOUTH, new ExtensionDirection(Direction.EAST, Direction.UP)
        );
        EXTENSIONS.put(
                Facing.WEST, new ExtensionDirection(Direction.SOUTH, Direction.UP)
        );
        EXTENSIONS.put(
                Facing.NORTH_DOWN, new ExtensionDirection(Direction.WEST, Direction.NORTH)
        );
        EXTENSIONS.put(
                Facing.EAST_DOWN, new ExtensionDirection(Direction.NORTH, Direction.EAST)
        );
        EXTENSIONS.put(
                Facing.SOUTH_DOWN, new ExtensionDirection(Direction.EAST, Direction.SOUTH)
        );
        EXTENSIONS.put(
                Facing.WEST_DOWN, new ExtensionDirection(Direction.SOUTH, Direction.WEST)
        );
        EXTENSIONS.put(
                Facing.NORTH_UP, new ExtensionDirection(Direction.WEST, Direction.SOUTH)
        );
        EXTENSIONS.put(
                Facing.EAST_UP, new ExtensionDirection(Direction.NORTH, Direction.WEST)
        );
        EXTENSIONS.put(
                Facing.SOUTH_UP, new ExtensionDirection(Direction.EAST, Direction.NORTH)
        );
        EXTENSIONS.put(
                Facing.WEST_UP, new ExtensionDirection(Direction.SOUTH, Direction.EAST)
        );
    }

    public static ExtensionDirection get(Facing facing) {
        return EXTENSIONS.get(facing);
    }
}
