package uk.me.msb.cinemashow.datagen;

import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder.PartBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import uk.me.msb.cinemashow.ScreenBlockName;
import uk.me.msb.cinemashow.ShowProperties;
import uk.me.msb.cinemashow.TileIterableIterator;
import uk.me.msb.cinemashow.block.Facing;
import uk.me.msb.cinemashow.block.ScreenBlock;
import uk.me.msb.cinemashow.block.ScreenState;
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
        ROTATIONS.put(new Facing(Direction.NORTH), new Point(0, 0));
        ROTATIONS.put(new Facing(Direction.EAST), new Point(0, 90));
        ROTATIONS.put(new Facing(Direction.SOUTH), new Point(0, 180));
        ROTATIONS.put(new Facing(Direction.WEST), new Point(0, 270));
        ROTATIONS.put(new Facing(Direction.NORTH, Direction.DOWN), new Point(90, 0));
        ROTATIONS.put(new Facing(Direction.EAST, Direction.DOWN), new Point(90, 90));
        ROTATIONS.put(new Facing(Direction.SOUTH, Direction.DOWN), new Point(90, 180));
        ROTATIONS.put(new Facing(Direction.WEST, Direction.DOWN), new Point(90, 270));
        ROTATIONS.put(new Facing(Direction.NORTH, Direction.UP), new Point(270, 0));
        ROTATIONS.put(new Facing(Direction.EAST, Direction.UP), new Point(270, 90));
        ROTATIONS.put(new Facing(Direction.SOUTH, Direction.UP), new Point(270, 180));
        ROTATIONS.put(new Facing(Direction.WEST, Direction.UP), new Point(270, 270));
    }

    public GenBlockStates(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, "cinemashow", helper);
    }

    @Override
    protected void registerStatesAndModels() {
        // create a simple default screen block model using the `block/screen_base` texture.
        BlockModelBuilder defaultModel = models().getBuilder("block/screen_base");
        defaultModel.parent(models().getExistingFile(mcLoc("cube_all")));
        defaultModel.texture("all", modLoc("block/screen_base"));

        // for each screen block ..
        for (RegistryObject<Block> screenBlock: Registration.BLOCKS.getEntries()) {
            // .. create a state file with `defaultModel` as the default block model.
            MultiPartBlockStateBuilder stateBuilder = getMultipartBuilder(screenBlock.get());
            stateBuilder.part().modelFile(defaultModel).addModel();
            // if that screen block has a show assigned create the additional show models and
            // conditionally reference them in the state.
            ScreenBlockName blockName = ScreenBlockName.fromBlock(screenBlock);
            ShowProperties properties = Registration.SHOW_PROPERTIES.get(blockName);
            if (properties != null) {
                createShowModelsAndState(properties, stateBuilder);
            }
        }
    }

    /**
     * Create all the models for the show's tiles (blocks) and conditionally add them to the `showState` resource.
     *
     * @param properties the show's metadata
     * @param showState the builder for the screen block's state resource
     */
    private void createShowModelsAndState(ShowProperties properties, MultiPartBlockStateBuilder showState) {
        // For each tile (block) of the show ..
        for (Point tile: new TileIterableIterator(properties)) {

            // .. create a block model resource mapping the show tile texture to the `NORTH` face of the block.

            String modelName = String.format(
                    "block/%s_%d_%d", properties.getAssignToBlock(), tile.x, tile.y
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

            // Conditionally add all possible rotations of `tileModel` to the `showState` resource.
            for (Map.Entry<Facing, Point> rotation: ROTATIONS.entrySet()) {
                ConfiguredModel.Builder<PartBuilder> part = showState.part().modelFile(tileModel);
                if (rotation.getValue().x != 0) {
                    part = part.rotationX(rotation.getValue().x);
                }
                if (rotation.getValue().y != 0) {
                    part = part.rotationY(rotation.getValue().y);
                }
                part.addModel().condition(ScreenBlock.SCREEN, new ScreenState(tile.x, tile.y, rotation.getKey()));
            }
        }
    }
}
