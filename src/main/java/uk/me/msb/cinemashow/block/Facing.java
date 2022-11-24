package uk.me.msb.cinemashow.block;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Model representing the direction an entity or block is facing. Note that two directions are required because if an
 * entity/block is facing `UP` it will still have a `horizontal` orientation.
 */
public class Facing implements Comparable<Facing> {

    /**
     * All possible `horizontal` values.
     */
    public final static Direction[] HORIZONTAL = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    /**
     * All possible `vertical` values.
     */
    public final static Direction[] VERTICAL = {null, Direction.UP, Direction.DOWN};

    /**
     * The horizontal `Direction` that an entity/block is facing.
     */
    @Nonnull
    public final Direction horizontal;

    /**
     * The vertical `Direction` that a player/block is facing (null for facing toward the horizon).
     */
    public final Direction vertical;

    public Facing(@Nonnull Direction horizontal, Direction vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public Facing(@Nonnull Direction horizontal) {
        this(horizontal, null);
    }

    public Facing getOpposite() {
        if (vertical == null) {
            return new Facing(horizontal.getOpposite());
        }
        return new Facing(horizontal.getOpposite(), vertical.getOpposite());
    }

    @Override
    public String toString() {
        return vertical == null ?
            String.format("%s", horizontal) : String.format("%s_%s", horizontal, vertical);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Facing that = (Facing) o;
        return horizontal == that.horizontal && vertical == that.vertical;
    }

    @Override
    public int hashCode() {
        return Objects.hash(horizontal, vertical);
    }

    @Override
    public int compareTo(Facing o) {
        return toString().compareTo(o.toString());
    }

    /**
     * @param entity an entity (always a player in this context)
     * @return a `Facing` object representing the direction the entity is facing.
     */
    public static Facing getFacingForEntity(Entity entity) {
        final Direction[] facing = Direction.orderedByNearest(entity);
        int index = 0;
        Direction vertical = null;
        if (facing[index] == Direction.DOWN || facing[index] == Direction.UP) {
            vertical = facing[index];
            index ++;
        }
        final Direction horizontal = facing[index];
        return new Facing(horizontal, vertical);
    }
}
