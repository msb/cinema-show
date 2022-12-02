package uk.me.msb.cinemashow.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;
import uk.me.msb.cinemashow.CinemaShow;
import uk.me.msb.cinemashow.setup.Registration;

import static uk.me.msb.cinemashow.setup.ModSetup.TAB_NAME;

/**
 * Class for generating resources for the screen block names.
 */
public class GenLanguageProvider extends LanguageProvider {

    public GenLanguageProvider(DataGenerator gen, String locale) {
        super(gen, CinemaShow.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + TAB_NAME, "Cinema Show");
        // for each screen block ..
        for (RegistryObject<Block> screenBlock: Registration.BLOCKS.getEntries()) {
            final String name;
            String blockName = screenBlock.getId().getPath();
            // give the screen the show name
            name = Registration.SHOW_PROPERTIES.get(blockName).getShowName();
            add(screenBlock.get(), name);
        }
    }
}
