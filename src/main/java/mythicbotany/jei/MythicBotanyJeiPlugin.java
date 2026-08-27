package mythicbotany.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mythicbotany.MythicBotany;
import mythicbotany.recipe.InfuserRecipe;
import mythicbotany.recipe.YggdrasilBranchRecipe;
import mythicbotany.registry.ModBlocks;
import mythicbotany.registry.ModItems;
import mythicbotany.rune.RuneRitualRecipe;
import mythicbotany.rune.RuneRitualRegistry;
import mythicbotany.tile.TileYggdrasilBranch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.block.tile.mana.TilePool;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/** JEI 4.x integration for the 1.12.2 mana infuser and rune holders. */
@JEIPlugin
public final class MythicBotanyJeiPlugin implements IModPlugin {
    private static final ResourceLocation RITUAL_BACKGROUND = new ResourceLocation(
            MythicBotany.MODID, "textures/gui/jei_ritual.png");
    // The lower controls stay at their original positions while only the
    // rune-panel drawable grows around them.
    private static final int RITUAL_LOWER_CONTENT_OFFSET_Y = 0;
    private IRecipeRegistry runtimeRecipeRegistry;
    private final List<YggdrasilBranchRecipe> pendingBranchRecipes = new ArrayList<>();
    private final Set<YggdrasilBranchRecipe> initialBranchRecipes =
            Collections.newSetFromMap(new IdentityHashMap<YggdrasilBranchRecipe, Boolean>());

