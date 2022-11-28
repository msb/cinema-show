package uk.me.msb.cinemashow;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import uk.me.msb.cinemashow.setup.ModSetup;
import uk.me.msb.cinemashow.setup.Registration;

/**
 * FIXME
 */
@SuppressWarnings("unused")
public abstract class AbstractShow {

    public AbstractShow(String modId)
    {
        // Register the deferred registries
        Registration.init(modId, getClass().getClassLoader());

        // Register the setup method for modloading
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(ModSetup::init);
    }
}
