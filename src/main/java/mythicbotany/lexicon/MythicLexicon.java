package mythicbotany.lexicon;

import mythicbotany.MythicBotany;
import mythicbotany.registry.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.LexiconCategory;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.common.lexicon.page.PageText;

/** Lexicon categories, entries, and text keys ported from MythicBotany upstream. */
public final class MythicLexicon {
    private static boolean registered;
    private MythicLexicon() { }
    public static void register() {
        if (registered) return;
        registered = true;
        LexiconCategory category0 = new LexiconCategory("lexicon.category.mythicbotany.botania.alfheim")
                .setPriority(6)
                .setIcon(new ResourceLocation(MythicBotany.MODID, "textures/items/dream_cherry.png"));
        BotaniaAPI.addCategory(category0);
        add(category0, "lexicon.entry.mythicbotany.botania.alfheim.alfheim_landscape", "minecraft:spawn_egg{EntityTag:{id:\"mythicbotany:alf_pixie\"}}", "lexicon.entry.mythicbotany.botania.alfheim.alfheim_landscape.page0.text0", "lexicon.entry.mythicbotany.botania.alfheim.alfheim_landscape.page1.text0", "lexicon.entry.mythicbotany.botania.alfheim.alfheim_landscape.page1.text1", "lexicon.entry.mythicbotany.botania.alfheim.alfheim_landscape.page2.text0", "lexicon.entry.mythicbotany.botania.alfheim.alfheim_landscape.page2.text1", "lexicon.entry.mythicbotany.botania.alfheim.alfheim_landscape.page3.text0", "lexicon.entry.mythicbotany.botania.alfheim.alfheim_landscape.page3.text1");
        add(category0, "lexicon.entry.mythicbotany.botania.alfheim.alfheim_resources", "mythicbotany:elementium_ore", "lexicon.entry.mythicbotany.botania.alfheim.alfheim_resources.page0.text0", "lexicon.entry.mythicbotany.botania.alfheim.alfheim_resources.page1.text0", "lexicon.entry.mythicbotany.botania.alfheim.alfheim_resources.page2.text0", "lexicon.entry.mythicbotany.botania.alfheim.alfheim_resources.page3.text0", "lexicon.entry.mythicbotany.botania.alfheim.alfheim_resources.page4.text0", "lexicon.entry.mythicbotany.botania.alfheim.alfheim_resources.page5.text0");
        add(category0, "lexicon.entry.mythicbotany.botania.alfheim.andwari", "mythicbotany:andwari_ring{Damage:0}", "lexicon.entry.mythicbotany.botania.alfheim.andwari.page0.text0", "lexicon.entry.mythicbotany.botania.alfheim.andwari.page1.text0", "lexicon.entry.mythicbotany.botania.alfheim.andwari.page1.text1", "lexicon.entry.mythicbotany.botania.alfheim.andwari.page3.text0", "lexicon.entry.mythicbotany.botania.alfheim.andwari.page4.text0");
        add(category0, "lexicon.entry.mythicbotany.botania.alfheim.kvasir", "mythicbotany:kvasir_mead", "lexicon.entry.mythicbotany.botania.alfheim.kvasir.page0.text0", "lexicon.entry.mythicbotany.botania.alfheim.kvasir.page1.text0", "lexicon.entry.mythicbotany.botania.alfheim.kvasir.page3.text0");
        LexiconCategory category1 = new LexiconCategory("lexicon.category.mythicbotany.botania.mythic_botany")
                .setPriority(6)
                .setIcon(new ResourceLocation(MythicBotany.MODID, "textures/items/alfsteel_sword.png"));
        BotaniaAPI.addCategory(category1);
        add(category1, "lexicon.entry.mythicbotany.botania.mythic_botany.functional", "botania:specialflower{type:mythicbotany_hellebore}", "lexicon.entry.mythicbotany.botania.mythic_botany.functional.page1.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.functional.page3.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.functional.page5.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.functional.page7.text0");
        add(category1, "lexicon.entry.mythicbotany.botania.mythic_botany.generating", "botania:specialflower{type:mythicbotany_wither_aconite}", "lexicon.entry.mythicbotany.botania.mythic_botany.generating.page1.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.generating.page3.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.generating.page5.text0");
        add(category1, "lexicon.entry.mythicbotany.botania.mythic_botany.infuser", "mythicbotany:alfsteel_ingot", "lexicon.entry.mythicbotany.botania.mythic_botany.infuser.page0.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.infuser.page1.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.infuser.page2.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.infuser.page2.text1", "lexicon.entry.mythicbotany.botania.mythic_botany.infuser.page3.text0");
        add(category1, "lexicon.entry.mythicbotany.botania.mythic_botany.manaband", "mythicbotany:mana_ring_greatest", "lexicon.entry.mythicbotany.botania.mythic_botany.manaband.page0.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.manaband.page1.text0");
        add(category1, "lexicon.entry.mythicbotany.botania.mythic_botany.mimir", "mythicbotany:gjallar_horn_full", "lexicon.entry.mythicbotany.botania.mythic_botany.mimir.page0.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.mimir.page2.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.mimir.page3.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.mimir.page4.text0");
        add(category1, "lexicon.entry.mythicbotany.botania.mythic_botany.mjoellnir", "mythicbotany:mjoellnir", "lexicon.entry.mythicbotany.botania.mythic_botany.mjoellnir.page0.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.mjoellnir.page1.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.mjoellnir.page2.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.mjoellnir.page5.text0");
        add(category1, "lexicon.entry.mythicbotany.botania.mythic_botany.pylons", "mythicbotany:alfsteel_pylon", "lexicon.entry.mythicbotany.botania.mythic_botany.pylons.page0.text0");
        add(category1, "lexicon.entry.mythicbotany.botania.mythic_botany.rings", "mythicbotany:fire_ring", "lexicon.entry.mythicbotany.botania.mythic_botany.rings.page0.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.rings.page1.text0");
        add(category1, "lexicon.entry.mythicbotany.botania.mythic_botany.rune_rituals", "mythicbotany:fimbultyr_tablet", "lexicon.entry.mythicbotany.botania.mythic_botany.rune_rituals.page0.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.rune_rituals.page1.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.rune_rituals.page3.text0");
        add(category1, "lexicon.entry.mythicbotany.botania.mythic_botany.runes", "mythicbotany:niflheim_rune", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page0.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page1.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page2.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page3.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page4.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page5.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page6.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page7.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page8.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page9.text0");
        add(category1, "lexicon.entry.mythicbotany.botania.mythic_botany.tools", "mythicbotany:alfsteel_axe{Damage:0}", "lexicon.entry.mythicbotany.botania.mythic_botany.tools.page0.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.tools.page1.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.tools.page2.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.tools.page3.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.tools.page4.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.tools.page5.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.tools.page6.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.tools.page7.text0", "lexicon.entry.mythicbotany.botania.mythic_botany.tools.page8.text0");
    }

    private static void add(LexiconCategory category, String name, String iconId, String... pageKeys) {
        LexiconEntry entry = new LexiconEntry(name, category);
        entry.setIcon(icon(iconId));
        PageText[] pages = new PageText[pageKeys.length];
        for (int i = 0; i < pageKeys.length; i++) pages[i] = new PageText(pageKeys[i]);
        entry.setLexiconPages(pages);
        BotaniaAPI.addEntry(entry, category);
    }

    private static ItemStack icon(String raw) {
        int brace = raw.indexOf("{");
        String id = brace < 0 ? raw : raw.substring(0, brace);
        Item item = Item.REGISTRY.getObject(new ResourceLocation(id));
        if (item == null) return new ItemStack(ModItems.alfsteelIngot);
        ItemStack stack = new ItemStack(item);
        if ("minecraft:spawn_egg".equals(id) && raw.contains("mythicbotany:alf_pixie")) {
            ItemMonsterPlacer.applyEntityIdToItemStack(stack,
                    new ResourceLocation(MythicBotany.MODID, "alf_pixie"));
            return stack;
        }
        int end = raw.lastIndexOf('}');
        if (brace >= 0 && end > brace) {
            String data = raw.substring(brace + 1, end);
            int typeIndex = data.indexOf("type:");
            if (typeIndex >= 0) {
                String type = data.substring(typeIndex + 5).replace("\"", "").trim();
                net.minecraft.nbt.NBTTagCompound tag = new net.minecraft.nbt.NBTTagCompound();
                tag.setString("type", type);
                stack.setTagCompound(tag);
            }
            int damageIndex = data.indexOf("Damage:");
            if (damageIndex >= 0) {
                String damage = data.substring(damageIndex + 7).replaceAll("[^0-9].*", "");
                if (!damage.isEmpty()) stack.setItemDamage(Integer.parseInt(damage));
            }
        }
        return stack;
    }
}
