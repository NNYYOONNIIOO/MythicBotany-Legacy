package mythicbotany.rune;

import mythicbotany.registry.ModItems;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RuneRitualRegistry {
    private static final List<RuneRitualRecipe> RECIPES = new ArrayList<>();
    private static boolean defaultsRegistered;

    private RuneRitualRegistry() {
    }

    public static void registerDefaults() {
        if (defaultsRegistered) {
            return;
        }
        defaultsRegistered = true;
        register(new RuneRitualRecipe(new ItemStack(ModItems.alfsteelTemplate),
                new ItemStack(ModItems.alfsteelIngot), 50000, 100,
                RuneRitualRecipe.rune(-2, 0, new ItemStack(ModItems.asgardRune)),
                RuneRitualRecipe.rune(2, 0, new ItemStack(ModItems.vanaheimRune)),
                RuneRitualRecipe.rune(0, -2, new ItemStack(ModItems.alfheimRune)),
                RuneRitualRecipe.rune(0, 2, new ItemStack(ModItems.midgardRune))));
        register(new RuneRitualRecipe(new ItemStack(ModItems.fadedNetherStar),
                new ItemStack(ModItems.alfsteelTemplate), 25000, 80,
                RuneRitualRecipe.rune(-2, -2, new ItemStack(ModItems.joetunheimRune)),
                RuneRitualRecipe.rune(2, -2, new ItemStack(ModItems.muspelheimRune)),
                RuneRitualRecipe.rune(-2, 2, new ItemStack(ModItems.niflheimRune)),
                RuneRitualRecipe.rune(2, 2, new ItemStack(ModItems.helheimRune))));
        register(new RuneRitualRecipe(new ItemStack(ModItems.fimbultyrTablet),
                new ItemStack(ModItems.alfsteelNugget, 4), 15000, 60,
                RuneRitualRecipe.rune(-2, 0, new ItemStack(ModItems.nidavellirRune)),
                RuneRitualRecipe.rune(2, 0, new ItemStack(ModItems.vanaheimRune)),
                RuneRitualRecipe.rune(0, -2, new ItemStack(ModItems.asgardRune)),
                RuneRitualRecipe.rune(0, 2, new ItemStack(ModItems.alfheimRune))));
    }

    public static void register(RuneRitualRecipe recipe) {
        if (recipe != null) {
            RECIPES.add(recipe);
        }
    }

    public static List<RuneRitualRecipe> getRecipes() {
        registerDefaults();
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
