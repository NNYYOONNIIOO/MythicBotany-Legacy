package mythicbotany.recipe;

import mythicbotany.registry.ModBlocks;
import mythicbotany.registry.ModItems;
import mythicbotany.rune.RuneRitualRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.botania.api.BotaniaAPI;

public final class ModRecipes {
    private static boolean registered;

    private ModRecipes() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        registerOreDictionary();
        InfuserRecipe.loadResources();
        RuneRitualRegistry.loadResources();
        SmeltingRecipeLoader.loadResources();
        registerFunctionalFlowerRecipes();
    }

    private static void registerOreDictionary() {
        OreDictionary.registerOre("ingotAlfsteel", new ItemStack(ModItems.alfsteelIngot));
        OreDictionary.registerOre("nuggetAlfsteel", new ItemStack(ModItems.alfsteelNugget));
        OreDictionary.registerOre("blockAlfsteel", new ItemStack(ModBlocks.alfsteelBlock));
        OreDictionary.registerOre("oreElementium", new ItemStack(ModBlocks.elementiumOre));
        OreDictionary.registerOre("oreDragonstone", new ItemStack(ModBlocks.dragonstoneOre));
        OreDictionary.registerOre("oreGold", new ItemStack(ModBlocks.goldOre));
    }

    private static void registerFunctionalFlowerRecipes() {
        ItemStack red = petal(14);
        ItemStack orange = petal(1);
        ItemStack yellow = petal(11);
        ItemStack green = petal(2);
        ItemStack lime = petal(5);
        ItemStack blue = petal(4);
        ItemStack cyan = petal(6);
        ItemStack lightBlue = petal(12);
        ItemStack purple = petal(13);
        ItemStack magenta = petal(13);
        ItemStack black = petal(8);
        ItemStack white = petal(0);
        BotaniaAPI.registerPetalRecipe(new ItemStack(ModBlocks.exoblaze), red, red, orange, orange,
                new ItemStack(ModItems.muspelheimRune));
        BotaniaAPI.registerPetalRecipe(new ItemStack(ModBlocks.witherAconite), purple, purple, black, black,
                new ItemStack(ModItems.helheimRune));
        BotaniaAPI.registerPetalRecipe(new ItemStack(ModBlocks.aquapanthus), blue, blue, cyan, cyan,
                new ItemStack(ModItems.vanaheimRune));
        BotaniaAPI.registerPetalRecipe(new ItemStack(ModBlocks.hellebore), white, white, lime, lime,
                new ItemStack(ModItems.alfheimRune));
        BotaniaAPI.registerPetalRecipe(new ItemStack(ModBlocks.raindeletia), lightBlue, lightBlue, blue, blue,
                new ItemStack(ModItems.niflheimRune));
        BotaniaAPI.registerPetalRecipe(new ItemStack(ModBlocks.feysythia), yellow, yellow, orange, lime,
                new ItemStack(ModItems.asgardRune));
        BotaniaAPI.registerPetalRecipe(new ItemStack(ModBlocks.petrunia), magenta, magenta, purple, black,
                new ItemStack(ModItems.joetunheimRune));
    }

    private static ItemStack petal(int meta) {
        return new ItemStack(vazkii.botania.common.item.ModItems.petal, 1, meta);
    }

}
