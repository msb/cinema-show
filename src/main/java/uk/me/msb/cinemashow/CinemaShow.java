package uk.me.msb.cinemashow;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import uk.me.msb.cinemashow.setup.ModSetup;

/**
 * The "main" class for the cinema show mod
 */
@Mod("cinemashow")
public class CinemaShow {

    public CinemaShow()
    {
//        // Register the deferred registry
//        Registration.init();

        // Register the setup method for modloading
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(ModSetup::init);
    }
}
