package uk.me.msb.cinemashow.gentextures;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import uk.me.msb.cinemashow.ShowProperties;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

/**
 * An executable class that generates sets of MC animated block textures from an input of sets of
 * animation frame images ("shows"). There is a more detailed description of the image processing
 * in the README (How the image processing works).
 */
public class GenerateTextures {

    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * The expected file name of the YAML file for the show's properties.
     */
    public static final String METADATA_FILENAME = "meta.json";

    /**
     * The image format type of the output textures
     */
    public static final String FORMAT_TYPE = "png";

    /**
     * The source folder containing the show sub-folders
     */
    private final File srcDir;

    /**
     * The root folder of mod's assets
     */
    private final File assetsDir;

    /**
     * The sub asset folder for block textures
     */
    private final File texturesDir;

    public GenerateTextures(String srcDir, String assetsDir) {
        this.srcDir = new File(srcDir);
        this.assetsDir = new File(assetsDir);
        this.texturesDir = Paths.get(
                this.assetsDir.getAbsolutePath(), "textures", "block"
        ).toFile();
    }

    private void run() {

        // create the destination folder, if necessary
        try {
            Files.createDirectories(texturesDir.toPath());
        } catch (IOException e) {
            throw new RuntimeException("IO error creating the destination folder", e);
        }

        // Processes each show sub-folder.
        for (File showDir: Objects.requireNonNull(srcDir.listFiles())) {
            if (showDir.isDirectory()) {
                try {
                    processShow(showDir);
                } catch (IOException e) {
                    throw new RuntimeException(
                        String.format("IO error processing: %s", showDir.getAbsolutePath()), e
                    );
                }
            }
        }
    }

    /**
     * Outputs the animated show textures for the images in a given show sub-folder.
     * 
     * @param showDir the show sub-folder being processed
     * @throws IOException possible error when reading/writing reosurces
     */
    public void processShow(File showDir) throws IOException {

        // read the show's properties
        File metadata = new File(showDir.getAbsolutePath(), METADATA_FILENAME);
        ShowProperties props = ShowProperties.create(new FileInputStream(metadata));
        // additional validation
        if (props.getBlocksX() != 0 && props.getBlocksY() != 0) {
            throw new IllegalArgumentException("Only blocksX or blocksY can be set");
        }
        // initialise the scaling context
        ShowScalingContext context = new ShowScalingContext(props);

        // scale each show image and keep in a list
        List<BufferedImage> frameImages = new ArrayList<>();
        File[] frames = Objects.requireNonNull(showDir.listFiles());
        Arrays.sort(frames);
        for (File frame: frames) {
            if (frame.isFile()) {
                BufferedImage sourceImage = ImageIO.read(frame);
                if (sourceImage != null) {
                    frameImages.add(scaleImage(context, sourceImage));
                }
            }
        }

        // for each show tile position ..
        for (Point position: context.positions()) {

            // .. create an empty animated texture image that is 1 block wide and `n` blocks high
            // where `n` is the number of animation frames.
            int outputImageHeight = ShowScalingContext.PIXELS_PER_BLOCK * frameImages.size();
            BufferedImage outputImage = new BufferedImage(
                    ShowScalingContext.PIXELS_PER_BLOCK, outputImageHeight,
                    BufferedImage.TYPE_INT_RGB
            );
            // for each frame image crop `position` tile from that image and draw it into the
            // texture image
            int i = 1;
            for (BufferedImage scaledImage: frameImages) {
                int textureYPos = outputImageHeight - i * ShowScalingContext.PIXELS_PER_BLOCK;
                // the offset for cropping along the secondary axis.
                Point offset = context.getOffset(scaledImage.getWidth(), scaledImage.getHeight());
                drawFrameTile(scaledImage, offset, position, textureYPos, outputImage);
                i ++;
            }

            // write the animated texture image and associated metadata to the assigned screen
            // block (the tile position is also encoded in the file name)
            String outputFileName = String.format(
                "%s_%d_%d", props.getAssignToBlock(), position.x, position.y
            );
            outputImage(outputFileName, outputImage);
            outputMetadata(outputFileName, props);
        }

        // Save the updated `ShowProperties` as a resource (to be available in-game, etc)
        String metadataResourceName = String.format("%s.json", props.getAssignToBlock());
        props.save(new File(assetsDir, metadataResourceName));
    }

    /**
     * Scales the frame image using the scaling context.
     * 
     * @param context the show's scaling context
     * @param image   the frame image to scale
     * @return a buffered scaled frame image
     */
    private static BufferedImage scaleImage(ShowScalingContext context, BufferedImage image) {
        Dimension scaleForImage = context.getScaleForImage(image.getWidth(), image.getHeight());
        Image scaledImage = image.getScaledInstance(
            scaleForImage.width, scaleForImage.height, Image.SCALE_REPLICATE
        );
        BufferedImage scaledBufferedImage = new BufferedImage(
            scaledImage.getWidth(null), scaledImage.getHeight(null),
            BufferedImage.TYPE_INT_RGB
        );
        scaledBufferedImage.getGraphics().drawImage(scaledImage, 0, 0, null);
        return scaledBufferedImage;
    }

    /**
     * Crops the tile from the image frame and draws it into animated texture image at
     * `textureYPos`.
     * 
     * @param frameImage  scaled frame image
     * @param offset      offset into the frame image of the bottom left tile
     * @param position    tile position
     * @param textureYPos y position of the tile in the animated texture image
     * @param outputImage animated texture image
     */
    private static void drawFrameTile(
        BufferedImage frameImage, Point offset, Point position, int textureYPos, 
        BufferedImage outputImage
    ) {
        BufferedImage tile = frameImage.getSubimage(
                offset.x + position.x * ShowScalingContext.PIXELS_PER_BLOCK,
                offset.y + position.y * ShowScalingContext.PIXELS_PER_BLOCK,
                ShowScalingContext.PIXELS_PER_BLOCK, ShowScalingContext.PIXELS_PER_BLOCK
        );
        outputImage.getGraphics().drawImage(tile, 0, textureYPos, null);
    }

    /**
     * Writes the animated texture image to file.
     * 
     * @param outputFileName the name of the block texture resource
     * @param outputImage animated texture image
     * @throws IOException possible error when writing file
     */
    private void outputImage(String outputFileName, BufferedImage outputImage) throws IOException {
        File imageFile = new File(texturesDir, String.format("%s.%s", outputFileName, FORMAT_TYPE));
        ImageIO.write(outputImage, FORMAT_TYPE, imageFile);
    }

    /**
     * Writes a "mcmeta" defining the frame time in ticks as a json resource file.
     * 
     * @param outputFileName the name of the block texture resource
     * @param props the show's properties
     * @throws IOException possible error when writing file
     */
    private void outputMetadata(String outputFileName, ShowProperties props) throws IOException {
        File metadataFile = new File(
                texturesDir, String.format("%s.%s.mcmeta", outputFileName, FORMAT_TYPE)
        );
        try (Writer writer = new FileWriter(metadataFile)) {
            writer.write(props.getMcmeta());
        }
    }

    public static void main(String[] args) {

        LOGGER.info(
            "Generating textures using {} as source folder and {} destination.",
            args[0], args[1]
        );

        GenerateTextures generateTextures = new GenerateTextures(args[0], args[1]);

        try {
            generateTextures.run();
        } catch (Throwable e) {
            LOGGER.info("Error generating textures.", e);
        }
    }
}
