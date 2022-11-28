package uk.me.msb.cinemashow;

import uk.me.msb.cinemashow.setup.Registration;

/**
 * FIXME
 */
@SuppressWarnings("unused")
public abstract class AbstractShow {

    public AbstractShow(String modId)
    {
        // Register blocks/items with deferred registries.
        Registration.init(modId, getClass().getClassLoader());
    }
}
