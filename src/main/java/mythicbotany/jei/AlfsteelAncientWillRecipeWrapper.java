package mythicbotany.jei;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import mythicbotany.MythicBotany;
import mythicbotany.registry.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.api.item.IAncientWillContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/** JEI crafting view for attaching Botania's Ancient Wills to the Alfsteel helmet. */
public final class AlfsteelAncientWillRecipeWrapper implements ICustomCraftingRecipeWrapper {
    private final ResourceLocation name = new ResourceLocation(
            MythicBotany.MODID, "alfsteel_ancient_will");
    private final List<List<ItemStack>> inputs;
    private final List<ItemStack> outputs;

    public AlfsteelAncientWillRecipeWrapper() {
        ImmutableList.Builder<List<ItemStack>> inputBuilder = ImmutableList.builder();
        ImmutableList.Builder<ItemStack> helmets = ImmutableList.builder();
        ImmutableList.Builder<ItemStack> wills = ImmutableList.builder();
        helmets.add(new ItemStack(ModItems.alfsteelHelmet));
        for (int i = 0; i < 6; i++) {
            wills.add(new ItemStack(vazkii.botania.common.item.ModItems.ancientWill, 1, i));
        }
        outputs = helmets.build();
        inputBuilder.add(outputs);
        inputBuilder.add(wills.build());
        inputs = inputBuilder.build();
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, inputs);
        ingredients.setOutputLists(VanillaTypes.ITEM, ImmutableList.of(outputs));
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return name;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout,
                          @Nonnull IIngredients ingredients) {
        IFocus<?> focus = recipeLayout.getFocus();
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        group.set(ingredients);

        if (focus == null || !(focus.getValue() instanceof ItemStack)) {
            return;
        }
        ItemStack focused = (ItemStack) focus.getValue();
        if (focus.getMode() == IFocus.Mode.INPUT
                && focused.getItem() == vazkii.botania.common.item.ModItems.ancientWill) {
            group.set(2, new ItemStack(vazkii.botania.common.item.ModItems.ancientWill,
                    1, focused.getMetadata()));
            group.set(0, getHelmetWithWill(focused.getMetadata()));
        } else if (focused.getItem() == ModItems.alfsteelHelmet) {
            group.set(1, new ItemStack(ModItems.alfsteelHelmet));
            group.set(0, getHelmetsWithWills());
        }
    }

    private List<ItemStack> getHelmetWithWill(int will) {
        ItemStack helmet = new ItemStack(ModItems.alfsteelHelmet);
        ((IAncientWillContainer) helmet.getItem()).addAncientWill(helmet, will);
        return ImmutableList.of(helmet);
    }

    private List<ItemStack> getHelmetsWithWills() {
        ImmutableList.Builder<ItemStack> result = ImmutableList.builder();
        Item item = ModItems.alfsteelHelmet;
        for (int i = 0; i < 6; i++) {
            ItemStack helmet = new ItemStack(item);
            ((IAncientWillContainer) item).addAncientWill(helmet, i);
            result.add(helmet);
        }
        return result.build();
    }
}
