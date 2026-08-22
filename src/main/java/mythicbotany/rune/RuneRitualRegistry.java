package mythicbotany.rune;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mythicbotany.MythicBotany;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RuneRitualRegistry {
    private static final String RESOURCE_ROOT = "assets/mythicbotany/rune_ritual_recipes/";
    private static final List<RuneRitualRecipe> RECIPES = new ArrayList<>();
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
            ItemStack center = readStack(recipe, "center");
            ItemStack output = readStack(recipe, "output");
            JsonArray runesJson = recipe.getAsJsonArray("runes");
            if (center.isEmpty() || output.isEmpty() || runesJson == null
                    || !recipe.has("mana") || !recipe.has("ticks")) {
                warn("Invalid Rune Ritual recipe: " + name);
                return;
            }
            List<RuneRitualRecipe.RunePosition> runes = new ArrayList<>();
            for (JsonElement element : runesJson) {
                JsonObject rune = element.getAsJsonObject();
                ItemStack stack = readStack(rune, "stack");
                if (stack.isEmpty() || !rune.has("x") || !rune.has("z")) {
                    warn("Invalid rune in Rune Ritual recipe: " + name);
                    return;
                }
                runes.add(RuneRitualRecipe.rune(rune.get("x").getAsInt(),
                        rune.get("z").getAsInt(), stack));
            }
            register(new RuneRitualRecipe(center, output, recipe.get("mana").getAsInt(),
                    recipe.get("ticks").getAsInt(),
                    runes.toArray(new RuneRitualRecipe.RunePosition[0])));
        } catch (Exception exception) {
            warn("Unable to load Rune Ritual recipe " + name + ": " + exception.getMessage());
        }
    }

    private static ItemStack readStack(JsonObject parent, String key) {
        JsonElement element = parent.get(key);
        if (element == null || !element.isJsonObject()) {
            return ItemStack.EMPTY;
        }
        JsonObject object = element.getAsJsonObject();
        if (!object.has("item")) {
            return ItemStack.EMPTY;
        }
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(object.get("item").getAsString()));
        if (item == null) {
            return ItemStack.EMPTY;
        }
        int count = object.has("count") ? object.get("count").getAsInt() : 1;
        int meta = object.has("data") ? object.get("data").getAsInt()
                : (object.has("meta") ? object.get("meta").getAsInt() : 0);
        return new ItemStack(item, Math.max(1, count), meta);
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

    public static List<RuneRitualRecipe> getRecipes() {
        loadResources();
        return Collections.unmodifiableList(RECIPES);
    }

    public static RuneRitualRecipe getRecipe(int index) {
        List<RuneRitualRecipe> recipes = getRecipes();
        return index >= 0 && index < recipes.size() ? recipes.get(index) : null;
    }

    public static int indexOf(RuneRitualRecipe recipe) {
        return getRecipes().indexOf(recipe);
    }
}
