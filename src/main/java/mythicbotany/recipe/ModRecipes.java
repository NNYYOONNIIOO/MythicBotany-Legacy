package mythicbotany.recipe;

import mythicbotany.registry.ModBlocks;
import mythicbotany.registry.ModItems;
import mythicbotany.flower.MythicFlowerSubTiles;
import mythicbotany.flower.MythicFlowerSignature;
import mythicbotany.rune.RuneRitualRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

public final class ModRecipes {
    private static boolean registered;
    private static boolean specialFlowersRegistered;

    private ModRecipes() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        registerOreDictionary();
        YggdrasilBranchRecipe.registerDefaults();
        InfuserRecipe.loadResources();
        RuneRitualRegistry.loadResources();
        SmeltingRecipeLoader.loadResources();
        registerSpecialFlowerRecipes();
    }

    /** Register Botania's shared specialflower subtiles before client models and tile data are baked. */
    public static void registerSpecialFlowerSubTiles() {
        if (specialFlowersRegistered) {
            return;
        }
        specialFlowersRegistered = true;
        BotaniaAPI.registerSubTile("mythicbotany_exoblaze", MythicFlowerSubTiles.Exoblaze.class);
        BotaniaAPI.registerSubTileSignature(MythicFlowerSubTiles.Exoblaze.class,
                new MythicFlowerSignature("mythicbotany_exoblaze",
                        "tooltip.mythicbotany.flower.exoblaze"));
        BotaniaAPI.addSubTileToCreativeMenu("mythicbotany_exoblaze");
        BotaniaAPI.registerSubTile("mythicbotany_wither_aconite", MythicFlowerSubTiles.WitherAconite.class);
        BotaniaAPI.registerSubTileSignature(MythicFlowerSubTiles.WitherAconite.class,
                new MythicFlowerSignature("mythicbotany_wither_aconite",
                        "tooltip.mythicbotany.flower.wither_aconite"));
        BotaniaAPI.addSubTileToCreativeMenu("mythicbotany_wither_aconite");
        BotaniaAPI.registerSubTile("mythicbotany_aquapanthus", MythicFlowerSubTiles.Aquapanthus.class);
        BotaniaAPI.registerSubTileSignature(MythicFlowerSubTiles.Aquapanthus.class,
                new MythicFlowerSignature("mythicbotany_aquapanthus",
                        "tooltip.mythicbotany.flower.aquapanthus"));
        BotaniaAPI.addSubTileToCreativeMenu("mythicbotany_aquapanthus");
        BotaniaAPI.registerSubTile("mythicbotany_hellebore", MythicFlowerSubTiles.Hellebore.class);
        BotaniaAPI.registerSubTileSignature(MythicFlowerSubTiles.Hellebore.class,
                new MythicFlowerSignature("mythicbotany_hellebore",
                        "tooltip.mythicbotany.flower.hellebore"));
        BotaniaAPI.addSubTileToCreativeMenu("mythicbotany_hellebore");
        BotaniaAPI.registerSubTile("mythicbotany_raindeletia", MythicFlowerSubTiles.Raindeletia.class);
        BotaniaAPI.registerSubTileSignature(MythicFlowerSubTiles.Raindeletia.class,
                new MythicFlowerSignature("mythicbotany_raindeletia",
                        "tooltip.mythicbotany.flower.raindeletia"));
        BotaniaAPI.addSubTileToCreativeMenu("mythicbotany_raindeletia");
        BotaniaAPI.registerSubTile("mythicbotany_feysythia", MythicFlowerSubTiles.Feysythia.class);
        BotaniaAPI.registerSubTileSignature(MythicFlowerSubTiles.Feysythia.class,
                new MythicFlowerSignature("mythicbotany_feysythia",
                        "tooltip.mythicbotany.flower.feysythia"));
        BotaniaAPI.addSubTileToCreativeMenu("mythicbotany_feysythia");
        BotaniaAPI.registerSubTile("mythicbotany_petrunia", MythicFlowerSubTiles.Petrunia.class);
        BotaniaAPI.registerSubTileSignature(MythicFlowerSubTiles.Petrunia.class,
                new MythicFlowerSignature("mythicbotany_petrunia",
                        "tooltip.mythicbotany.flower.petrunia"));
        BotaniaAPI.addSubTileToCreativeMenu("mythicbotany_petrunia");
    }

    private static void registerOreDictionary() {
        OreDictionary.registerOre("ingotAlfsteel", new ItemStack(ModItems.alfsteelIngot));
        OreDictionary.registerOre("nuggetAlfsteel", new ItemStack(ModItems.alfsteelNugget));
        OreDictionary.registerOre("blockAlfsteel", new ItemStack(ModBlocks.alfsteelBlock));
        OreDictionary.registerOre("oreElementium", new ItemStack(ModBlocks.elementiumOre));
        OreDictionary.registerOre("oreGold", new ItemStack(ModBlocks.goldOre));
        // Dragonstone must never be selected by Orechid/Orechid Ignem.
        BotaniaAPI.oreWeights.remove("oreDragonstone");
        BotaniaAPI.oreWeightsNether.remove("oreDragonstone");
    }

    private static void registerSpecialFlowerRecipes() {
        registerSpecialFlowerSubTiles();

        ItemStack red = petal(14);
        ItemStack orange = petal(1);
        ItemStack yellow = petal(4);
        ItemStack green = petal(13);
        ItemStack lime = petal(5);
        ItemStack blue = petal(11);
        ItemStack cyan = petal(9);
        ItemStack lightBlue = petal(3);
        ItemStack purple = petal(10);
        ItemStack magenta = petal(2);
        ItemStack black = petal(15);
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
