package mythicbotany.lexicon;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mythicbotany.MythicBotany;
import mythicbotany.recipe.InfuserRecipe;
import mythicbotany.recipe.YggdrasilBranchRecipe;
import mythicbotany.registry.ModItems;
import mythicbotany.rune.RuneRitualRecipe;
import mythicbotany.rune.RuneRitualRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeManaInfusion;
import vazkii.botania.api.recipe.RecipePetals;
import vazkii.botania.api.recipe.RecipeRuneAltar;
import vazkii.botania.api.lexicon.LexiconCategory;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconPage;
import vazkii.botania.api.lexicon.LexiconRecipeMappings;
import vazkii.botania.common.lexicon.page.PageCraftingRecipe;
import vazkii.botania.common.lexicon.page.PageText;

/** Lexicon categories, entries, and text keys ported from MythicBotany upstream. */
public final class MythicLexicon {
    private static final String EMPTY_PAGE = "";
    private static boolean registered;
    private static final Map<String, LexiconEntry> ENTRIES = new HashMap<>();
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
        registerItemMappings();
        registerRecipePages();
        registerCraftingRecipePages();
    }

    private static void add(LexiconCategory category, String name, String iconId, String... pageKeys) {
        LexiconEntry entry = new LexiconEntry(name, category);
        entry.setIcon(icon(iconId));
        LexiconPage[] pages;
        if (name.endsWith(".runes")) {
            // Pages 2-10 are recipe pages. The old text-only pages were in a
            // different order from the rune recipes and caused wrong captions.
            pages = new LexiconPage[] {new PageText(pageKeys[0])};
        } else if (name.endsWith(".alfheim_landscape")) {
            // Use the same full-page image presentation as Botania's
            // Hydroangeas entry. PageMythicImage scales the 512px resources to
            // the legacy lexicon page instead of showing only their corner.
            pages = new LexiconPage[] {
                    new PageText(pageKeys[0]),
                    new PageMythicImage(pageKeys[1], pageKeys[2],
                            "mythicbotany:textures/image/alfheim_hills.png"),
                    new PageMythicImage(pageKeys[3], pageKeys[4],
                            "mythicbotany:textures/image/dreamwood_forest.png"),
                    new PageMythicImage(pageKeys[5], pageKeys[6],
                            "mythicbotany:textures/image/golden_fields.png")
            };
        } else if (name.endsWith(".andwari")) {
            pages = new LexiconPage[] {
                    new PageText(pageKeys[0]),
                    new PageMythicImage(pageKeys[1], pageKeys[2],
                            "mythicbotany:textures/image/andwari_entrance.png"),
                    new PageMythicImage(pageKeys[1], pageKeys[3],
                            "mythicbotany:textures/image/andwari_cave.png"),
                    new PageText(pageKeys[4])
            };
        } else {
            pages = new LexiconPage[pageKeys.length];
            for (int i = 0; i < pageKeys.length; i++) pages[i] = new PageText(pageKeys[i]);
        }
        entry.setLexiconPages(pages);
        BotaniaAPI.addEntry(entry, category);
        ENTRIES.put(name.substring(name.lastIndexOf('.') + 1), entry);
    }

    /**
     * PageText entries do not create the item-to-entry links used by the
     * Botania lexicon search.  Register every MythicBotany item explicitly so
     * Ctrl-click/Shift-click works for blocks, tools, rings and materials too.
     * The mappings are deliberately installed after the item registry has been
     * populated, which is when this class is called by the mod lifecycle.
     */
    private static void registerItemMappings() {
        for (Item item : Item.REGISTRY) {
            ResourceLocation registryName = item.getRegistryName();
            if (registryName == null) continue;
            String name = registryName.toString();
            String prefix = MythicBotany.MODID + ":";
            if (!name.startsWith(prefix)) continue;
            LexiconEntry entry = entryFor(name.substring(prefix.length()));
            if (entry == null) continue;

            NonNullList<ItemStack> variants = NonNullList.create();
            item.getSubItems(CreativeTabs.SEARCH, variants);
            if (variants.isEmpty()) {
                LexiconRecipeMappings.map(new ItemStack(item), entry, 0, true);
            } else {
                for (ItemStack variant : variants) {
                    LexiconRecipeMappings.map(variant, entry, 0, true);
                }
            }
        }

        // Ancient Wills are Botania metadata variants but are documented by
        // MythicBotany's armour entry.
        Item ancientWill = Item.REGISTRY.getObject(new ResourceLocation("botania", "ancientwill"));
        if (ancientWill != null) {
            for (int meta = 0; meta <= 5; meta++) map(new ItemStack(ancientWill, 1, meta), "tools");
        }

        // Mythic flowers are stored as Botania's specialflower with a type tag.
        Item specialFlower = Item.REGISTRY.getObject(new ResourceLocation("botania", "specialflower"));
        if (specialFlower != null) {
            mapFlower(specialFlower, "mythicbotany_exoblaze", "functional");
            mapFlower(specialFlower, "mythicbotany_wither_aconite", "generating");
            mapFlower(specialFlower, "mythicbotany_aquapanthus", "functional");
            mapFlower(specialFlower, "mythicbotany_hellebore", "functional");
            mapFlower(specialFlower, "mythicbotany_raindeletia", "functional");
            mapFlower(specialFlower, "mythicbotany_petrunia", "functional");
        }
    }

    private static void registerRecipePages() {
        // Lexicon pages are a snapshot of the built-in recipes. CraftTweaker
        // recipes remain available in JEI, but must not leak into the lexicon.
        LexiconEntry mimir = find("mimir");
        if (mimir != null) {
            for (YggdrasilBranchRecipe recipe : YggdrasilBranchRecipe.getDefaultRecipes()) {
                addRecipePage(mimir, new PageYggdrasilBranchRecipe(
                        EMPTY_PAGE, recipe.getInput(), recipe.getOutput(), recipe.getMana()));
            }
            for (RecipeManaInfusion recipe : BotaniaAPI.manaInfusionRecipes) {
                if (recipe.getOutput().getItem() == ModItems.gjallarHornEmpty) {
                    addRecipePage(mimir, BotaniaAPI.internalHandler.manaInfusionRecipePage(
                            EMPTY_PAGE,
                            recipe));
                }
            }
        }

        LexiconEntry infuser = find("infuser");
        if (infuser != null) {
            for (InfuserRecipe recipe : InfuserRecipe.getDefaultRecipes()) {
                addRecipePage(infuser, new PageMythicInfuserRecipe(
                        EMPTY_PAGE, recipe.getOutput(), recipe.getMana(), recipe.getInputs()));
            }
        }

        LexiconEntry rituals = find("rune_rituals");
        if (rituals != null) {
            for (RuneRitualRecipe recipe : RuneRitualRegistry.getDefaultRecipes()) {
                ItemStack output = recipe.getOutput();
                if (!output.isEmpty()) {
                    addRecipePage(rituals, new PageRitualOutput(
                            EMPTY_PAGE,
                            "lexicon.entry.mythicbotany.botania.mythic_botany.rune_rituals.page3.text0",
                            output));
                }
            }
        }

        // The explanatory text occupies pages 1-4 / 1-3. Keep recipes on the
        // requested following pages, with no caption text on those pages.
        addPetalRecipePage("functional", "mythicbotany_aquapanthus", EMPTY_PAGE);
        addPetalRecipePage("functional", "mythicbotany_exoblaze", EMPTY_PAGE);
        addPetalRecipePage("functional", "mythicbotany_hellebore", EMPTY_PAGE);
        addPetalRecipePage("functional", "mythicbotany_petrunia", EMPTY_PAGE);
        addPetalRecipePage("generating", "mythicbotany_wither_aconite", EMPTY_PAGE);
        addPetalRecipePage("generating", "mythicbotany_raindeletia", EMPTY_PAGE);

        // Recipe captions intentionally match the rune item, not the old page
        // order from the JSON data.
        addRuneRecipePage("runes", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page1.text0", ModItems.asgardRune);
        addRuneRecipePage("runes", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page2.text0", ModItems.vanaheimRune);
        addRuneRecipePage("runes", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page3.text0", ModItems.alfheimRune);
        addRuneRecipePage("runes", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page4.text0", ModItems.midgardRune);
        addRuneRecipePage("runes", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page5.text0", ModItems.joetunheimRune);
        addRuneRecipePage("runes", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page6.text0", ModItems.nidavellirRune);
        addRuneRecipePage("runes", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page7.text0", ModItems.helheimRune);
        addRuneRecipePage("runes", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page8.text0", ModItems.muspelheimRune);
        addRuneRecipePage("runes", "lexicon.entry.mythicbotany.botania.mythic_botany.runes.page9.text0", ModItems.niflheimRune);
    }

    /** Adds custom Forge recipes that are not already represented by the static lexicon JSON. */
    private static void registerCraftingRecipePages() {
        addCraftingRecipePage(find("tools"),
                EMPTY_PAGE,
                "alfsteel_pick_elementium", "alfsteel_helmet_upgrade",
                "alfsteel_chestplate_upgrade", "alfsteel_leggings_upgrade", "alfsteel_boots_upgrade",
                "alfsteel_sword_upgrade", "alfsteel_axe_upgrade", "alfsteel_pick_upgrade");
        addCraftingRecipePage(find("manaband"),
                EMPTY_PAGE,
                "mana_ring_greatest_upgrade", "aura_ring_greatest_upgrade");
        addCraftingRecipePage(find("rings"),
                EMPTY_PAGE,
                "fire_ring", "ice_ring");
        addCraftingRecipePage(find("infuser"),
                EMPTY_PAGE,
                "alfsteel_ingots", "alfsteel_nuggets", "alfsteel_block");
    }

    private static void addCraftingRecipePage(LexiconEntry entry, String textKey, String... names) {
        if (entry == null) {
            return;
        }
        for (String name : names) {
            ResourceLocation id = new ResourceLocation(MythicBotany.MODID, name);
            if (ForgeRegistries.RECIPES.getValue(id) != null) {
                // Keep each recipe on its own page.  The Botania page renderer
                // lays out all recipes in a supplied list at once, which makes
                // the longer alfsteel and ring recipes overflow the page.
                addRecipePage(entry, new PageCraftingRecipe(textKey, id));
            }
        }
    }

    private static void addPetalRecipePage(String entrySuffix, String flowerType, String pageName) {
        LexiconEntry entry = find(entrySuffix);
        if (entry == null) return;
        for (RecipePetals recipe : BotaniaAPI.petalRecipes) {
            ItemStack output = recipe.getOutput();
            NBTTagCompound tag = output.getTagCompound();
            if (tag != null && flowerType.equals(tag.getString("type"))) {
                addRecipePage(entry, BotaniaAPI.internalHandler.petalRecipePage(pageName, recipe));
                return;
            }
        }
    }

    private static void addRuneRecipePage(String entrySuffix, String pageName, Item outputItem) {
        LexiconEntry entry = find(entrySuffix);
        if (entry == null) return;
        for (RecipeRuneAltar recipe : BotaniaAPI.runeAltarRecipes) {
            if (recipe.getOutput().getItem() == outputItem) {
                addRecipePage(entry, BotaniaAPI.internalHandler.runeRecipePage(pageName, recipe));
                return;
            }
        }
    }

    private static void addImagePage(String entrySuffix, String pageName, String texture) {
        LexiconEntry entry = find(entrySuffix);
        if (entry != null && BotaniaAPI.internalHandler != null) {
            addRecipePage(entry, BotaniaAPI.internalHandler.imagePage(pageName, texture));
        }
    }

    private static void addReflectiveRecipePages(LexiconEntry entry, Iterable<?> recipes, String pageName) {
        if (entry == null || recipes == null) return;
        for (Object recipe : recipes) {
            if (recipe == null) continue;
            List<ItemStack> stacks = extractStacks(recipe);
            if (stacks.isEmpty()) continue;

            List<ItemStack> declaredOutputs = extractStacks(invokeFirst(recipe,
                    "getOutput", "getOutputs", "getResult", "getResults", "getResultItem"));
            ItemStack output = declaredOutputs.isEmpty()
                    ? stacks.get(stacks.size() - 1) : declaredOutputs.get(0);
            removeMatchingStack(stacks, output);
            int mana = number(invokeFirst(recipe, "getMana", "getManaUsage", "getManaCost"));
            addRecipePage(entry, new PageMythicRecipe(pageName, output, mana,
                    stacks.toArray(new ItemStack[stacks.size()])));
        }
    }

    private static void addRecipePage(LexiconEntry entry, LexiconPage page) {
        if (entry == null || page == null) return;
        int pageIndex = entry.pages.size();
        entry.addPage(page);
        page.onPageAdded(entry, pageIndex);
    }

    private static Object invokeFirst(Object target, String... methodNames) {
        for (String methodName : methodNames) {
            try {
                return target.getClass().getMethod(methodName).invoke(target);
            } catch (ReflectiveOperationException ignored) {
                // Recipe implementations have changed names between legacy revisions.
            }
        }
        return null;
    }

    private static int number(Object value) {
        return value instanceof Number ? Math.max(0, ((Number) value).intValue()) : 0;
    }

    private static List<ItemStack> extractStacks(Object value) {
        List<ItemStack> result = new ArrayList<>();
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());
        collectStacks(value, result, visited, 0);
        return result;
    }

    private static void collectStacks(Object value, List<ItemStack> result,
                                      Set<Object> visited, int depth) {
        if (value == null || depth > 5) return;
        if (value instanceof ItemStack) {
            ItemStack stack = (ItemStack) value;
            if (!stack.isEmpty()) result.add(stack.copy());
            return;
        }
        if (value instanceof Iterable<?>) {
            for (Object child : (Iterable<?>) value) collectStacks(child, result, visited, depth + 1);
            return;
        }
        Class<?> type = value.getClass();
        if (type.isArray()) {
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                collectStacks(Array.get(value, i), result, visited, depth + 1);
            }
            return;
        }
        Package packageInfo = type.getPackage();
        if (packageInfo == null || !packageInfo.getName().startsWith("mythicbotany")) return;
        if (!visited.add(value)) return;
        for (Field field : type.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            try {
                field.setAccessible(true);
                collectStacks(field.get(value), result, visited, depth + 1);
            } catch (ReflectiveOperationException | SecurityException ignored) {
                // A recipe may contain an implementation detail that is not readable.
            }
        }
    }

    private static void removeMatchingStack(List<ItemStack> stacks, ItemStack target) {
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack candidate = stacks.get(i);
            if (candidate.getItem() == target.getItem()
                    && candidate.getMetadata() == target.getMetadata()
                    && ItemStack.areItemStackTagsEqual(candidate, target)) {
                stacks.remove(i);
                return;
            }
        }
    }

    private static LexiconEntry entryFor(String path) {
        if (path.contains("gjallar") || path.contains("yggdrasil") || path.contains("branch")) return find("mimir");
        if (path.contains("mjoellnir")) return find("mjoellnir");
        if (path.contains("kvasir")) return find("kvasir");
        if (path.contains("andwari")) return find("andwari");
        if (path.contains("pylon")) return find("pylons");
        if (path.contains("mana_ring") || path.contains("aura_ring")) return find("manaband");
        if (path.endsWith("fire_ring") || path.endsWith("ice_ring")) return find("rings");
        if (path.contains("rune_holder") || path.contains("central_rune") || path.contains("fimbultyr")) return find("rune_rituals");
        if (path.contains("rune")) return find("runes");
        if (path.contains("helmet") || path.contains("chestplate") || path.contains("leggings") || path.contains("boots")
                || path.contains("sword") || path.contains("pick") || path.contains("axe") || path.contains("shovel")
                || path.contains("breaker")) return find("tools");
        if (path.contains("ore") || path.contains("dream") || path.contains("pixie") || path.contains("dragonstone")
                || path.contains("gold")) return find("alfheim_resources");
        if (path.contains("return_portal")) return find("alfheim_landscape");
        if (path.contains("infuser") || path.contains("alfsteel")) return find("infuser");
        return find("tools");
    }

    private static LexiconEntry find(String suffix) {
        return ENTRIES.get(suffix);
    }

    private static void mapFlower(Item item, String type, String entrySuffix) {
        ItemStack stack = new ItemStack(item);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("type", type);
        stack.setTagCompound(tag);
        map(stack, entrySuffix);
    }

    private static void map(ItemStack stack, String entrySuffix) {
        LexiconEntry entry = find(entrySuffix);
        if (entry != null) LexiconRecipeMappings.map(stack, entry, 0, true);
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
