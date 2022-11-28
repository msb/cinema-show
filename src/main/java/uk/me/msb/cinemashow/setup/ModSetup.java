package uk.me.msb.cinemashow.setup;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nonnull;

/**
 * The only thing setup here currently is a custom tab for the screen block.
 */
public class ModSetup {

    public static final String TAB_NAME = "cinemashow";

    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(TAB_NAME) {
        @Nonnull
        public ItemStack makeIcon() {
            // FIXME
            return new ItemStack(Items.DIAMOND);
        }
    };

    public static void init(@SuppressWarnings("unused") FMLCommonSetupEvent event) {
    }
}
