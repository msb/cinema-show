package uk.me.msb.cinemashow.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.me.msb.cinemashow.ScreenBlockName;
import uk.me.msb.cinemashow.ShowProperties;
import uk.me.msb.cinemashow.setup.Registration;

import java.util.Objects;

/**
 * Class implementing the screen block. The only customisation here is how the state of a newly placed block is set.
 */
public class ScreenBlock extends Block {

    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * Single instance of `ScreenStateProperty`
     */
    public static final ScreenStateProperty SCREEN = ScreenStateProperty.create();

    public ScreenBlock() {
        super(BlockBehaviour.Properties.of(Material.DIRT));
    }

    
    /** 
     * The new `ScreenStateProperty` is registered here
     * 
     * @param builder for registering new state properties
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SCREEN);
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

        ShowProperties props = Registration.SHOW_PROPERTIES.get(ScreenBlockName.fromBlock(this));

        if (props == null) {
            // this screen isn't assigned a show so no need to set screen state
            return bs;
        }

        // check in each direction for a matching screen block
        for (Direction direction: Direction.values()) {
            BlockState adjoiningBlockState = context.getLevel().getBlockState(clickedPos.relative(direction.getOpposite()));
            if (ScreenBlockName.fromBlock(adjoiningBlockState.getBlock()) == props.getAssignToBlock()) {
                // a match was found so check if `direction` qualifies as a valid direction to extend the screen
                // (up and right, relatively).
                ScreenState adjoiningState = adjoiningBlockState.getValue(SCREEN);
                ExtensionDirection extension = ExtensionDirection.get(adjoiningState.facing);
                if (direction == extension.x() || direction == extension.y()) {
                    // `direction` is a valid extension direction so create new screen block state with a matching
                    // `Facing` and the new calculated tile position extending from the existing block.
                    ScreenState newScreenState = new ScreenState(
                            adjoiningState.x + (direction == extension.x() ? 1 : 0),
                            adjoiningState.y + (direction == extension.y() ? -1 : 0),
                            adjoiningState.facing
                    );
                    // check that the 
                    if (newScreenState.x < props.getBlocksX() && newScreenState.y >= 0) {
                        return bs.setValue(SCREEN, newScreenState);
                    }
                }
            }
        }

        // No qualifying adjoining screen block was found to extend from or the placed block was outside the bounds of
        // the show so create state for a bottom left tile of the show with the screen facing the player.

        LOGGER.debug(String.format("Player facing: %s", facing));

        return bs.setValue(SCREEN, new ScreenState(0, props.getBlocksY() - 1, facing.getOpposite()));
    }
}
