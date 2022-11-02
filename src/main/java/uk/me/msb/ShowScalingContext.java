package uk.me.msb;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * The purpose of this context class is two fold:
 * 
 * - The source image is scaled according either axis depending on which property is defined.
 *   To simplify the code in `GenerateTextures`, this class abstracts which axis is defined.
 * - The secondary axis is cropped based on the minimum lengths of all the show's frame images.
 *   This class tracks that minimum length.
 */
public class ShowScalingContext {

    /**
     * How many pixels per MC block (along a single axis)
     */
    public static final int PIXELS_PER_BLOCK = 16;

    /**
     * Properties for a show.
     */
    final private ShowProperties props;

    /**
     * The minimum length of the secondary axis of all the scaled frame images.
     */
    private int min2ndAxisLength = Integer.MAX_VALUE;

    public ShowScalingContext(ShowProperties props) {
        this.props = props;
    }

    /**
     * Get's the target scale frame using the scheme defined in the README (How the image
     * processing works) . Also updates `min2ndAxisLength`.
     * 
     * @param imageWidth  the source frame image's width
     * @param imageHeight the source frame image's height
     * @return the scale for the frame image
     */
    public Dimension getScaleForImage(int imageWidth, int imageHeight) {
        Dimension scale = new Dimension();
        if (props.getBlocksX() == 0) {
            scale.height = props.getBlocksY() * PIXELS_PER_BLOCK;
            scale.width = scale.height * imageWidth / imageHeight;
            min2ndAxisLength = Math.min(min2ndAxisLength, scale.width);
        } else {
            scale.width = props.getBlocksX() * PIXELS_PER_BLOCK;
            scale.height = scale.width * imageHeight / imageWidth;
            min2ndAxisLength = Math.min(min2ndAxisLength, scale.height);
        }
        return scale;
    }

    /**
     * Returns an `Iterable` that iterates over the positions of all the show's tiles.
     * 
     * @return the `Iterable`
     */
    public Iterable<Point> positions() {
        min2ndAxisLength -= min2ndAxisLength % PIXELS_PER_BLOCK;
        return new TileIterableIterator();
    }

    /**
     * A partially implemented `Iterable` that iterates over the positions of all the show's tiles.
     * For simplicity the class also implements the `Iterator` so `Iterable.iterator()` just
     * returns `this`.
     */
    class TileIterableIterator implements Iterable<Point>, Iterator<Point> {

        /**
         * Tracks the next value for `Iterator.next()` and is set to `null` on completion.
         */
        private Point next = new Point();
        
        /**
         * The upper bounds on the tile positions.
         */
        final private Point limit;

        public TileIterableIterator() {
            if (props.getBlocksX() == 0) {
                limit = new Point(min2ndAxisLength / PIXELS_PER_BLOCK, props.getBlocksY());
            } else {
                limit = new Point(props.getBlocksX(), min2ndAxisLength / PIXELS_PER_BLOCK);
            }
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
            if (next.x == limit.x) {
                next.x = 0;
                next.translate(0, 1);
            }
            if (next.y == limit.y) {
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

    /**
     * Returns the offset into the image used to crop along the secondary axis.
     * 
     * @param imageWidth  the source frame image's width
     * @param imageHeight the source frame image's height
     * @return
     */
    public Point getOffset(int imageWidth, int imageHeight) {
        if (props.getBlocksX() == 0) {
            return new Point((imageWidth - min2ndAxisLength) / 2, 0);
        } else {
            return new Point(0, (imageHeight - min2ndAxisLength) / 2);
        }
    }
}
