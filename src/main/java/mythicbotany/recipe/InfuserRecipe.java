package mythicbotany.recipe;

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

public final class InfuserRecipe {
    private static final String RESOURCE_ROOT = "assets/mythicbotany/infusion_recipes/";
    private static final List<InfuserRecipe> RECIPES = new ArrayList<>();
    private static boolean resourcesLoaded;
    private final ItemStack input;
    private final ItemStack output;
    private final int mana;

    private InfuserRecipe(ItemStack input, ItemStack output, int mana) {
        this.input = input.copy();
        this.output = output.copy();
        this.mana = Math.max(0, mana);
    }

    public static void register(ItemStack input, ItemStack output, int mana) {
        if (input != null && output != null && !input.isEmpty() && !output.isEmpty()) {
            RECIPES.add(new InfuserRecipe(input, output, mana));
        }
    }

    public static synchronized void loadResources() {
        if (resourcesLoaded) {
            return;
        }
        resourcesLoaded = true;
        try (InputStream indexStream = resource(RESOURCE_ROOT + "index.json")) {
            if (indexStream == null) {
                warn("Missing Infuser recipe index");
                return;
            }
            JsonElement index = new JsonParser().parse(new InputStreamReader(indexStream, StandardCharsets.UTF_8));
            JsonArray names = index.isJsonArray() ? index.getAsJsonArray()
                    : index.getAsJsonObject().getAsJsonArray("recipes");
            if (names == null) {
                warn("Infuser recipe index has no recipes array");
                return;
            }
            for (JsonElement name : names) {
                loadRecipe(name.getAsString());
            }
        } catch (Exception exception) {
            warn("Unable to load Infuser recipes: " + exception.getMessage());
        }
    }

    private static void loadRecipe(String name) {
        try (InputStream stream = resource(RESOURCE_ROOT + name + ".json")) {
            if (stream == null) {
                warn("Missing Infuser recipe: " + name);
                return;
            }
            JsonObject recipe = new JsonParser()
                    .parse(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            ItemStack input = readStack(recipe, "input");
            ItemStack output = readStack(recipe, "output");
            if (input.isEmpty() || output.isEmpty() || !recipe.has("mana")) {
                warn("Invalid Infuser recipe: " + name);
                return;
            }
            register(input, output, recipe.get("mana").getAsInt());
        } catch (Exception exception) {
            warn("Unable to load Infuser recipe " + name + ": " + exception.getMessage());
        }
    }

    private static ItemStack readStack(JsonObject recipe, String key) {
        JsonElement element = recipe.get(key);
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
        return InfuserRecipe.class.getClassLoader().getResourceAsStream(path);
    }

    private static void warn(String message) {
        if (MythicBotany.logger != null) {
            MythicBotany.logger.warn(message);
        } else {
            System.err.println("[MythicBotany] " + message);
        }
    }

    public static InfuserRecipe find(ItemStack stack) {
        loadResources();
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        for (InfuserRecipe recipe : RECIPES) {
            if (recipe.input.getItem() == stack.getItem()
                    && (recipe.input.getMetadata() == stack.getMetadata() || recipe.input.getMetadata() == 32767)) {
                return recipe;
            }
        }
        return null;
    }

    public static List<InfuserRecipe> getRecipes() {
        loadResources();
        return Collections.unmodifiableList(RECIPES);
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public ItemStack getInput() {
        return input.copy();
    }

    public int getMana() {
        return mana;
    }
}
