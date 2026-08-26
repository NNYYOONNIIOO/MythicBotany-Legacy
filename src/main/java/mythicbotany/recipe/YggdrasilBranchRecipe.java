package mythicbotany.recipe;

import mythicbotany.registry.ModItems;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A one-input, one-output recipe processed by a Yggdrasil branch. */
public final class YggdrasilBranchRecipe {
    public static final int MANA_PER_TICK = 10;
    public static final int DEFAULT_MANA = 6000;
    private static final List<YggdrasilBranchRecipe> RECIPES = new ArrayList<>();
    private static boolean defaultsRegistered;

    private final ItemStack input;
    private final ItemStack output;
    private final int mana;

    private YggdrasilBranchRecipe(ItemStack input, ItemStack output, int mana) {
        this.input = input.copy();
        this.input.setCount(1);
        this.output = output.copy();
        this.mana = Math.max(0, mana);
    }

    public static synchronized void registerDefaults() {
        if (defaultsRegistered) {
            return;
        }
        defaultsRegistered = true;
        register(new ItemStack(ModItems.gjallarHornEmpty),
                new ItemStack(ModItems.gjallarHornFull), DEFAULT_MANA);
    }

    public static synchronized void register(ItemStack input, ItemStack output, int mana) {
        if (input == null || input.isEmpty() || output == null || output.isEmpty()) {
            return;
        }
        YggdrasilBranchRecipe recipe = new YggdrasilBranchRecipe(input, output, mana);
        for (int i = 0; i < RECIPES.size(); i++) {
            if (RECIPES.get(i).sameInput(recipe.input)) {
                RECIPES.set(i, recipe);
                return;
            }
        }
        RECIPES.add(recipe);
    }

    public static YggdrasilBranchRecipe find(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        for (int i = RECIPES.size() - 1; i >= 0; i--) {
            YggdrasilBranchRecipe recipe = RECIPES.get(i);
            if (recipe.matchesInput(stack)) {
                return recipe;
            }
        }
        return null;
    }

    private boolean sameInput(ItemStack stack) {
        return input.getItem() == stack.getItem()
                && (input.getMetadata() == 32767 || input.getMetadata() == stack.getMetadata())
                && (!input.hasTagCompound() || ItemStack.areItemStackTagsEqual(input, stack));
    }

    public boolean matchesInput(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getCount() >= input.getCount()
                && sameInput(stack);
    }

    public ItemStack getInput() {
        return input.copy();
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public int getMana() {
        return mana;
    }

    public int getTicks() {
        return Math.max(1, (mana + MANA_PER_TICK - 1) / MANA_PER_TICK);
    }

    public int getManaForProgress(int progress) {
        return Math.max(0, Math.min(MANA_PER_TICK, mana - progress * MANA_PER_TICK));
    }

    public static List<YggdrasilBranchRecipe> getRecipes() {
        return Collections.unmodifiableList(new ArrayList<>(RECIPES));
    }

    public static YggdrasilBranchRecipe getRecipe(int index) {
        return index >= 0 && index < RECIPES.size() ? RECIPES.get(index) : null;
    }

    public static int indexOf(YggdrasilBranchRecipe recipe) {
        return recipe == null ? -1 : RECIPES.indexOf(recipe);
    }
}
