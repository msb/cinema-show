package uk.me.msb.cinemashow.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.me.msb.cinemashow.ShowProperties;
import uk.me.msb.cinemashow.setup.Registration;

import java.util.Objects;

/**
 * Class implementing the screen block. The only customisation here is how the state of a newly placed block is set.
 */
public class ScreenBlock extends Block {

    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * Single instance of `FacingProperty`
     */
    public static final FacingProperty FACING = FacingProperty.create();
    /**
     * State property indicating the show tile x position
     */
    public static final IntegerProperty SCREEN_X = IntegerProperty.create("x", 0, ShowProperties.BLOCKS_X_MAX - 1);
    /**
     * State property indicating the show tile y position
     */
    public static final IntegerProperty SCREEN_Y = IntegerProperty.create("y", 0, ShowProperties.BLOCKS_Y_MAX - 1);

    public ScreenBlock() {
        super(BlockBehaviour.Properties.of(Material.DIRT));
    }

    
    /** 
     * The new `FACING`, `SCREEN_X`, and `SCREEN_Y` properties are registered here.
     * 
     * @param builder for registering new state properties
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING).add(SCREEN_X).add(SCREEN_Y);
    }

    
    /** 
     * Called to create the state for a screen block to be placed. Matching adjoining blocks are tested for and the
     * state is set accordingly allowing the player to use one screen block to simply build a whole show (see README).
     * Note that the Y direction of the show (downwards) is opposite to the Y direction in which a screen is built up.
     * 
     * @param context context for block placement
     * @return the state for the newly placed screen block
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final BlockState bs = Objects.requireNonNull(super.getStateForPlacement(context));

        BlockPos clickedPos = context.getClickedPos();
        Facing facing = Facing.getFacingForEntity(context.getPlayer());

        ShowProperties props = Registration.SHOW_PROPERTIES.get(getBlockName(this));

        // check in each direction for a matching screen block
        for (Direction direction: Direction.values()) {
            BlockState adjoiningState = context.getLevel().getBlockState(clickedPos.relative(direction.getOpposite()));
            if (getBlockName(adjoiningState.getBlock()).equals(props.getBlockName())) {
                // a show match was found so check if `direction` qualifies as a valid direction to extend the screen
                // (up and right, relatively).
                Facing adjoiningFacing = adjoiningState.getValue(FACING);
                int adjoiningX = adjoiningState.getValue(SCREEN_X);
                int adjoiningY = adjoiningState.getValue(SCREEN_Y);
                ExtensionDirection extension = ExtensionDirection.get(adjoiningFacing);
                if (direction == extension.x() || direction == extension.y()) {
                    // `direction` is a valid extension direction so create new screen block state with a matching
                    // `Facing` and the new calculated tile position extending from the existing block.
                    int x = adjoiningX + (direction == extension.x() ? 1 : 0);
                    int y = adjoiningY + (direction == extension.y() ? -1 : 0);
                    // check that the new show block lies within the bounds of the screen
                    if (x < props.getBlocksX() && y >= 0) {
                        return bs.setValue(FACING, adjoiningFacing).setValue(SCREEN_X, x).setValue(SCREEN_Y, y);
                    }
                }
            }
        }

        // No qualifying adjoining screen block was found to extend from or the placed block was outside the bounds of
        // the show. Therefore create state for a bottom left tile of the show with the screen facing the player.

        LOGGER.debug(String.format("Player facing: %s", facing));

        return bs.setValue(FACING, facing.getOpposite())
                .setValue(SCREEN_X, 0)
                .setValue(SCREEN_Y, props.getBlocksY() - 1);
    }

    /**
     * TODO is there a cleaner way to get this?
     * 
     * @param block a block object
     * @return the block's name (show slug)
     */
    private static String getBlockName(Block block) {
        String id = block.getDescriptionId();
        return id.substring(id.lastIndexOf(".") + 1);
    }
}