    @Override
    public void register(IModRegistry registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        InfuserCategory infuserCategory = new InfuserCategory(guiHelper);
        RitualCategory ritualCategory = new RitualCategory(guiHelper);
        YggdrasilBranchCategory yggdrasilBranchCategory = new YggdrasilBranchCategory(guiHelper);
        registry.addRecipeCategories(infuserCategory, ritualCategory, yggdrasilBranchCategory);
        registry.addRecipes(Collections.singletonList(new AlfsteelAncientWillRecipeWrapper()),
                VanillaRecipeCategoryUid.CRAFTING);

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
        YggdrasilBranchRecipe.registerDefaults();
        initialBranchRecipes.clear();
        pendingBranchRecipes.clear();
        List<YggdrasilBranchWrapper> branchRecipes = new ArrayList<>();
        for (YggdrasilBranchRecipe recipe : YggdrasilBranchRecipe.getRecipes()) {
            initialBranchRecipes.add(recipe);
            branchRecipes.add(new YggdrasilBranchWrapper(recipe));
        }
        registry.addRecipes(branchRecipes, YggdrasilBranchCategory.UID);
        YggdrasilBranchRecipe.addListener(new YggdrasilBranchRecipe.RecipeListener() {
            @Override
            public void onRecipeAdded(YggdrasilBranchRecipe recipe) {
                addYggdrasilBranchRecipe(recipe);
            }
        });

        registry.addRecipeCatalyst(new ItemStack(ModBlocks.manaInfuser), InfuserCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.centralRuneHolder), RitualCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.runeHolder), RitualCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.yggdrasilBranch),
                YggdrasilBranchCategory.UID);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        runtimeRecipeRegistry = runtime.getRecipeRegistry();
        for (YggdrasilBranchRecipe recipe : new ArrayList<>(pendingBranchRecipes)) {
            runtimeRecipeRegistry.addRecipe(new YggdrasilBranchWrapper(recipe),
                    YggdrasilBranchCategory.UID);
        }
        pendingBranchRecipes.clear();
    }

    private void addYggdrasilBranchRecipe(YggdrasilBranchRecipe recipe) {
        if (recipe == null || initialBranchRecipes.contains(recipe)) {
            return;
        }
        if (runtimeRecipeRegistry != null) {
            runtimeRecipeRegistry.addRecipe(new YggdrasilBranchWrapper(recipe),
                    YggdrasilBranchCategory.UID);
        } else if (!pendingBranchRecipes.contains(recipe)) {
            pendingBranchRecipes.add(recipe);
        }
    }

    private static final class InfuserCategory implements IRecipeCategory<InfuserWrapper> {
        private static final String UID = MythicBotany.MODID + ":infuser";
        private static final int CENTER_X = 47;
        private static final int CENTER_Y = 44;
        private static final int INPUT_RADIUS = 31;
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
            stacks.init(0, true, CENTER_X, CENTER_Y);
            stacks.set(0, new ItemStack(ModBlocks.manaInfuser));

            List<ItemStack> inputs = wrapper.recipe.getInputs();
            for (int i = 0; i < inputs.size(); i++) {
                stacks.init(i + 1, true, inputSlotX(i, inputs.size()),
                        inputSlotY(i, inputs.size()));
                stacks.set(i + 1, inputs.get(i));
            }

            int outputSlot = inputs.size() + 1;
            stacks.init(outputSlot, false, 86, 11);
            stacks.set(outputSlot, wrapper.recipe.getOutput());
        }

        private static int inputSlotX(int index, int count) {
            double angle = -Math.PI / 2.0D + 2.0D * Math.PI * index / count;
            return CENTER_X + (int) Math.round(Math.cos(angle) * INPUT_RADIUS);
        }

        private static int inputSlotY(int index, int count) {
            double angle = -Math.PI / 2.0D + 2.0D * Math.PI * index / count;
            return CENTER_Y + (int) Math.round(Math.sin(angle) * INPUT_RADIUS);
        }
    }

    private static final class RitualCategory implements IRecipeCategory<RitualWrapper> {
        private static final String UID = MythicBotany.MODID + ":ritual";
        private static final int ITEM_SLOT_SIZE = 18;
        private final IDrawable background;
        private final IDrawable itemSlot;
        private final IDrawable icon;

        private RitualCategory(IGuiHelper helper) {
            background = new ExpandedRitualBackground();
            // Use JEI's unscaled 18x18 slot texture so the normal slot border is
            // preserved independently of the enlarged ritual background.
            itemSlot = helper.createDrawable(new ResourceLocation("jei",
                    "textures/gui/slot.png"), 0, 0, ITEM_SLOT_SIZE, ITEM_SLOT_SIZE);
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
                int x = 62 + 16 * rune.getOriginalX();
                int y = 62 - 16 * rune.getOriginalZ();
                stacks.init(slotIndex, true, x, y);
                stacks.set(slotIndex, rune.getRune());
                slotIndex++;
            }

            for (RuneRitualRecipe.InputRequirement input : wrapper.recipe.getInputs()) {
                int extraIndex = slotIndex - runeCount - 1;
                int extraCount = wrapper.recipe.getInputs().size()
                        + wrapper.recipe.getSpecialInputs().size();
                stacks.init(slotIndex, true, extraInputX(extraIndex, extraCount),
                        extraInputY(extraIndex, extraCount));
                stacks.setBackground(slotIndex, itemSlot);
                stacks.set(slotIndex, input.getDisplayStack());
                slotIndex++;
            }

            int entityIndex = 0;
            for (String entityId : wrapper.recipe.getSpecialInputs()) {
                int extraIndex = wrapper.recipe.getInputs().size() + entityIndex;
                int extraCount = wrapper.recipe.getInputs().size()
                        + wrapper.recipe.getSpecialInputs().size();
                stacks.init(slotIndex, true, extraInputX(extraIndex, extraCount),
                        extraInputY(extraIndex, extraCount));
                stacks.setBackground(slotIndex, itemSlot);
                stacks.set(slotIndex, entityDisplayStack(entityId));
                slotIndex++;
                entityIndex++;
            }

            stacks.init(slotIndex, false, 60, 170 + RITUAL_LOWER_CONTENT_OFFSET_Y);
            stacks.setBackground(slotIndex, itemSlot);
            stacks.set(slotIndex, wrapper.recipe.getOutput());

            stacks.addTooltipCallback(new ITooltipCallback<ItemStack>() {
                @Override
                public void onTooltip(int hoveredSlot, boolean input, ItemStack ingredient, List<String> tooltip) {
                    if (hoveredSlot > 0 && hoveredSlot <= runeCount) {
                        RuneRitualRecipe.RunePosition rune = wrapper.recipe.getRunes().get(hoveredSlot - 1);
                        tooltip.add(TextFormatting.GOLD + I18n.format(
                                "tooltip.mythicbotany.rune_offset",
                                rune.getOriginalX(), rune.getOriginalZ()));
                    } else {
                        int entityStart = 1 + runeCount + wrapper.recipe.getInputs().size();
                        int entitySlot = hoveredSlot - entityStart;
                        if (entitySlot >= 0 && entitySlot < wrapper.recipe.getSpecialInputs().size()) {
                            ResourceLocation entityId = new ResourceLocation(
                                    stripEntityMarker(wrapper.recipe.getSpecialInputs().get(entitySlot)));
                            String entityName = entityDisplayName(entityId);
                            tooltip.add(entityName);
                            tooltip.add(I18n.format("tooltip.mythicbotany.sacrifice_entity"));
                        }
                    }
                }
            });
        }

        private static int extraInputX(int index, int count) {
            int columns = Math.min(7, Math.max(1, count));
            int row = index / columns;
            int column = index % columns;
            int itemsInRow = Math.min(columns, count - row * columns);
            int startX = 62 - ITEM_SLOT_SIZE / 2 * (itemsInRow - 1);
            return startX + column * ITEM_SLOT_SIZE;
        }

        private static int extraInputY(int index, int count) {
            int columns = Math.min(7, Math.max(1, count));
            int rows = (count + columns - 1) / columns;
            int row = index / columns;
            return 140 + RITUAL_LOWER_CONTENT_OFFSET_Y
                    - ITEM_SLOT_SIZE * (rows - 1) + row * ITEM_SLOT_SIZE;
        }

        /**
         * Enlarges the current rune panel by another 10 px on the left,
         * 18 px on the right, and 13 px at the bottom. The lower controls
         * remain at their existing coordinates.
         */
        private static final class ExpandedRitualBackground implements IDrawable {
            // Relative to the current 211x265 drawable: +10 px on the left,
            // +18 px on the right, and +13 px at the bottom.
            private static final int WIDTH = 239;
            private static final int HEIGHT = 278;
            private static final int LEFT = 40;
            private static final int TOP = 18;
            private static final int SOURCE_WIDTH = 136;
            private static final int SOURCE_HEIGHT = 196;

            @Override
            public int getWidth() {
                // Include the left overhang in JEI's category bounds. The
                // drawable is intentionally painted from -LEFT so the rune
                // panel grows around the existing rune coordinates.
                return WIDTH + LEFT;
            }

            @Override
            public int getHeight() {
                // Include the top overhang as well, preventing the enlarged
                // frame from being clipped by JEI's recipe-area boundary.
                return HEIGHT + TOP;
            }

            @Override
            public void draw(Minecraft minecraft, int xOffset, int yOffset) {
                minecraft.renderEngine.bindTexture(RITUAL_BACKGROUND);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                // Keep the enlarged drawable's full bounds known to JEI so
                // its frame is not clipped by the recipe panel boundary.
                Gui.drawScaledCustomSizeModalRect(xOffset - LEFT, yOffset - TOP,
                        0.0F, 0.0F, SOURCE_WIDTH, SOURCE_HEIGHT,
                        WIDTH, HEIGHT, 256.0F, 256.0F);
                GlStateManager.disableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        private static ItemStack entityDisplayStack(String entityId) {
            String registryName = stripEntityMarker(entityId);
            ItemStack stack = new ItemStack(Items.SPAWN_EGG, 1,
                    spawnEggMetadata(registryName));
            NBTTagCompound entityTag = new NBTTagCompound();
            entityTag.setString("id", registryName);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag("EntityTag", entityTag);
            stack.setTagCompound(tag);
            return stack;
        }

        private static String stripEntityMarker(String entityId) {
            if (entityId == null) {
                return "";
            }
            String value = entityId.trim();
            return value.regionMatches(true, 0, "entity:", 0, "entity:".length())
                    ? value.substring("entity:".length()) : value;
        }

        private static int spawnEggMetadata(String entityId) {
            try {
                Method getId = EntityList.class.getMethod("getIDFromString", String.class);
                String path = entityId;
                int separator = path.lastIndexOf(':');
                if (separator >= 0) {
                    path = path.substring(separator + 1);
                }
                String capitalized = path.isEmpty() ? path
                        : Character.toUpperCase(path.charAt(0)) + path.substring(1);
                String[] candidates = {entityId, path, capitalized, "Entity" + capitalized};
                for (String candidate : candidates) {
                    Object id = getId.invoke(null, candidate);
                    if (id instanceof Number && ((Number) id).intValue() >= 0) {
                        return ((Number) id).intValue();
                    }
                }
            } catch (Exception ignored) {
                // Custom entities may not have a vanilla spawn-egg metadata value.
            }
            return 0;
        }

        private static String entityDisplayName(ResourceLocation entityId) {
            String path = entityId.getPath();
            String capitalizedPath = path.isEmpty() ? path
                    : Character.toUpperCase(path.charAt(0)) + path.substring(1);
            String[] keys = {
                    "entity." + path + ".name",
                    "entity." + capitalizedPath + ".name",
                    "entity." + entityId.getNamespace() + "." + path + ".name",
                    "entity." + entityId.getNamespace() + "." + capitalizedPath + ".name"
            };
            for (String key : keys) {
                String translated = I18n.format(key);
                if (!translated.equals(key)) {
                    return translated;
                }
            }
            return entityId.toString();
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
            stacks.set(0, wrapper.recipe.getInput());
            stacks.init(1, true, 62, 12);
            stacks.set(1, new ItemStack(ModBlocks.yggdrasilBranch));
            stacks.init(2, false, 93, 12);
            stacks.set(2, wrapper.recipe.getOutput());
        }
    }

    private static final class InfuserWrapper implements IRecipeWrapper {
        private final InfuserRecipe recipe;

        private InfuserWrapper(InfuserRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            List<List<ItemStack>> inputs = new ArrayList<>();
            for (ItemStack input : recipe.getInputs()) {
                inputs.add(Collections.singletonList(input));
            }
            ingredients.setInputLists(ItemStack.class, inputs);
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
            for (String entityId : recipe.getSpecialInputs()) {
                inputs.add(Collections.singletonList(RitualCategory.entityDisplayStack(entityId)));
            }
            ingredients.setInputLists(ItemStack.class, inputs);
            ingredients.setOutput(ItemStack.class, recipe.getOutput());
        }

        @Override
        public void drawInfo(Minecraft minecraft, int width, int height, int mouseX, int mouseY) {
            if (recipe.getMana() > 0) {
                HUDHandler.renderManaBar(17, 189 + RITUAL_LOWER_CONTENT_OFFSET_Y,
                        0x0000FF, 0.75F,
                        recipe.getMana(), 1000000);
            }
        }
    }

    private static final class YggdrasilBranchWrapper implements IRecipeWrapper {
        private final YggdrasilBranchRecipe recipe;

        private YggdrasilBranchWrapper(YggdrasilBranchRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInput(ItemStack.class, recipe.getInput());
            ingredients.setOutput(ItemStack.class, recipe.getOutput());
        }

        @Override
        public void drawInfo(Minecraft minecraft, int width, int height,
                             int mouseX, int mouseY) {
            HUDHandler.renderManaBar(20, 50, 0x0000FF, 0.75F,
                    recipe.getMana(), TilePool.MAX_MANA / 10);
        }
    }
}
