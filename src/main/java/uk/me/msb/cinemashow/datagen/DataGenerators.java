package uk.me.msb.cinemashow.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class subscribes to the `GatherDataEvent` event. When this event occurs (when MCF is invoked in data generation
 * mode) providers are registered that:
 * - generate resources for screen block models and state
 * - generate resources for screen block item models
 * - generate resources for the screen block names
 */
@Mod.EventBusSubscriber(modid = "cinemashow", bus = Mod.EventBusSubscriber.Bus.MOD)
@SuppressWarnings("unused")
public class DataGenerators {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        boolean run = true;
        if (event.includeClient()) {
            generator.addProvider(run,
                    new GenBlockStates(generator, event.getExistingFileHelper())
            );
            generator.addProvider(run,
                    new GenItemModels(generator, event.getExistingFileHelper())
            );
            generator.addProvider(run,
                    new GenLanguageProvider(generator, "en_us")
            );
        }
    }
}
