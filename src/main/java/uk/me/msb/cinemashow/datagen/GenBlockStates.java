package uk.me.msb.cinemashow.datagen;

import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import uk.me.msb.cinemashow.CinemaShow;
import uk.me.msb.cinemashow.ShowProperties;
import uk.me.msb.cinemashow.block.Facing;
import uk.me.msb.cinemashow.block.ScreenBlock;
import uk.me.msb.cinemashow.setup.Registration;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for generating resources for the screen block models and state.
 */
public class GenBlockStates extends BlockStateProvider {

    /**
     * A map of all possible `Facing` values mapped to the `x` and `y` rotations necessary for a block model to face
     * that way.
     */
    private static final Map<Facing, Point> ROTATIONS = new HashMap<>();

    static {
        ROTATIONS.put(Facing.NORTH, new Point(0, 0));
        ROTATIONS.put(Facing.EAST, new Point(0, 90));
        ROTATIONS.put(Facing.SOUTH, new Point(0, 180));
        ROTATIONS.put(Facing.WEST, new Point(0, 270));
        ROTATIONS.put(Facing.NORTH_DOWN, new Point(90, 0));
        ROTATIONS.put(Facing.EAST_DOWN, new Point(90, 90));
        ROTATIONS.put(Facing.SOUTH_DOWN, new Point(90, 180));
        ROTATIONS.put(Facing.WEST_DOWN, new Point(90, 270));
        ROTATIONS.put(Facing.NORTH_UP, new Point(270, 0));
        ROTATIONS.put(Facing.EAST_UP, new Point(270, 90));
        ROTATIONS.put(Facing.SOUTH_UP, new Point(270, 180));
        ROTATIONS.put(Facing.WEST_UP, new Point(270, 270));
    }

    public GenBlockStates(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, CinemaShow.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        // Create a simple default screen block model using the `block/screen_base` texture.
        BlockModelBuilder defaultModel = models().getBuilder("block/screen_base");
        defaultModel.parent(models().getExistingFile(mcLoc("cube_all")));
        defaultModel.texture("all", modLoc("block/screen_base"));

        // Create all mode and state resources for each screen block.
        for (RegistryObject<Block> screenBlock: Registration.BLOCKS.getEntries()) {
            String blockName = screenBlock.getId().getPath();
            ShowProperties properties = Registration.SHOW_PROPERTIES.get(blockName);
            VariantBlockStateBuilder stateBuilder = getVariantBuilder(screenBlock.get());
            createShowModelsAndState(properties, stateBuilder, defaultModel);
        }
    }

    /**
     * Create all the models for the show's tiles (blocks) and create the block's state file with
     * variants mapping all the state combinations to these models with a correct rotations.
     *
     * @param properties the show's metadata
     * @param stateBuilder the variant builder for the screen block's state resource
     * @param defaultModel the default block model
     */
    private void createShowModelsAndState(
            ShowProperties properties, VariantBlockStateBuilder stateBuilder, BlockModelBuilder defaultModel
    ) {
        // for each combination of screen state ..
        stateBuilder.forAllStates(showState -> {
            ConfiguredModel.Builder<?> builder = ConfiguredModel.builder();

            Facing facing = showState.getValue(ScreenBlock.FACING);
            int x = showState.getValue(ScreenBlock.SCREEN_X);
            int y = showState.getValue(ScreenBlock.SCREEN_Y);

            // .. if state coords lie within the show's bounds ..
            if (x < properties.getBlocksX() && y < properties.getBlocksY()) {
                // .. create a block model resource mapping the show tile texture to the `NORTH` face of the block.

                String modelName = String.format(
                        "block/%s_%d_%d", properties.getBlockName(), x, y
                );
                BlockModelBuilder tileModel = models().getBuilder(modelName);
                tileModel.parent(models().getExistingFile(mcLoc("block/cube_all")));

                tileModel.texture("screen", modLoc(modelName));
                tileModel.texture("back", modLoc("block/screen_base"));

                tileModel.element()
                        .from(0, 0, 0)
                        .to(16, 16, 16)
                        .allFaces((direction, faceBuilder) -> faceBuilder.texture(
                                direction == Direction.NORTH ? "#screen" : "#back")
                        ).end();

                // Create the variant state mapping using the rotation defined in `ROTATIONS`.
                builder.modelFile(tileModel);
                Point rotation = ROTATIONS.get(facing);
                if (rotation.x != 0) {
                    builder = builder.rotationX(rotation.x);
                }
                if (rotation.y != 0) {
                    builder = builder.rotationY(rotation.y);
                }
            } else {
                // If out of bounds, just map the default model.
                builder.modelFile(defaultModel);
            }
            return builder.build();
        });
    }
}
