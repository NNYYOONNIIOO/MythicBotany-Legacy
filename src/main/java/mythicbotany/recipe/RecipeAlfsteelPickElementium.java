package mythicbotany.recipe;

import mythicbotany.item.ItemAlfsteelPick;
import mythicbotany.registry.ModItems;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

/** Produces a tipped Alfsteel pick and rejects an already tipped input. */
public class RecipeAlfsteelPickElementium extends ShapelessRecipes {
    private final Item elementiumPick;

    public RecipeAlfsteelPickElementium(Item elementiumPick) {
        super("mythicbotany", createOutput(), createIngredients(elementiumPick));
        this.elementiumPick = elementiumPick;
    }

    private static ItemStack createOutput() {
        ItemStack output = new ItemStack(ModItems.alfsteelPick);
        ItemAlfsteelPick.setTipped(output, true);
        return output;
    }

    private static NonNullList<Ingredient> createIngredients(Item elementiumPick) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.fromItem(ModItems.alfsteelPick));
        ingredients.add(Ingredient.fromItem(elementiumPick));
        return ingredients;
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        int pickCount = 0;
        int elementiumCount = 0;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() == ModItems.alfsteelPick) {
                if (ItemAlfsteelPick.isTipped(stack) || ++pickCount > 1) {
                    return false;
                }
            } else if (stack.getItem() == elementiumPick) {
                if (++elementiumCount > 1) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return pickCount == 1 && elementiumCount == 1;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack output = super.getCraftingResult(inventory);
        ItemAlfsteelPick.setTipped(output, true);
        return output;
    }
}
