package uk.me.msb.cinemashow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/**
 * Model class defining a show's properties. `create()` read the properties a YAML file using `ObjectMapper` and
 * validates them before returning the model.
 *
 * Note that MC isn't optimised for dealing with state `Property` objects with a large number of possible values. There
 * the max blocks has been limited to 6 to reduce the number of possible values for `ScreenStateProperty`.
 *
 * TODO `ObjectMapper` annotations could probably used to do a neater job of the validation.
 */
public class ShowProperties {

    /**
     * Upper limit of blocksX.
     */
    public static final int BLOCKS_X_MAX = 6; // see note above

    /**
     * Upper limit of blocksY
     */
    public static final int BLOCKS_Y_MAX = 6; // see note above

    /**
     * The name of the show (used in the language resource)
     */
    private String showName;

    /**
     * The animation rate in game ticks.
     */
    private int frameTime;

    /**
     * The number of X blocks the show requires. Mutually exclusive with `blocksY`.
     */
    private int blocksX;

    /**
     * The number of Y blocks the show requires. Mutually exclusive with `blocksX`.
     */
    private int blocksY;

    /**
     * The JSON structure used to serialise `frameTime` to the `mcmeta` file.
     */
    @JsonIgnore
    private Map<String, Map<String, Integer>> mcmeta;

    /**
     * The screen block the show is assigned to.
     */
    private ScreenBlockName assignToBlock;

    public String getShowName() {
        return showName;
    }

    @SuppressWarnings("unused") // used by `ObjectMapper`
    public void setShowName(String showName) {
        this.showName = showName;
    }

    public int getBlocksX() {
        return blocksX;
    }

    /**
     * Sets and validates `blocksX`.
     */
    @SuppressWarnings("unused") // used by `ObjectMapper`
    public void setBlocksX(int blocksX) {
        if (blocksX < 0) {
            throw new IllegalArgumentException("blocksX should be greater than zero");
        }
        if (blocksX > BLOCKS_X_MAX) {
            throw new IllegalArgumentException(
                String.format("blocksX cannot exceed %d", BLOCKS_X_MAX)
            );
        }
        this.blocksX = blocksX;
    }

    public int getBlocksY() {
        return blocksY;
    }

    /**
     * Sets and validates `blocksY`.
     */
    @SuppressWarnings("unused") // used by `ObjectMapper`
    public void setBlocksY(int blocksY) {
        if (blocksY < 0) {
            throw new IllegalArgumentException("blocksY should be greater than zero");
        }
        if (blocksY > BLOCKS_Y_MAX) {
            throw new IllegalArgumentException(
                    String.format("blocksY cannot exceed %d", BLOCKS_Y_MAX)
            );
        }
        this.blocksY = blocksY;
    }

    @SuppressWarnings("unused") // used by `ObjectMapper`
    public int getFrameTime() {
        return frameTime;
    }

    /**
     * Validates `frameTime` and sets `mcmeta`.
     */
    @SuppressWarnings("unused") // used by `ObjectMapper`
    public void setFrameTime(int frameTime) {
        if (frameTime < 0) {
            throw new IllegalArgumentException("frameTime should be greater than zero");
        }
        this.frameTime = frameTime;
        Map<String, Integer> animation = new HashMap<>();
        animation.put("frametime", frameTime);
        mcmeta = new HashMap<>();
        mcmeta.put("animation", animation);
    }

    public ScreenBlockName getAssignToBlock() {
        return assignToBlock;
    }

    @SuppressWarnings("unused") // used by `ObjectMapper`
    public void setAssignToBlock(ScreenBlockName assignToBlock) {
        this.assignToBlock = assignToBlock;
    }

    public Object getMcmeta() {
        return mcmeta;
    }

    /**
     * Reads the show's properties `metadata` (YAML format) and validates.
     * 
     * @param metadata the resource's input stream
     * @return The validated show properties.
     * @throws IOException Errors resulting from parsing `metadata`.
     */
    public static ShowProperties create(InputStream metadata) throws IOException {
        ObjectMapper mapper = new YAMLMapper();
        ShowProperties properties =  mapper.readValue(metadata, ShowProperties.class);
        if (properties.assignToBlock == null) {
            throw new IllegalArgumentException("assignToBlock is not defined");
        }
        if (properties.getMcmeta() == null) {
            throw new IllegalArgumentException("frameTime is not defined");
        }
        if (properties.blocksX == 0 && properties.blocksY == 0) {
            throw new IllegalArgumentException("One of blocksX and blocksY must be defined");
        }
        return properties;
    }

    /**
     * Saves properties to `metadata` file.
     * @param metadata file to write data to
     *
     * @throws IOException Errors resulting from writing the file.
     */
    public void save(File metadata) throws IOException {
        (new YAMLMapper()).writeValue(metadata, this);
    }
}
