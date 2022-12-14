package uk.me.msb.cinemashow.gentextures;

import uk.me.msb.cinemashow.ShowProperties;
import uk.me.msb.cinemashow.TileIterableIterator;

import java.awt.*;

/**
 * The purpose of this context class is two fold:
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
     * Whether X is the defined axis.
     */
    final private boolean xDefinedAxis;

    /**
     * The minimum length of the secondary (undefined) axis of all the scaled frame images.
     */
    private int min2ndAxisLength = Integer.MAX_VALUE;

    public ShowScalingContext(ShowProperties props) {
        this.props = props;
        xDefinedAxis = props.getBlocksY() == 0;
    }

    /**
     * Gets the target scale frame using the scheme defined in the README (How the image
     * processing works) . Also updates `min2ndAxisLength`.
     * 
     * @param imageWidth  the source frame image's width
     * @param imageHeight the source frame image's height
     * @return the scale for the frame image
     */
    public Dimension getScaleForImage(int imageWidth, int imageHeight) {
        Dimension scale = new Dimension();
        if (xDefinedAxis) {
            scale.width = props.getBlocksX() * PIXELS_PER_BLOCK;
            scale.height = scale.width * imageHeight / imageWidth;
            min2ndAxisLength = Math.min(min2ndAxisLength, scale.height);
        } else {
            scale.height = props.getBlocksY() * PIXELS_PER_BLOCK;
            scale.width = scale.height * imageWidth / imageHeight;
            min2ndAxisLength = Math.min(min2ndAxisLength, scale.width);
        }
        return scale;
    }

    /**
     * Returns an `Iterable` that iterates over the positions of all the show's tiles.
     * Also updates the blocks 2nd axis length.
     * 
     * @return the `Iterable`
     */
    public Iterable<Point> positions() {
        // rounds `min2ndAxisLength` down to the nearest `PIXELS_PER_BLOCK` (idempotent)
        min2ndAxisLength -= min2ndAxisLength % PIXELS_PER_BLOCK;

        if (xDefinedAxis) {
            props.setBlocksY(min2ndAxisLength / PIXELS_PER_BLOCK);
        } else {
            props.setBlocksX(min2ndAxisLength / PIXELS_PER_BLOCK);
        }
        return new TileIterableIterator(props);
    }

    /**
     * @param imageWidth  the source frame image's width
     * @param imageHeight the source frame image's height
     * @return the offset into the image used to crop along the secondary axis.
     */
    public Point getOffset(int imageWidth, int imageHeight) {
        if (xDefinedAxis) {
            return new Point(0, (imageHeight - min2ndAxisLength) / 2);
        } else {
            return new Point((imageWidth - min2ndAxisLength) / 2, 0);
        }
    }
}
