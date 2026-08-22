package mythicbotany.recipe;

import mythicbotany.registry.ModBlocks;
import mythicbotany.registry.ModItems;
import mythicbotany.flower.MythicFlowerSubTiles;
import mythicbotany.rune.RuneRitualRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

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
        registerSpecialFlowerRecipes();
    }

    private static void registerOreDictionary() {
        OreDictionary.registerOre("ingotAlfsteel", new ItemStack(ModItems.alfsteelIngot));
        OreDictionary.registerOre("nuggetAlfsteel", new ItemStack(ModItems.alfsteelNugget));
        OreDictionary.registerOre("blockAlfsteel", new ItemStack(ModBlocks.alfsteelBlock));
        OreDictionary.registerOre("oreElementium", new ItemStack(ModBlocks.elementiumOre));
        OreDictionary.registerOre("oreDragonstone", new ItemStack(ModBlocks.dragonstoneOre));
        OreDictionary.registerOre("oreGold", new ItemStack(ModBlocks.goldOre));
    }

    private static void registerSpecialFlowerRecipes() {
        BotaniaAPI.registerSubTile("mythicbotany_exoblaze", MythicFlowerSubTiles.Exoblaze.class);
        BotaniaAPI.registerSubTile("mythicbotany_wither_aconite", MythicFlowerSubTiles.WitherAconite.class);
        BotaniaAPI.registerSubTile("mythicbotany_aquapanthus", MythicFlowerSubTiles.Aquapanthus.class);
        BotaniaAPI.registerSubTile("mythicbotany_hellebore", MythicFlowerSubTiles.Hellebore.class);
        BotaniaAPI.registerSubTile("mythicbotany_raindeletia", MythicFlowerSubTiles.Raindeletia.class);
        BotaniaAPI.registerSubTile("mythicbotany_feysythia", MythicFlowerSubTiles.Feysythia.class);
        BotaniaAPI.registerSubTile("mythicbotany_petrunia", MythicFlowerSubTiles.Petrunia.class);

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
        BotaniaAPI.registerPetalRecipe(ItemBlockSpecialFlower.ofType("mythicbotany_exoblaze"), red, red, orange, orange,
                new ItemStack(ModItems.muspelheimRune));
        BotaniaAPI.registerPetalRecipe(ItemBlockSpecialFlower.ofType("mythicbotany_wither_aconite"), purple, purple, black, black,
                new ItemStack(ModItems.helheimRune));
        BotaniaAPI.registerPetalRecipe(ItemBlockSpecialFlower.ofType("mythicbotany_aquapanthus"), blue, blue, cyan, cyan,
                new ItemStack(ModItems.vanaheimRune));
        BotaniaAPI.registerPetalRecipe(ItemBlockSpecialFlower.ofType("mythicbotany_hellebore"), white, white, lime, lime,
                new ItemStack(ModItems.alfheimRune));
        BotaniaAPI.registerPetalRecipe(ItemBlockSpecialFlower.ofType("mythicbotany_raindeletia"), lightBlue, lightBlue, blue, blue,
                new ItemStack(ModItems.niflheimRune));
        BotaniaAPI.registerPetalRecipe(ItemBlockSpecialFlower.ofType("mythicbotany_feysythia"), yellow, yellow, orange, lime,
                new ItemStack(ModItems.asgardRune));
        BotaniaAPI.registerPetalRecipe(ItemBlockSpecialFlower.ofType("mythicbotany_petrunia"), magenta, magenta, purple, black,
                new ItemStack(ModItems.joetunheimRune));
    }

    private static ItemStack petal(int meta) {
        return new ItemStack(vazkii.botania.common.item.ModItems.petal, 1, meta);
    }

}
