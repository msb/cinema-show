package uk.me.msb.cinemashow.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.text.WordUtils;
import uk.me.msb.cinemashow.ScreenBlockName;
import uk.me.msb.cinemashow.setup.Registration;

import static uk.me.msb.cinemashow.setup.ModSetup.TAB_NAME;

/**
 * Class for generating resources for the screen block names.
 */
public class GenLanguageProvider extends LanguageProvider {

    public GenLanguageProvider(DataGenerator gen, String locale) {
        super(gen, "cinemashow", locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + TAB_NAME, "Cinema Show");
        // for each screen block ..
        for (RegistryObject<Block> screenBlock: Registration.BLOCKS.getEntries()) {
            final String name;
            ScreenBlockName blockName = ScreenBlockName.fromBlock(screenBlock);
            // .. if the screen is assigned to a show ..
            if (Registration.SHOW_PROPERTIES.containsKey(blockName)) {
                // .. give it the show name ..
                name = Registration.SHOW_PROPERTIES.get(blockName).getShowName();
            } else {
                // .. else change the screen block name into something more readible.
                name = WordUtils.capitalize(blockName.name().replace("_", " "));
            }
            add(screenBlock.get(), name);
        }
    }
}
