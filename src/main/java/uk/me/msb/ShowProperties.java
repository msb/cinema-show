package uk.me.msb;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/**
 * Model class defining a show's properties. `create()` read the properties a YAML file using
 * `ObjectMapper` and validates them before returning the model.
 * 
 * TODO `ObjectMapper` annotations could probably used to do a neater job of the validation.
 */
public class ShowProperties {

    /**
     * The expected file name of the YAML file for the show's properties.
     */
    private static final String METADATA_FILENAME = "meta.yaml";

    /**
     * The names of the screen blocks to assign resources to.
     */
    @SuppressWarnings("unused") // used by `ObjectMapper`
    public enum ScreenBlockName {
        screen_alpha,
        screen_bravo,
        screen_charlie,
        screen_delta,
        screen_echo,
        screen_foxtrot,
        screen_golf,
        screen_hotel,
        screen_india,
        screen_juliet,
        screen_kilo,
        screen_lima,
        screen_mike,
        screen_november,
        screen_oscar,
        screen_papa,
        screen_quebec,
        screen_romeo,
        screen_sierra,
        screen_tango,
        screen_uniform,
        screen_victor,
        screen_whiskey,
        screen_xray,
        screen_yankee,
        screen_zulu,
    }

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
    private Map<String, Map<String, Integer>> mcmeta;

    /**
     * The screen block the show is assigned to.
     */
    public ScreenBlockName assignToBlock;

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
        if (blocksY != 0) {
            throw new IllegalArgumentException("Only blocksX or blocksY can be set");
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
        if (blocksX != 0) {
            throw new IllegalArgumentException("Only blocksX or blocksY can be set");
        }
        this.blocksY = blocksY;
    }

    /**
     * Validates `frameTime` and sets `mcmeta`.
     */
    @SuppressWarnings("unused") // used by `ObjectMapper`
    public void setFrameTime(int frameTime) {
        if (frameTime < 0) {
            throw new IllegalArgumentException("frameTime should be greater than zero");
        }
        Map<String, Integer> animation = new HashMap<>();
        animation.put("frametime", frameTime);
        mcmeta = new HashMap<>();
        mcmeta.put("animation", animation);
    }

    public Object getMcmeta() {
        return mcmeta;
    }

    /**
     * Reads and show's properties from the `METADATA_FILENAME` file and validates.
     * 
     * @param showDir The show resource folder.
     * @return The validated show properties.
     * @throws IOException Errors resulting from reading the file.
     */
    public static ShowProperties create(File showDir) throws IOException {
        File metadata = new File(showDir.getAbsolutePath(), METADATA_FILENAME);
        ObjectMapper mapper = new YAMLMapper();
        ShowProperties properties =  mapper.readValue(metadata, ShowProperties.class);
        if (properties.assignToBlock == null) {
            throw new IllegalArgumentException("assignToBlock is not defined");
        }
        if (properties.getMcmeta() == null) {
            throw new IllegalArgumentException("frameTime is not defined");
        }
        if (properties.getBlocksX() == 0 && properties.getBlocksY() == 0) {
            throw new IllegalArgumentException("Either blocksX or blocksY must be defined");
        }
        return properties;
    }
}
