package mythicbotany.recipe;

import javax.annotation.Nonnull;

import mythicbotany.registry.ModItems;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

/**
 * Converts a Botania item into its Alfsteel counterpart without dropping any
 * state stored on the original stack.  The recipe is deliberately dynamic at
 * result time because durability, enchantments, mana, and other NBT are all
 * properties of the source stack rather than of the recipe itself.
 */
public final class RecipeAlfsteelUpgrade extends ShapelessRecipes {
    private final Item sourceItem;
    private final Item outputItem;

    public RecipeAlfsteelUpgrade(Item sourceItem, Item outputItem) {
        super("mythicbotany", new ItemStack(outputItem), createIngredients(sourceItem));
        this.sourceItem = sourceItem;
        this.outputItem = outputItem;
    }

    private static NonNullList<Ingredient> createIngredients(Item sourceItem) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.fromItem(sourceItem));
        ingredients.add(Ingredient.fromItem(ModItems.alfsteelTemplate));
        ingredients.add(Ingredient.fromItem(ModItems.alfsteelIngot));
        return ingredients;
    }

    @Override
    public boolean matches(@Nonnull InventoryCrafting inventory, @Nonnull World world) {
        boolean sourceFound = false;
        boolean templateFound = false;
        boolean ingotFound = false;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            Item item = stack.getItem();
            if (item == sourceItem) {
                if (sourceFound) {
                    return false;
                }
                sourceFound = true;
            } else if (item == ModItems.alfsteelTemplate) {
                if (templateFound) {
                    return false;
                }
                templateFound = true;
            } else if (item == ModItems.alfsteelIngot) {
                if (ingotFound) {
                    return false;
                }
                ingotFound = true;
            } else {
                return false;
            }
        }
        return sourceFound && templateFound && ingotFound;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inventory) {
        ItemStack source = ItemStack.EMPTY;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() == sourceItem) {
                source = stack;
                break;
            }
        }
        return copyAttributes(source, new ItemStack(outputItem));
    }

    /** Copies every transferable stack property used by the legacy items. */
    static ItemStack copyAttributes(ItemStack source, ItemStack output) {
        if (source == null || source.isEmpty()) {
            return output;
        }
        output.setItemDamage(source.getItemDamage());
        output.setTagCompound(source.hasTagCompound()
                ? source.getTagCompound().copy() : null);
        return output;
    }
}
