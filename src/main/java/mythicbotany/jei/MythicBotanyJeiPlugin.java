package mythicbotany.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mythicbotany.MythicBotany;
import mythicbotany.recipe.InfuserRecipe;
import mythicbotany.registry.ModBlocks;
import mythicbotany.registry.ModItems;
import mythicbotany.rune.RuneRitualRecipe;
import mythicbotany.rune.RuneRitualRegistry;
import mythicbotany.tile.TileYggdrasilBranch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.block.tile.mana.TilePool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** JEI 4.x integration for the 1.12.2 mana infuser and rune holders. */
@JEIPlugin
public final class MythicBotanyJeiPlugin implements IModPlugin {
    private static final ResourceLocation RITUAL_BACKGROUND = new ResourceLocation(
            MythicBotany.MODID, "textures/gui/jei_ritual.png");

    @Override
    public void register(IModRegistry registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        InfuserCategory infuserCategory = new InfuserCategory(guiHelper);
        RitualCategory ritualCategory = new RitualCategory(guiHelper);
        YggdrasilBranchCategory yggdrasilBranchCategory = new YggdrasilBranchCategory(guiHelper);
        registry.addRecipeCategories(infuserCategory, ritualCategory, yggdrasilBranchCategory);

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
        registry.addRecipes(Collections.singletonList(new YggdrasilBranchWrapper()),
                YggdrasilBranchCategory.UID);

        registry.addRecipeCatalyst(new ItemStack(ModBlocks.manaInfuser), InfuserCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.centralRuneHolder), RitualCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.runeHolder), RitualCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.yggdrasilBranch),
                YggdrasilBranchCategory.UID);
    }

    private static final class InfuserCategory implements IRecipeCategory<InfuserWrapper> {
        private static final String UID = MythicBotany.MODID + ":infuser";
        private final IDrawable background;
        private final IDrawable overlay;
        private final IDrawable icon;

        private InfuserCategory(IGuiHelper helper) {
            background = helper.createBlankDrawable(114, 104);
            overlay = helper.createDrawable(new ResourceLocation("botania",
                    "textures/gui/petalOverlay.png"), 17, 11, 114, 82);
            icon = helper.createDrawableIngredient(new ItemStack(ModBlocks.manaInfuser));
        }

        @Override public String getUid() { return UID; }
        @Override public String getTitle() { return I18n.format("jei.mythicbotany.infuser"); }
        @Override public String getModName() { return MythicBotany.NAME; }
        @Override public IDrawable getBackground() { return background; }
        @Override public IDrawable getIcon() { return icon; }

        @Override
        public void drawExtras(Minecraft minecraft) {
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            overlay.draw(minecraft, 0, 4);
            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
        }

        @Override
        public void setRecipe(IRecipeLayout layout, InfuserWrapper wrapper, IIngredients ingredients) {
            IGuiItemStackGroup stacks = layout.getItemStacks();
            stacks.init(0, true, 47, 44);
            stacks.set(0, new ItemStack(ModBlocks.manaInfuser));

            stacks.init(1, true, 47, 12);
            stacks.set(1, wrapper.recipe.getInput());

            stacks.init(2, false, 86, 11);
            stacks.set(2, wrapper.recipe.getOutput());
        }
    }

    private static final class RitualCategory implements IRecipeCategory<RitualWrapper> {
        private static final String UID = MythicBotany.MODID + ":ritual";
        private final IDrawable background;
        private final IDrawable slot;
        private final IDrawable icon;

        private RitualCategory(IGuiHelper helper) {
            background = helper.createDrawable(RITUAL_BACKGROUND, 0, 0, 136, 196);
            slot = helper.getSlotDrawable();
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
            stacks.init(0, true, 62, 62);
            stacks.set(0, wrapper.recipe.getCenter());

            final int runeCount = wrapper.recipe.getRunes().size();
            int slotIndex = 1;
            for (RuneRitualRecipe.RunePosition rune : wrapper.recipe.getRunes()) {
                int x = 62 + 16 * (rune.getOriginalX() / 2);
                int y = 62 - 16 * (rune.getOriginalZ() / 2);
                stacks.init(slotIndex, true, x, y);
                stacks.set(slotIndex, rune.getRune());
                slotIndex++;
            }

            for (RuneRitualRecipe.InputRequirement input : wrapper.recipe.getInputs()) {
                stacks.init(slotIndex, true, 8 + 18 * (slotIndex - runeCount - 1), 116);
                stacks.set(slotIndex, input.getDisplayStack());
                slotIndex++;
            }

            stacks.init(slotIndex, false, 60, 170);
            stacks.setBackground(slotIndex, slot);
            stacks.set(slotIndex, wrapper.recipe.getOutput());

            stacks.addTooltipCallback(new ITooltipCallback<ItemStack>() {
                @Override
                public void onTooltip(int hoveredSlot, boolean input, ItemStack ingredient, List<String> tooltip) {
                    if (hoveredSlot > 0 && hoveredSlot <= runeCount) {
                        RuneRitualRecipe.RunePosition rune = wrapper.recipe.getRunes().get(hoveredSlot - 1);
                        tooltip.add(TextFormatting.GOLD + I18n.format(
                                "tooltip.mythicbotany.rune_offset",
                                rune.getOriginalX() / 2, rune.getOriginalZ() / 2));
                    }
                }
            });
        }
    }

    /** JEI view for filling an empty Gjallar Horn on a Yggdrasil branch. */
    private static final class YggdrasilBranchCategory
            implements IRecipeCategory<YggdrasilBranchWrapper> {
        private static final String UID = MythicBotany.MODID + ":yggdrasil_branch";
        private final IDrawable background;
        private final IDrawable overlay;
        private final IDrawable icon;

        private YggdrasilBranchCategory(IGuiHelper helper) {
            background = helper.createBlankDrawable(142, 55);
            overlay = helper.createDrawable(new ResourceLocation("botania",
                    "textures/gui/pureDaisyOverlay.png"), 0, 0, 64, 46);
            icon = helper.createDrawableIngredient(new ItemStack(ModBlocks.yggdrasilBranch));
        }

        @Override public String getUid() { return UID; }
        @Override public String getTitle() { return I18n.format("jei.mythicbotany.yggdrasil_branch"); }
        @Override public String getModName() { return MythicBotany.NAME; }
        @Override public IDrawable getBackground() { return background; }
        @Override public IDrawable getIcon() { return icon; }

        @Override
        public void drawExtras(Minecraft minecraft) {
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            overlay.draw(minecraft, 40, 0);
            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
        }

        @Override
        public void setRecipe(IRecipeLayout layout, YggdrasilBranchWrapper wrapper,
                              IIngredients ingredients) {
            IGuiItemStackGroup stacks = layout.getItemStacks();
            stacks.init(0, true, 32, 12);
            stacks.set(0, wrapper.input);
            stacks.init(1, true, 62, 12);
            stacks.set(1, new ItemStack(ModBlocks.yggdrasilBranch));
            stacks.init(2, false, 93, 12);
            stacks.set(2, wrapper.output);
        }
    }

    private static final class InfuserWrapper implements IRecipeWrapper {
        private final InfuserRecipe recipe;

        private InfuserWrapper(InfuserRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInput(ItemStack.class, recipe.getInput());
            ingredients.setOutput(ItemStack.class, recipe.getOutput());
        }

        @Override
        public void drawInfo(Minecraft minecraft, int width, int height, int mouseX, int mouseY) {
            HUDHandler.renderManaBar(6, height - 15, 0x0000FF, 0.75F,
                    recipe.getMana(), TilePool.MAX_MANA / 10);
        }
    }

    private static final class RitualWrapper implements IRecipeWrapper {
        private final RuneRitualRecipe recipe;

        private RitualWrapper(RuneRitualRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            List<List<ItemStack>> inputs = new ArrayList<>();
            inputs.add(Collections.singletonList(recipe.getCenter()));
            for (RuneRitualRecipe.RunePosition rune : recipe.getRunes()) {
                inputs.add(Collections.singletonList(rune.getRune()));
            }
            for (RuneRitualRecipe.InputRequirement input : recipe.getInputs()) {
                inputs.add(input.getAlternatives());
            }
            ingredients.setInputLists(ItemStack.class, inputs);
            ingredients.setOutput(ItemStack.class, recipe.getOutput());
        }

        @Override
        public void drawInfo(Minecraft minecraft, int width, int height, int mouseX, int mouseY) {
            if (recipe.getMana() > 0) {
                HUDHandler.renderManaBar(17, height - 7, 0x0000FF, 0.75F,
                        recipe.getMana(), 1000000);
            }
        }
    }

    private static final class YggdrasilBranchWrapper implements IRecipeWrapper {
        private final ItemStack input = new ItemStack(ModItems.gjallarHornEmpty);
        private final ItemStack output = new ItemStack(ModItems.gjallarHornFull);

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInput(ItemStack.class, input);
            ingredients.setOutput(ItemStack.class, output);
        }

        @Override
        public void drawInfo(Minecraft minecraft, int width, int height,
                             int mouseX, int mouseY) {
            HUDHandler.renderManaBar(20, 50, 0x0000FF, 0.75F,
                    TileYggdrasilBranch.getManaRequired(), TilePool.MAX_MANA / 10);
        }
    }
}
