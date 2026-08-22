package mythicbotany.recipe;

import mythicbotany.MythicBotany;
import mythicbotany.registry.ModBlocks;
import mythicbotany.registry.ModItems;
import mythicbotany.rune.RuneRitualRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModRecipes {
    private static boolean registered;

    private ModRecipes() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        GameRegistry.addShapedRecipe(new ResourceLocation(MythicBotany.MODID, "mana_infuser"),
                new ResourceLocation(MythicBotany.MODID, "general"),
                new ItemStack(ModBlocks.manaInfuser), "IRI", "EGE", "IRI",
                'I', Items.IRON_INGOT, 'R', Items.REDSTONE, 'E', Items.ENDER_PEARL, 'G', Items.GOLD_INGOT);
        GameRegistry.addShapedRecipe(new ResourceLocation(MythicBotany.MODID, "mana_collector"),
                new ResourceLocation(MythicBotany.MODID, "general"),
                new ItemStack(ModBlocks.manaCollector), "IRI", "EGE", "IRI",
                'I', Items.IRON_INGOT, 'R', Items.REDSTONE, 'E', Items.GLOWSTONE_DUST, 'G', Items.GOLD_INGOT);
        GameRegistry.addShapedRecipe(new ResourceLocation(MythicBotany.MODID, "alfsteel_block"),
                new ResourceLocation(MythicBotany.MODID, "general"),
                new ItemStack(ModBlocks.alfsteelBlock), "III", "III", "III", 'I', ModItems.alfsteelIngot);
        GameRegistry.addShapedRecipe(new ResourceLocation(MythicBotany.MODID, "alfsteel_sword"),
                new ResourceLocation(MythicBotany.MODID, "general"),
                new ItemStack(ModItems.alfsteelSword), "I", "I", "S", 'I', ModItems.alfsteelIngot, 'S', Items.STICK);
        GameRegistry.addShapedRecipe(new ResourceLocation(MythicBotany.MODID, "alfsteel_pick"),
                new ResourceLocation(MythicBotany.MODID, "general"),
                new ItemStack(ModItems.alfsteelPick), "III", " S ", " S ", 'I', ModItems.alfsteelIngot, 'S', Items.STICK);
        InfuserRecipe.register(new ItemStack(Items.GOLD_INGOT), new ItemStack(ModItems.alfsteelIngot), 20000);
        InfuserRecipe.register(new ItemStack(Items.IRON_INGOT), new ItemStack(ModItems.alfsteelNugget, 2), 5000);
        InfuserRecipe.register(new ItemStack(Items.NETHER_STAR), new ItemStack(ModItems.fadedNetherStar), 10000);
        InfuserRecipe.register(new ItemStack(Items.APPLE), new ItemStack(ModItems.dreamCherry), 10000);
        InfuserRecipe.register(new ItemStack(vazkii.botania.common.item.ModItems.manaResource, 1, 7),
                new ItemStack(ModItems.alfsteelIngot), 50000);
        RuneRitualRegistry.registerDefaults();
    }
}
