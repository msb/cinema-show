package uk.me.msb.cinemashow.block;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nonnull;

/**
 * Enum representing the direction an entity or block is facing. Note that two directions are required because if an
 * entity/block is facing `UP` it will still have a `horizontal` orientation.
 */
public enum Facing implements StringRepresentable {

    NORTH(Direction.NORTH),
    EAST(Direction.EAST),
    SOUTH(Direction.SOUTH),
    WEST(Direction.WEST),
    NORTH_DOWN(Direction.NORTH, Direction.DOWN),
    EAST_DOWN(Direction.EAST, Direction.DOWN),
    SOUTH_DOWN(Direction.SOUTH, Direction.DOWN),
    WEST_DOWN(Direction.WEST, Direction.DOWN),
    NORTH_UP(Direction.NORTH, Direction.UP),
    EAST_UP(Direction.EAST, Direction.UP),
    SOUTH_UP(Direction.SOUTH, Direction.UP),
    WEST_UP(Direction.WEST, Direction.UP);

    /**
     * The horizontal `Direction` that an entity/block is facing.
     */
    @Nonnull
    private final Direction horizontal;

    /**
     * The vertical `Direction` that a player/block is facing (null for facing toward the horizon).
     */
    private final Direction vertical;

    Facing(@Nonnull Direction horizontal, Direction vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    Facing(Direction horizontal) {
        this(horizontal, null);
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }

    /**
     * @return the opposite facing to the enum's value (see `FacingTest` for expected behaviour).
     */
    public Facing getOpposite() {
        Direction vertical = this.vertical == null ? null : this.vertical.getOpposite();
        return Facing.getFacing(this.horizontal.getOpposite(), vertical);
    }

    /**
     * @param entity an entity (always a player in this context)
     * @return a `Facing` value representing the direction the entity is facing.
     */
    public static Facing getFacingForEntity(Entity entity) {
        final Direction[] facing = Direction.orderedByNearest(entity);
        int index = 0;
        Direction vertical = null;
        if (facing[index] == Direction.DOWN || facing[index] == Direction.UP) {
            vertical = facing[index];
            // The 2nd array element will now give the horizontal facing.
            index ++;
        }
        return Facing.getFacing(facing[index], vertical);
    }

    private static Facing getFacing(Direction horizontal, Direction vertical) {
        String horizontal_name = horizontal.name();
        String name = vertical == null ? horizontal_name : String.format("%s_%s", horizontal_name, vertical.name());
        return valueOf(name);
    }
}
