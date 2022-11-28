package uk.me.msb.cinemashow;

import uk.me.msb.cinemashow.setup.Registration;

/**
 * FIXME
 */
@SuppressWarnings("unused")
public class AbstractShowMain {

    public AbstractShowMain(String modId)
    {
        // Register the deferred registry
        Registration.init(modId, getClass().getClassLoader());
    }
}
