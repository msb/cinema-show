package uk.me.msb.cinemashow;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.*;

/**
 * Model class defining a show's properties. `create()` read the properties a YAML file using `ObjectMapper` and
 * validates them before returning the model.
 *
 * Note that MC isn't optimised for dealing with state `Property` objects with a large number of possible values. There
 * the max blocks has been limited to 6 to reduce the number of possible values for `ScreenStateProperty`.
 */
public class ShowProperties {

    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Gson instance.
     */
    private static final Gson GSON = new Gson();

    /**
     * Upper limit of blocksX.
     */
    public static final int BLOCKS_X_MAX = 6; // see note above

    /**
     * Upper limit of blocksY.
     */
    public static final int BLOCKS_Y_MAX = 6; // see note above

    /**
     * Default value if `franeTime` not set.
     */
    private static final int DEFAULT_FRAME_TIME = 25;

    /**
     * The name of the show (used in the language resource).
     */
    @SuppressWarnings("unused") // used by `GSON.fromJson()`
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
     * The JSON string created from `frameTime` for the texture `mcmeta` file.
     */
    private transient String mcmeta;

    /**
     * The screen block the show is assigned to.
     */
    @SuppressWarnings("unused") // used by `GSON.fromJson()`
    private ScreenBlockName assignToBlock;

    public String getShowName() {
        return showName;
    }

    public int getBlocksX() {
        return blocksX;
    }

    /**
     * Sets and validates `blocksX`.
     */
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

    /**
     * Validates `frameTime`.
     */
    public void setFrameTime(int frameTime) {
        if (frameTime < 0) {
            throw new IllegalArgumentException("frameTime should be greater than zero");
        }
        this.frameTime = frameTime;
    }

    public ScreenBlockName getAssignToBlock() {
        return assignToBlock;
    }

    public String getMcmeta() {
        return mcmeta;
    }

    /**
     * Reads the show's properties `metadata` (JSON format) and validates.
     * 
     * @param metadata the resource's input stream
     * @return The validated show properties.
     * @throws IOException Errors resulting from parsing `metadata`.
     */
    public static ShowProperties create(InputStream metadata) throws IOException {
        ShowProperties props = GSON.fromJson(new InputStreamReader(metadata), ShowProperties.class);
        if (props.frameTime == 0) {
            props.setFrameTime(ShowProperties.DEFAULT_FRAME_TIME);
            LOGGER.warn("`frameTime` not defined - setting to {}", props.frameTime);
        }
        props.mcmeta = createMcmeta(props.frameTime);
        if (props.blocksX == 0 && props.blocksY == 0) {
            props.setBlocksX(ShowProperties.BLOCKS_X_MAX);
            LOGGER.warn("Neither `blocksX` nor `blocksY` are defined - setting `blocksX` to {}", props.blocksX);

        }
        return props;
    }

    /**
     * Serialises `frameTime` as a JSON string for the texture `mcmeta` file.
     *
     * @param frameTime the animation rate
     * @return JSON string
     * @throws IOException Errors resulting from writing `mcmeta`.
     */
    private static String createMcmeta(int frameTime) throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter json = (new Gson()).newJsonWriter(writer);
        json.beginObject();
        json.name("animation").beginObject();
        json.name("frametime").value(frameTime);
        json.endObject();
        json.endObject();
        json.close();
        return writer.toString();
    }

    /**
     * Saves properties to `metadata` file.
     * @param metadata file to write data to
     *
     * @throws IOException Errors resulting from writing the file.
     */
    public void save(File metadata) throws IOException {
        try (Writer writer = new FileWriter(metadata)) {
            GSON.toJson(this, writer);
        }
    }
}
