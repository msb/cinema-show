package uk.me.msb.cinemashow.datagen;

import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import uk.me.msb.cinemashow.CinemaShow;
import uk.me.msb.cinemashow.setup.Registration;

/**
 * Class for generating resources for the screen block item models.
 */
public class GenItemModels extends ItemModelProvider {

    public GenItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, CinemaShow.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // for each screen item block ..
        for (RegistryObject<Item> screenBlockItem: Registration.ITEMS.getEntries()) {
            ScreenBlockName blockName = ScreenBlockName.fromItem(screenBlockItem);
            // .. if the screen is assigned to a show ..
            if (Registration.SHOW_PROPERTIES.containsKey(blockName)) {
                // .. create an item model with `block/screen_assigned` texture on a face that's visible in the
                // inventory, etc ..
            ItemModelBuilder builder = getBuilder(screenBlockItem.getId().getPath());
            builder.parent(getExistingFile(mcLoc("block/cube_all")));

            builder.texture("screen", modLoc("block/screen_item"));
            builder.texture("back", modLoc("block/screen_base"));

            builder.element()
                    .from(0, 0, 0)
                    .to(16, 16, 16)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture(
                            direction == Direction.UP ? "#screen" : "#back"
                    )).end();
            } else {
                // .. else create a simple item model from the `block/screen_base` texture.
                withExistingParent(screenBlockItem.getId().getPath(), modLoc("block/screen_base"));
            }
        }
    }
}
