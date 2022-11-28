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
import uk.me.msb.cinemashow.ShowProperties;
import uk.me.msb.cinemashow.block.ScreenBlock;

import java.io.IOException;
import java.io.InputStream;

/**
 * FIXME
 * The class responsible for the following initialisations:
 * - loading show metadata
 * - registering screen blocks
 * - registering screen block items
 */
public class Registration {

    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * FIXME
     */
    public static ShowProperties showProps;

    /**
     * A deferred registry of all screen blocks.
     */
    public static DeferredRegister<Block> BLOCKS;

    /**
     * A deferred registry of all screen block items.
     */
    public static DeferredRegister<Item> ITEMS;

    public static void init(String modId, ClassLoader loader) {

        BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, modId);
        ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, modId);

        String resource = String.format("/assets/%s/show.json", modId);
        InputStream metadata = loader.getResourceAsStream(resource);
        try {
            showProps = ShowProperties.create(metadata);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error parsing %s", resource), e);
        }

        LOGGER.info(showProps.toString());

        itemFromBlock(BLOCKS.register(showProps.getShowSlug(), ScreenBlock::new));

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        // register registries?
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

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
