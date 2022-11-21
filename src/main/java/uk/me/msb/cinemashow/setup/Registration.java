package uk.me.msb.cinemashow.setup;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.me.msb.cinemashow.ScreenBlockName;
import uk.me.msb.cinemashow.ShowProperties;
import uk.me.msb.cinemashow.block.ScreenBlock;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static uk.me.msb.cinemashow.CinemaShow.MODID;

/**
 * The class responsible for the following initialisations:
 * - loading show metadata
 * - registering screen blocks
 * - registering screen block items
 */
public class Registration {

    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * A map of the metadata for all defined shows keyed on their assign screen block.
     */
    public static final Map<ScreenBlockName, ShowProperties> SHOW_PROPERTIES = new HashMap<>();

    /**
     * A deferred registry of all screen blocks.
     */
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    /**
     * A deferred registry of all screen block items.
     */
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static void init() {

        // check each block name for any show metadata resource files and populate `SHOW_PROPERTIES`
        for (ScreenBlockName name: ScreenBlockName.values()) {
            String resource = String.format("/assets/%s/%s.json", MODID, name);
            InputStream metadata = name.getClass().getResourceAsStream(resource);
            if (metadata != null) {
                try {
                    ShowProperties props = ShowProperties.create(metadata);
                    SHOW_PROPERTIES.put(props.getAssignToBlock(), props);
                } catch (IOException e) {
                    throw new RuntimeException(String.format("Error parsing %s", resource), e);
                }
            }
        }

        LOGGER.info(SHOW_PROPERTIES.toString());

        // register all screen block and their items (saving one of the items for the tab title)
        for (ScreenBlockName screenBlock: ScreenBlockName.values()) {
            TAB_TITLE_ITEM = itemFromBlock(BLOCKS.register(screenBlock.name(), ScreenBlock::new));
        }

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        // register registries?
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

    /**
     * Any of the registered screen items to be used as the show/screen tab's title.
     */
    public static RegistryObject<Item> TAB_TITLE_ITEM;

    /**
     * The only property we are currently assigning to items is a custom tab
     */
    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(ModSetup.ITEM_GROUP);

    /**
     * Convenience function: Take a RegistryObject<Block> and make a corresponding RegistryObject<Item> from it
     */
    public static <B extends Block> RegistryObject<Item> itemFromBlock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }
}
