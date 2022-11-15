package uk.me.msb.cinemashow;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * A partially implemented `Iterable` that iterates over the positions of all the show's tiles.
 * For simplicity the class also implements the `Iterator` so `Iterable.iterator()` just
 * returns `this`.
 */
public class TileIterableIterator implements Iterable<Point>, Iterator<Point> {

    /**
     * Tracks the next value for `Iterator.next()` and is set to `null` on completion.
     */
    private Point next = new Point();

    /**
     * The show's metadata.
     */
    final private ShowProperties props;

    public TileIterableIterator(ShowProperties props) {
        this.props = props;
    }

    @NotNull
    @Override
    public Iterator<Point> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public Point next() {
        if (next == null) {
            throw new NoSuchElementException();
        }
        Point copyOfNext = next.getLocation();
        next.translate(1, 0);
        if (next.x == props.getBlocksX()) {
            next.x = 0;
            next.translate(0, 1);
        }
        if (next.y == props.getBlocksY()) {
            next = null;
        }
        return copyOfNext;
    }

    @Override
    public void forEach(Consumer<? super Point> action) {
        throw new NotImplementedException();
    }

    @Override
    public Spliterator<Point> spliterator() {
        throw new NotImplementedException();
    }
}
