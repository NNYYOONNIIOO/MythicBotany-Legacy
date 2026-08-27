package mythicbotany.rune;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mythicbotany.MythicBotany;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.nbt.JsonToNBT;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RuneRitualRegistry {
    private static final String RESOURCE_ROOT = "assets/mythicbotany/rune_ritual_recipes/";
    private static final List<RuneRitualRecipe> RECIPES = new ArrayList<>();
    private static final List<RuneRitualRecipe> DEFAULT_RECIPES = new ArrayList<>();
    private static boolean resourcesLoaded;

    private RuneRitualRegistry() {
    }

    public static synchronized void loadResources() {
        if (resourcesLoaded) {
            return;
        }
        resourcesLoaded = true;
        try (InputStream indexStream = resource(RESOURCE_ROOT + "index.json")) {
            if (indexStream == null) {
                warn("Missing Rune Ritual recipe index");
                return;
            }
            JsonElement index = new JsonParser().parse(new InputStreamReader(indexStream, StandardCharsets.UTF_8));
            JsonArray names = index.isJsonArray() ? index.getAsJsonArray()
                    : index.getAsJsonObject().getAsJsonArray("recipes");
            if (names == null) {
                warn("Rune Ritual recipe index has no recipes array");
                return;
            }
            for (JsonElement name : names) {
                loadRecipe(name.getAsString());
            }
        } catch (Exception exception) {
            warn("Unable to load Rune Ritual recipes: " + exception.getMessage());
        }
    }

    private static void loadRecipe(String name) {
        try (InputStream stream = resource(RESOURCE_ROOT + name + ".json")) {
            if (stream == null) {
                warn("Missing Rune Ritual recipe: " + name);
                return;
            }
            JsonObject recipe = new JsonParser()
                    .parse(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            ItemStack center = readStack(recipe.get("center"));
            JsonArray runesJson = recipe.getAsJsonArray("runes");
            if (center.isEmpty() || runesJson == null) {
                warn("Invalid Rune Ritual recipe: " + name);
                return;
            }

            List<ItemStack> outputs = new ArrayList<>();
            JsonElement outputsElement = recipe.get("outputs");
            if (outputsElement != null && outputsElement.isJsonArray()) {
                for (JsonElement element : outputsElement.getAsJsonArray()) {
                    ItemStack stack = readStack(element);
                    if (!stack.isEmpty()) {
                        outputs.add(stack);
                    }
                }
            } else {
                ItemStack output = readStack(recipe.get("output"));
                if (!output.isEmpty()) {
                    outputs.add(output);
                }
            }
            String specialOutput = property(recipe, "special_output");
            if (outputs.isEmpty() && specialOutput != null) {
                net.minecraft.item.Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(specialOutput));
                if (item != null) {
                    outputs.add(new ItemStack(item));
                }
            }

            List<RuneRitualRecipe.InputRequirement> inputs = new ArrayList<>();
            JsonElement inputsElement = recipe.get("inputs");
            if (inputsElement != null && inputsElement.isJsonArray()) {
                for (JsonElement element : inputsElement.getAsJsonArray()) {
                    List<ItemStack> alternatives = readAlternatives(element);
                    if (alternatives.isEmpty()) {
                        warn("Invalid input in Rune Ritual recipe: " + name);
                        return;
                    }
                    inputs.add(RuneRitualRecipe.InputRequirement.of(
                            alternatives.toArray(new ItemStack[0])));
                }
            }

            List<RuneRitualRecipe.RunePosition> runes = new ArrayList<>();
            for (JsonElement element : runesJson) {
                if (!element.isJsonObject()) {
                    warn("Invalid rune in Rune Ritual recipe: " + name);
                    return;
                }
                JsonObject rune = element.getAsJsonObject();
                JsonElement runeElement = rune.has("rune") ? rune.get("rune") : rune.get("stack");
                ItemStack stack = readStack(runeElement);
                if (stack.isEmpty() || !rune.has("x") || !rune.has("z")) {
                    warn("Invalid rune in Rune Ritual recipe: " + name);
                    return;
                }
                boolean consume = rune.has("consume") && rune.get("consume").getAsBoolean();
                runes.add(RuneRitualRecipe.rune(rune.get("x").getAsInt(),
                        rune.get("z").getAsInt(), stack, consume));
            }

            registerDefault(new RuneRitualRecipe(center, outputs,
                    recipe.has("mana") ? recipe.get("mana").getAsInt() : 0,
                    recipe.has("ticks") ? recipe.get("ticks").getAsInt() : 200,
                    inputs, readSpecialInputs(recipe), specialOutput,
                    runes.toArray(new RuneRitualRecipe.RunePosition[0])));
        } catch (Exception exception) {
            warn("Unable to load Rune Ritual recipe " + name + ": " + exception.getMessage());
        }
    }

    private static String property(JsonObject object, String key) {
        return object.has(key) && !object.get(key).isJsonNull() ? object.get(key).getAsString() : null;
    }

    private static List<String> readSpecialInputs(JsonObject recipe) {
        List<String> inputs = new ArrayList<>();
        JsonElement element = recipe.get("special_input");
        if (element == null || element.isJsonNull()) {
            return inputs;
        }
        if (element.isJsonArray()) {
            for (JsonElement value : element.getAsJsonArray()) {
                if (value.isJsonPrimitive()) {
                    inputs.add(value.getAsString());
                }
            }
        } else if (element.isJsonPrimitive()) {
            inputs.add(element.getAsString());
        }
        return inputs;
    }

    private static List<ItemStack> readAlternatives(JsonElement element) {
        List<ItemStack> stacks = new ArrayList<>();
        if (element == null) {
            return stacks;
        }
        if (element.isJsonArray()) {
            for (JsonElement alternative : element.getAsJsonArray()) {
                stacks.addAll(readAlternatives(alternative));
            }
            return stacks;
        }
        if (!element.isJsonObject()) {
            return stacks;
        }
        JsonObject object = element.getAsJsonObject();
        if (object.has("tag")) {
            String tag = object.get("tag").getAsString();
            String legacy = legacyOreName(tag);
            List<ItemStack> ores = OreDictionary.getOres(legacy, false);
            if (ores.isEmpty() && !legacy.equals(tag)) {
                ores = OreDictionary.getOres(tag, false);
            }
            for (ItemStack stack : ores) {
                if (stack != null && !stack.isEmpty()) {
                    stacks.add(stack.copy());
                }
            }
        } else {
            ItemStack stack = readStack(element);
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }
        return stacks;
    }

    private static String legacyOreName(String tag) {
        if (!tag.startsWith("forge:")) {
            return tag;
        }
        String path = tag.substring("forge:".length());
        int slash = path.indexOf('/');
        if (slash < 0 || slash == path.length() - 1) {
            return tag;
        }
        String category = path.substring(0, slash);
        String material = path.substring(slash + 1);
        if ("ingots".equals(category)) {
            return "ingot" + capitalize(material);
        }
        if ("nuggets".equals(category)) {
            return "nugget" + capitalize(material);
        }
        if ("gems".equals(category)) {
            return "gem" + capitalize(material);
        }
        if ("dusts".equals(category)) {
            return "dust" + capitalize(material);
        }
        return tag;
    }

    private static String capitalize(String value) {
        return value.isEmpty() ? value : Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    private static ItemStack readStack(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return ItemStack.EMPTY;
        }
        JsonObject object = element.getAsJsonObject();
        if (object.has("tag") || !object.has("item")) {
            return ItemStack.EMPTY;
        }
        net.minecraft.item.Item item = ForgeRegistries.ITEMS.getValue(
                new ResourceLocation(object.get("item").getAsString()));
        if (item == null) {
            return ItemStack.EMPTY;
        }
        int count = object.has("count") ? object.get("count").getAsInt() : 1;
        int meta = object.has("data") ? object.get("data").getAsInt()
                : (object.has("meta") ? object.get("meta").getAsInt() : 0);
        ItemStack stack = new ItemStack(item, Math.max(1, count), meta);
        if (object.has("nbt")) {
            try {
                JsonElement nbt = object.get("nbt");
                String nbtText = nbt.isJsonPrimitive() ? nbt.getAsString() : nbt.toString();
                stack.setTagCompound(JsonToNBT.getTagFromJson(nbtText));
            } catch (Exception ignored) {
                warn("Invalid NBT in Rune Ritual item: " + object.get("item").getAsString());
            }
        }
        return stack;
    }

    private static InputStream resource(String path) {
        return RuneRitualRegistry.class.getClassLoader().getResourceAsStream(path);
    }

    private static void warn(String message) {
        if (MythicBotany.logger != null) {
            MythicBotany.logger.warn(message);
        } else {
            System.err.println("[MythicBotany] " + message);
        }
    }

    public static void registerDefaults() {
        loadResources();
    }

    public static void register(RuneRitualRecipe recipe) {
        if (recipe != null) {
            RECIPES.add(recipe);
        }
    }

    private static void registerDefault(RuneRitualRecipe recipe) {
        if (recipe != null) {
            RECIPES.add(recipe);
            DEFAULT_RECIPES.add(recipe);
        }
    }

    public static List<RuneRitualRecipe> getRecipes() {
        loadResources();
        return Collections.unmodifiableList(RECIPES);
    }

    /** Recipes shipped by MythicBotany itself; CraftTweaker additions are excluded. */
    public static List<RuneRitualRecipe> getDefaultRecipes() {
        loadResources();
        return Collections.unmodifiableList(new ArrayList<>(DEFAULT_RECIPES));
    }

    public static RuneRitualRecipe getRecipe(int index) {
        List<RuneRitualRecipe> recipes = getRecipes();
        return index >= 0 && index < recipes.size() ? recipes.get(index) : null;
    }

    public static int indexOf(RuneRitualRecipe recipe) {
        return getRecipes().indexOf(recipe);
    }
}
