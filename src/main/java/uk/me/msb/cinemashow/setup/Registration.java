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

import com.google.gson.Gson;

import uk.me.msb.cinemashow.ShowProperties;
import uk.me.msb.cinemashow.block.ScreenBlock;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
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
    public static final Map<String, ShowProperties> SHOW_PROPERTIES = new HashMap<>();

    /**
     * A deferred registry of all screen blocks.
     */
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    /**
     * A deferred registry of all screen block items.
     */
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static void init() {

        // Read the show index resource
        String assetsRoot = String.format("/assets/%s", MODID);
        List<String> showBlockNames = readShowIndex(assetsRoot);

        LOGGER.info(showBlockNames.toString());

        // Read and cache the `ShowProperties` resource for each show and register the blocks/items.
        for (String showBlockName: showBlockNames) {
            String propsName = String.format("%s/%s.json", assetsRoot, showBlockName);
            InputStream propsStream = Registration.class.getResourceAsStream(propsName);
            if (propsStream != null) {
                try {
                    ShowProperties props = ShowProperties.create(propsStream);
                    SHOW_PROPERTIES.put(showBlockName, props);
                    // Note that we pick any of the resulting items for the invertory tab.
                    TAB_TITLE_ITEM = itemFromBlock(BLOCKS.register(showBlockName, ScreenBlock::new));
                } catch (IOException e) {
                    throw new RuntimeException(String.format("Error parsing %s", propsName), e);
                }
            }
        }

        LOGGER.info(SHOW_PROPERTIES.toString());

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        // register the deferred registries
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

    /**
     * Read the show index resource file from the mod's asset's root.
     * 
     * @param assetsRoot the mod's resource asset root.
     * @return a list of show block names
     */
    private static List<String> readShowIndex(String assetsRoot) {
        String showIndex = String.format("%s/%s", assetsRoot, ShowProperties.SHOWINDEX_FILENAME);
        InputStream showsInput = Registration.class.getResourceAsStream(showIndex);
        if (showsInput == null) {
            throw new RuntimeException(String.format("Error reading resource: %s", showIndex));
        }
        @SuppressWarnings("unchecked")
        List<String> showBlockNames = (new Gson()).fromJson(new InputStreamReader(showsInput), List.class);
        return showBlockNames;
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
