package uk.me.msb.cinemashow;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import uk.me.msb.cinemashow.setup.ModSetup;
import uk.me.msb.cinemashow.setup.Registration;

/**
 * The "main" class for the cinema show mod
 */
@Mod(CinemaShow.MODID)
public class CinemaShow {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "cinemashow";

    public CinemaShow()
    {
        // Register the deferred registries
        Registration.init();

        // Register the setup method for modloading
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(ModSetup::init);
    }
}
