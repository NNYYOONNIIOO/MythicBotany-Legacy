package mythicbotany.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mythicbotany.MythicBotany;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class InfuserRecipe {
    public static final int MAX_INPUTS = 16;
    private static final String RESOURCE_ROOT = "assets/mythicbotany/infusion_recipes/";
    private static final List<InfuserRecipe> RECIPES = new ArrayList<>();
    private static boolean resourcesLoaded;
    private final List<ItemStack> inputs;
    private final ItemStack output;
    private final int mana;

    private InfuserRecipe(List<ItemStack> inputs, ItemStack output, int mana) {
        List<ItemStack> copies = new ArrayList<>();
        for (ItemStack input : inputs) {
            copies.add(input.copy());
        }
        this.inputs = Collections.unmodifiableList(copies);
        this.output = output.copy();
        this.mana = Math.max(0, mana);
    }

    public static void register(ItemStack input, ItemStack output, int mana) {
        if (input != null && output != null && !input.isEmpty() && !output.isEmpty()) {
            register(Collections.singletonList(input), output, mana);
        }
    }

    public static synchronized void register(List<ItemStack> inputs, ItemStack output, int mana) {
        if (inputs == null || inputs.isEmpty() || inputs.size() > MAX_INPUTS
                || output == null || output.isEmpty()) {
            return;
        }
        for (ItemStack input : inputs) {
            if (input == null || input.isEmpty()) {
                return;
            }
        }
        RECIPES.add(new InfuserRecipe(inputs, output, mana));
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
            List<ItemStack> inputs = new ArrayList<>();
            JsonElement inputsElement = recipe.get("inputs");
            if (inputsElement != null && inputsElement.isJsonArray()) {
                for (JsonElement element : inputsElement.getAsJsonArray()) {
                    ItemStack input = readStack(element);
                    if (!input.isEmpty()) {
                        inputs.add(input);
                    }
                }
            } else {
                ItemStack input = readStack(recipe, "input");
                if (!input.isEmpty()) {
                    inputs.add(input);
                }
            }
            ItemStack output = readStack(recipe, "output");
            if (inputs.isEmpty() || output.isEmpty() || !recipe.has("mana")) {
                warn("Invalid Infuser recipe: " + name);
                return;
            }
            register(inputs, output, recipe.get("mana").getAsInt());
        } catch (Exception exception) {
            warn("Unable to load Infuser recipe " + name + ": " + exception.getMessage());
        }
    }

    private static ItemStack readStack(JsonObject recipe, String key) {
        JsonElement element = recipe.get(key);
        return readStack(element);
    }

    private static ItemStack readStack(JsonElement element) {
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
        for (int i = RECIPES.size() - 1; i >= 0; i--) {
            InfuserRecipe recipe = RECIPES.get(i);
            if (recipe.inputs.size() == 1 && recipe.matches(stack)) {
                return recipe;
            }
        }
        return null;
    }

    public static InfuserRecipe find(List<EntityItem> entities) {
        loadResources();
        for (int i = RECIPES.size() - 1; i >= 0; i--) {
            InfuserRecipe recipe = RECIPES.get(i);
            if (recipe.matches(entities)) {
                return recipe;
            }
        }
        return null;
    }

    public boolean matches(ItemStack stack) {
        return inputs.size() == 1 && stack != null && !stack.isEmpty()
                && matchesStack(inputs.get(0), stack)
                && inputs.get(0).getCount() <= stack.getCount();
    }

    public boolean matches(List<EntityItem> entities) {
        return getConsumption(entities) != null;
    }

    public Map<EntityItem, Integer> getConsumption(List<EntityItem> entities) {
        if (entities == null) {
            return null;
        }
        Map<EntityItem, Integer> available = new LinkedHashMap<>();
        for (EntityItem entity : entities) {
            if (entity != null && !entity.getItem().isEmpty()) {
                available.put(entity, entity.getItem().getCount());
            }
        }
        Map<EntityItem, Integer> consumed = new LinkedHashMap<>();
        for (ItemStack expected : inputs) {
            int needed = Math.max(1, expected.getCount());
            for (Map.Entry<EntityItem, Integer> entry : available.entrySet()) {
                if (needed <= 0) {
                    break;
                }
                if (entry.getValue() <= 0 || !matchesStack(expected, entry.getKey().getItem())) {
                    continue;
                }
                int take = Math.min(needed, entry.getValue());
                entry.setValue(entry.getValue() - take);
                consumed.put(entry.getKey(), consumed.containsKey(entry.getKey())
                        ? consumed.get(entry.getKey()) + take : take);
                needed -= take;
            }
            if (needed > 0) {
                return null;
            }
        }
        for (EntityItem entity : available.keySet()) {
            if (!consumed.containsKey(entity)) {
                return null;
            }
        }
        return consumed;
    }

    private static boolean matchesStack(ItemStack expected, ItemStack actual) {
        return expected != null && actual != null && !expected.isEmpty() && !actual.isEmpty()
                && expected.getItem() == actual.getItem()
                && (expected.getMetadata() == 32767 || expected.getMetadata() == actual.getMetadata())
                && (!expected.hasTagCompound() || ItemStack.areItemStackTagsEqual(expected, actual));
    }

    public static List<InfuserRecipe> getRecipes() {
        loadResources();
        return Collections.unmodifiableList(RECIPES);
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public ItemStack getInput() {
        return inputs.isEmpty() ? ItemStack.EMPTY : inputs.get(0).copy();
    }

    public List<ItemStack> getInputs() {
        List<ItemStack> copies = new ArrayList<>();
        for (ItemStack input : inputs) {
            copies.add(input.copy());
        }
        return copies;
    }

    public int getMana() {
        return mana;
    }

    public static InfuserRecipe getRecipe(int index) {
        List<InfuserRecipe> recipes = getRecipes();
        return index >= 0 && index < recipes.size() ? recipes.get(index) : null;
    }

    public static int indexOf(InfuserRecipe recipe) {
        return getRecipes().indexOf(recipe);
    }
}
