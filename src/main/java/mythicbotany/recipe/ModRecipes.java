package mythicbotany.recipe;

import mythicbotany.MythicBotany;
import mythicbotany.registry.ModBlocks;
import mythicbotany.registry.ModItems;
import mythicbotany.rune.RuneRitualRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
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
        registerOreDictionary();
        registerSmelting();
        registerEquipmentRecipes();
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

    private static void registerSmelting() {
        GameRegistry.addSmelting(new ItemStack(ModBlocks.elementiumOre),
                new ItemStack(ModItems.rawElementium), 1.0F);
        GameRegistry.addSmelting(new ItemStack(ModBlocks.dragonstoneOre),
                new ItemStack(vazkii.botania.common.item.ModItems.manaResource, 1, 7), 1.0F);
        GameRegistry.addSmelting(new ItemStack(ModItems.rawElementium),
                new ItemStack(ModItems.alfsteelNugget, 2), 0.5F);
    }

    private static void registerEquipmentRecipes() {
        GameRegistry.addShapedRecipe(id("alfsteel_axe"), group(), new ItemStack(ModItems.alfsteelAxe),
                "II", "IS", " S", 'I', ModItems.alfsteelIngot, 'S', Items.STICK);
        GameRegistry.addShapedRecipe(id("alfsteel_helmet"), group(), new ItemStack(ModItems.alfsteelHelmet),
                "III", "I I", 'I', ModItems.alfsteelIngot);
        GameRegistry.addShapedRecipe(id("alfsteel_chestplate"), group(), new ItemStack(ModItems.alfsteelChestplate),
                "I I", "III", "III", 'I', ModItems.alfsteelIngot);
        GameRegistry.addShapedRecipe(id("alfsteel_leggings"), group(), new ItemStack(ModItems.alfsteelLeggings),
                "III", "I I", "I I", 'I', ModItems.alfsteelIngot);
        GameRegistry.addShapedRecipe(id("alfsteel_boots"), group(), new ItemStack(ModItems.alfsteelBoots),
                "I I", "I I", 'I', ModItems.alfsteelIngot);
        GameRegistry.addShapedRecipe(id("mana_ring_greatest"), group(), new ItemStack(ModItems.manaRingGreatest),
                " M ", "MIM", " M ", 'M', vazkii.botania.common.item.ModItems.manaResource,
                'I', ModItems.alfsteelIngot);
        GameRegistry.addShapedRecipe(id("aura_ring_greatest"), group(), new ItemStack(ModItems.auraRingGreatest),
                " M ", "MIM", " M ", 'M', vazkii.botania.common.item.ModItems.manaResource,
                'I', ModItems.alfsteelNugget);
        GameRegistry.addShapedRecipe(id("mjoellnir"), group(), new ItemStack(ModItems.mjoellnir),
                " A ", "AEA", " S ", 'A', ModItems.alfsteelIngot, 'E', Items.NETHER_STAR, 'S', Items.STICK);
        GameRegistry.addShapedRecipe(id("andwari_ring"), group(), new ItemStack(ModItems.andwariRing),
                " G ", "GFG", " G ", 'G', Items.GOLD_INGOT, 'F', ModItems.fadedNetherStar);
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

    private static ResourceLocation id(String name) {
        return new ResourceLocation(MythicBotany.MODID, name);
    }

    private static ResourceLocation group() {
        return new ResourceLocation(MythicBotany.MODID, "recipes");
    }
}
