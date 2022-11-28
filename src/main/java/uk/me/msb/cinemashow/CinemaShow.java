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
        // Register the setup method for mod loading
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(ModSetup::init);
    }
}
