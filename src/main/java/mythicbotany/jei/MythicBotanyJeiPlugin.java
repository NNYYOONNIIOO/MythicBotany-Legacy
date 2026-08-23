package mythicbotany.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mythicbotany.MythicBotany;
import mythicbotany.recipe.InfuserRecipe;
import mythicbotany.registry.ModBlocks;
import mythicbotany.rune.RuneRitualRecipe;
import mythicbotany.rune.RuneRitualRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** JEI 4.x integration for the 1.12.2 mana infuser and rune holders. */
@JEIPlugin
public final class MythicBotanyJeiPlugin implements IModPlugin {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(
            MythicBotany.MODID, "textures/gui/jei_ritual.png");

    @Override
    public void register(IModRegistry registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        InfuserCategory infuserCategory = new InfuserCategory(guiHelper);
        RitualCategory ritualCategory = new RitualCategory(guiHelper);
        registry.addRecipeCategories(infuserCategory, ritualCategory);

        List<InfuserWrapper> infuserRecipes = new ArrayList<>();
        for (InfuserRecipe recipe : InfuserRecipe.getRecipes()) {
            infuserRecipes.add(new InfuserWrapper(recipe));
        }
        registry.addRecipes(infuserRecipes, InfuserCategory.UID);

        List<RitualWrapper> ritualRecipes = new ArrayList<>();
        for (RuneRitualRecipe recipe : RuneRitualRegistry.getRecipes()) {
            ritualRecipes.add(new RitualWrapper(recipe));
        }
        registry.addRecipes(ritualRecipes, RitualCategory.UID);

        registry.addRecipeCatalyst(new ItemStack(ModBlocks.manaInfuser), InfuserCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.centralRuneHolder), RitualCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.runeHolder), RitualCategory.UID);
    }

    private static IDrawable background(IGuiHelper helper, int height) {
        return helper.createDrawable(BACKGROUND, 0, 0, 160, height);
    }

    private static final class InfuserCategory implements IRecipeCategory<InfuserWrapper> {
        private static final String UID = MythicBotany.MODID + ":infuser";
        private final IDrawable background;
        private final IDrawable icon;

        private InfuserCategory(IGuiHelper helper) {
            background = background(helper, 64);
            icon = helper.createDrawableIngredient(new ItemStack(ModBlocks.manaInfuser));
        }

        @Override public String getUid() { return UID; }
        @Override public String getTitle() { return I18n.format("jei.mythicbotany.infuser"); }
        @Override public String getModName() { return MythicBotany.NAME; }
        @Override public IDrawable getBackground() { return background; }
        @Override public IDrawable getIcon() { return icon; }

        @Override
        public void setRecipe(IRecipeLayout layout, InfuserWrapper wrapper, IIngredients ingredients) {
            IGuiItemStackGroup stacks = layout.getItemStacks();
            stacks.init(0, true, 25, 25);
            stacks.init(1, false, 117, 25);
            stacks.set(0, wrapper.recipe.getInput());
            stacks.set(1, wrapper.recipe.getOutput());
        }
    }

    private static final class RitualCategory implements IRecipeCategory<RitualWrapper> {
        private static final String UID = MythicBotany.MODID + ":ritual";
        private final IDrawable background;
        private final IDrawable icon;

        private RitualCategory(IGuiHelper helper) {
            background = background(helper, 96);
            icon = helper.createDrawableIngredient(new ItemStack(ModBlocks.centralRuneHolder));
        }

        @Override public String getUid() { return UID; }
        @Override public String getTitle() { return I18n.format("jei.mythicbotany.ritual"); }
        @Override public String getModName() { return MythicBotany.NAME; }
        @Override public IDrawable getBackground() { return background; }
        @Override public IDrawable getIcon() { return icon; }

        @Override
        public void setRecipe(IRecipeLayout layout, RitualWrapper wrapper, IIngredients ingredients) {
            IGuiItemStackGroup stacks = layout.getItemStacks();
            stacks.init(0, true, 70, 38);
            stacks.init(1, false, 124, 38);
            stacks.set(0, wrapper.recipe.getCenter());
            stacks.set(1, wrapper.recipe.getOutput());
            int slot = 2;
            for (RuneRitualRecipe.RunePosition rune : wrapper.recipe.getRunes()) {
                int x = 70 + rune.getOriginalX() * 18;
                int y = 38 + rune.getOriginalZ() * 18;
                stacks.init(slot, true, x, y);
                stacks.set(slot, rune.getRune());
                slot++;
            }
        }
    }

    private static final class InfuserWrapper implements IRecipeWrapper {
        private final InfuserRecipe recipe;
        private InfuserWrapper(InfuserRecipe recipe) { this.recipe = recipe; }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInput(ItemStack.class, recipe.getInput());
            ingredients.setOutput(ItemStack.class, recipe.getOutput());
        }

        @Override
        public void drawInfo(Minecraft minecraft, int width, int height, int mouseX, int mouseY) {
            minecraft.fontRenderer.drawString(I18n.format("jei.mythicbotany.mana", recipe.getMana()),
                    4, height - 12, 0x404040);
        }
    }

    private static final class RitualWrapper implements IRecipeWrapper {
        private final RuneRitualRecipe recipe;
        private RitualWrapper(RuneRitualRecipe recipe) { this.recipe = recipe; }

        @Override
        public void getIngredients(IIngredients ingredients) {
            List<List<ItemStack>> inputs = new ArrayList<>();
            inputs.add(Collections.singletonList(recipe.getCenter()));
            for (RuneRitualRecipe.RunePosition rune : recipe.getRunes()) {
                inputs.add(Collections.singletonList(rune.getRune()));
            }
            ingredients.setInputLists(ItemStack.class, inputs);
            ingredients.setOutput(ItemStack.class, recipe.getOutput());
        }

        @Override
        public void drawInfo(Minecraft minecraft, int width, int height, int mouseX, int mouseY) {
            minecraft.fontRenderer.drawString(I18n.format("jei.mythicbotany.mana", recipe.getMana()),
                    4, height - 20, 0x404040);
            minecraft.fontRenderer.drawString(recipe.getTicks() + " t", 4, height - 10, 0x404040);
        }
    }
}
