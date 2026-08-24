package mythicbotany.recipe;

import mythicbotany.MythicBotany;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.botania.common.crafting.recipe.SpecialFloatingFlowerRecipe;

/** Registers Botania's NBT-aware floating-special-flower recipes for MythicBotany flowers. */
@Mod.EventBusSubscriber(modid = MythicBotany.MODID)
public final class FloatingFlowerRecipeRegistry {
    private static final String[] FLOWERS = {
            "exoblaze", "aquapanthus", "hellebore", "feysythia",
            "petrunia", "raindeletia", "wither_aconite"
    };

    private FloatingFlowerRecipeRegistry() {
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<IRecipe> event) {
        for (String flower : FLOWERS) {
            SpecialFloatingFlowerRecipe recipe = new SpecialFloatingFlowerRecipe(flower);
            recipe.setRegistryName(new ResourceLocation(MythicBotany.MODID, "floating_" + flower));
            event.getRegistry().register(recipe);
        }
    }
}
