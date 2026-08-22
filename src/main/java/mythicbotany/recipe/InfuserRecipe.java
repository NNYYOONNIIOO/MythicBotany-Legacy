package mythicbotany.recipe;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class InfuserRecipe {
    private static final List<InfuserRecipe> RECIPES = new ArrayList<>();
    private final ItemStack input;
    private final ItemStack output;
    private final int mana;

    private InfuserRecipe(ItemStack input, ItemStack output, int mana) {
        this.input = input.copy();
        this.output = output.copy();
        this.mana = mana;
    }

    public static void register(ItemStack input, ItemStack output, int mana) {
        RECIPES.add(new InfuserRecipe(input, output, mana));
    }

    public static InfuserRecipe find(ItemStack stack) {
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

    public ItemStack getOutput() {
        return output.copy();
    }

    public int getMana() {
        return mana;
    }
}
