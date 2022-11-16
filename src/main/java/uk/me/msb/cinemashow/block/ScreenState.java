package uk.me.msb.cinemashow.block;

import net.minecraft.core.Direction;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A possible value of the state of a screen block.
 */
public class ScreenState implements Comparable<ScreenState> {

    /**
     * The direction in which the screen is facing.
     */
    public final Facing facing;

    /**
     * The x position of the screen's show tile.
     */
    public final int x;

    /**
     * The y position of the screen's show tile.
     */
    public final int y;

    public ScreenState(int x, int y, @Nonnull Direction horizontal, Direction vertical) {
        this.facing = new Facing(horizontal, vertical);
        this.x = x;
        this.y = y;
    }

    public ScreenState(int x, int y, Facing facing) {
        this.facing = facing;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("%d_%d_%s", x, y, facing);
    }

    @Override
    public int compareTo(ScreenState o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScreenState that)) return false;
        return x == that.x && y == that.y && facing.equals(that.facing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facing, x, y);
    }
}
