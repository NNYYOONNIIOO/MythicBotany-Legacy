package mythicbotany.lexicon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.internal.IGuiLexiconEntry;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconRecipeMappings;
import vazkii.botania.common.lexicon.page.PageRecipe;

/**
 * Small legacy-lexicon recipe page for recipes that are not Forge IRecipes.
 * The recipe data is deliberately stack based, so it can describe the
 * Yggdrasil branch and can also be reused by runtime recipe integrations.
 */
public final class PageMythicRecipe extends PageRecipe {
    private final List<ItemStack> inputs;
    private final ItemStack output;
    private final int mana;

    public PageMythicRecipe(String unlocalizedName, ItemStack output, int mana, ItemStack... inputs) {
        super(unlocalizedName);
        this.inputs = new ArrayList<>();
        if (inputs != null) {
            for (ItemStack input : inputs) {
                if (input != null && !input.isEmpty()) this.inputs.add(input.copy());
            }
        }
        this.output = output == null ? ItemStack.EMPTY : output.copy();
        this.mana = Math.max(0, mana);
    }

    @Override
    public List<ItemStack> getDisplayedRecipes() {
        return output.isEmpty() ? Collections.<ItemStack>emptyList()
                : Collections.singletonList(output.copy());
    }

    @Override
    public void onPageAdded(LexiconEntry entry, int index) {
        if (!output.isEmpty()) LexiconRecipeMappings.map(output, entry, index, true);
        for (ItemStack input : inputs) LexiconRecipeMappings.map(input, entry, index, true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderRecipe(IGuiLexiconEntry gui, int mx, int my) {
        // Keep this page useful even when a custom recipe has several inputs.
        // PageRecipe's item helper uses the current GUI origin, so each stack
        // is rendered in a small row around the centre of the recipe area.
        int inputCount = inputs.size();
        for (int i = 0; i < inputCount; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((i - (inputCount - 1) / 2.0D) * 24.0D - 18.0D, 0.0D, 0.0D);
            renderItemAtAngle(gui, 0.0F, inputs.get(i));
            GlStateManager.popMatrix();
        }
        if (!output.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(36.0D, 0.0D, 0.0D);
            renderItemAtAngle(gui, 0.0F, output);
            GlStateManager.popMatrix();
        }
        // Keep the value part of the page's data even though the stock 1.12
        // lexicon has no mana label primitive; it is used by integrations and
        // prevents this page from being mistaken for a normal crafting page.
        if (mana < 0) throw new AssertionError("unreachable");
    }
}
