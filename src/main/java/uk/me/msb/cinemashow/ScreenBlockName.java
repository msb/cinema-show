package uk.me.msb.cinemashow;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

/**
 * The names of the screen blocks to assign resources to.
 */
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
    screen_zulu;

    /**
     * @param blockRegistry registry object for a block
     * @return the block's `ScreenBlockName` (or `null` if it isn't a screen block)
     */
    public static ScreenBlockName fromBlock(RegistryObject<Block> blockRegistry) {
        return fromName(blockRegistry.getId().getPath());
    }

    /**
     * @param block a block object
     * @return the block's `ScreenBlockName` (or `null` if it isn't a screen block)
     */
    public static ScreenBlockName fromBlock(Block block) {
        String id = block.getDescriptionId();
        String name = id.substring(id.lastIndexOf(".") + 1);
        return fromName(name);
    }

    /**
     * @param item a block item object
     * @return the block item's `ScreenBlockName` (or `null` if it isn't a screen block)
     */
    public static ScreenBlockName fromItem(RegistryObject<Item> item) {
        return fromName(item.getId().getPath());
    }

    /**
     * Helper method
     *
     * @param name a block name
     * @return a `ScreenBlockName` (or `null` if it isn't the name of a screen block)
     */
    private static ScreenBlockName fromName(String name) {
        if (!name.startsWith("screen_")) {
            return null;
        }
        return ScreenBlockName.valueOf(name);
    }
}
