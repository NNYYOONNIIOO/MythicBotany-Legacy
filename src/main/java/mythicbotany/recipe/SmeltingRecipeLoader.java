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
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/** Loads the 1.12.2-compatible JSON description of smelting recipes. */
public final class SmeltingRecipeLoader {
    private static final String RESOURCE_ROOT = "assets/mythicbotany/smelting_recipes/";
    private static boolean loaded;

    private SmeltingRecipeLoader() {
    }

    public static synchronized void loadResources() {
        if (loaded) {
            return;
        }
        loaded = true;
        try (InputStream indexStream = resource(RESOURCE_ROOT + "index.json")) {
            if (indexStream == null) {
                warn("Missing smelting recipe index");
                return;
            }
            JsonElement index = new JsonParser()
                    .parse(new InputStreamReader(indexStream, StandardCharsets.UTF_8));
            JsonArray names = index.isJsonArray() ? index.getAsJsonArray()
                    : index.getAsJsonObject().getAsJsonArray("recipes");
            if (names == null) {
                warn("Smelting recipe index has no recipes array");
                return;
            }
            for (JsonElement name : names) {
                loadRecipe(name.getAsString());
            }
        } catch (Exception exception) {
            warn("Unable to load smelting recipes: " + exception.getMessage());
        }
    }

    private static void loadRecipe(String name) {
        try (InputStream stream = resource(RESOURCE_ROOT + name + ".json")) {
            if (stream == null) {
                warn("Missing smelting recipe: " + name);
                return;
            }
            JsonObject recipe = new JsonParser()
                    .parse(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            ItemStack input = readStack(recipe.getAsJsonObject("ingredient"));
            ItemStack output = readStack(recipe.getAsJsonObject("result"));
            if (input.isEmpty() || output.isEmpty()) {
                warn("Invalid smelting recipe: " + name);
                return;
            }
            float experience = recipe.has("experience")
                    ? recipe.get("experience").getAsFloat() : 0.0F;
            GameRegistry.addSmelting(input, output, experience);
        } catch (Exception exception) {
            warn("Unable to load smelting recipe " + name + ": " + exception.getMessage());
        }
    }

    private static ItemStack readStack(JsonObject object) {
        if (object == null || !object.has("item")) {
            return ItemStack.EMPTY;
        }
        Item item = ForgeRegistries.ITEMS.getValue(
                new ResourceLocation(object.get("item").getAsString()));
        if (item == null) {
            return ItemStack.EMPTY;
        }
        int count = object.has("count") ? object.get("count").getAsInt() : 1;
        int data = object.has("data") ? object.get("data").getAsInt()
                : (object.has("meta") ? object.get("meta").getAsInt() : 0);
        return new ItemStack(item, Math.max(1, count), data);
    }

    private static InputStream resource(String path) {
        return SmeltingRecipeLoader.class.getClassLoader().getResourceAsStream(path);
    }

    private static void warn(String message) {
        if (MythicBotany.logger != null) {
            MythicBotany.logger.warn(message);
        } else {
            System.err.println("[MythicBotany] " + message);
        }
    }
}
