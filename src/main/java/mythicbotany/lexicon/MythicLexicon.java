package mythicbotany.lexicon;

import mythicbotany.MythicBotany;
import mythicbotany.registry.ModBlocks;
import mythicbotany.registry.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.LexiconCategory;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconPage;
import vazkii.botania.common.lexicon.page.PageText;

/** Registers MythicBotany entries with Botania's 1.12.2 Lexicon API. */
public final class MythicLexicon {
    private static boolean registered;

    private MythicLexicon() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        LexiconCategory category = new LexiconCategory("mythicbotany.lexicon.category")
                .setPriority(6)
                .setIcon(new ResourceLocation(MythicBotany.MODID,
                        "textures/items/alfsteel_ingot.png"));
        BotaniaAPI.addCategory(category);

        addEntry(category, "overview", new ItemStack(ModItems.alfsteelIngot), 2, true);
        addEntry(category, "mana", new ItemStack(ModItems.manaRingGreatest), 2, false);
        addEntry(category, "infuser", new ItemStack(ModBlocks.manaInfuser), 2, false);
        addEntry(category, "pylon", new ItemStack(ModBlocks.alfsteelPylon), 2, false);
        addEntry(category, "alfsteel", new ItemStack(ModItems.alfsteelChestplate), 3, false);
        addEntry(category, "pixie", new ItemStack(ModItems.alfPixieSpawnEgg), 2, false);
        addEntry(category, "rings", new ItemStack(ModItems.auraRingGreatest), 2, false);
        addEntry(category, "runes", new ItemStack(ModItems.asgardRune), 2, false);
    }

    private static void addEntry(LexiconCategory category, String name, ItemStack icon,
                                 int pageCount, boolean priority) {
        LexiconEntry entry = new LexiconEntry("mythicbotany.lexicon.entry." + name, category);
        if (priority) {
            entry.setPriority();
        }
        entry.setIcon(icon);

        LexiconPage[] pages = new LexiconPage[pageCount];
        for (int i = 0; i < pageCount; i++) {
            pages[i] = new PageText("mythicbotany.lexicon.page." + name + "." + i);
        }
        entry.setLexiconPages(pages);
        BotaniaAPI.addEntry(entry, category);
    }
}
