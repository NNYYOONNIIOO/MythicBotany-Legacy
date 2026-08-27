package mythicbotany.lexicon;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import vazkii.botania.api.internal.IGuiLexiconEntry;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconRecipeMappings;
import vazkii.botania.common.lexicon.page.PageRecipe;
import vazkii.botania.common.lexicon.page.PageText;

/**
 * Compact ritual page.  The full rune layout belongs to JEI; the lexicon
 * keeps only the output and can hand it to JEI when Shift-clicked.
 */
public final class PageRitualOutput extends PageRecipe {
    private final ItemStack output;
    private final String textKey;
    private boolean mouseDown;

    public PageRitualOutput(String unlocalizedName, ItemStack output) {
        this(unlocalizedName, "", output);
    }

    public PageRitualOutput(String unlocalizedName, String textKey, ItemStack output) {
        super("");
        this.textKey = textKey;
        this.output = output == null ? ItemStack.EMPTY : output.copy();
    }

    @Override
    public List<ItemStack> getDisplayedRecipes() {
        return output.isEmpty() ? Collections.<ItemStack>emptyList()
                : Collections.singletonList(output.copy());
    }

    @Override
    public void onPageAdded(LexiconEntry entry, int index) {
        if (!output.isEmpty()) {
            LexiconRecipeMappings.map(output, entry, index, true);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderRecipe(IGuiLexiconEntry gui, int mx, int my) {
        if (!output.isEmpty()) {
            renderItemAtGridPos(gui, 3, 0, output, false);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderScreen(IGuiLexiconEntry gui, int mx, int my) {
        boolean pressed = Mouse.isButtonDown(0);
        super.renderScreen(gui, mx, my);
        if (!textKey.isEmpty()) {
            PageText.renderText(gui.getLeft() + 16, gui.getTop() + gui.getHeight() - 100,
                    gui.getWidth() - 30, gui.getHeight(), textKey);
        }
        int outputX = gui.getLeft() + 3 * 29 + 17;
        int outputY = gui.getTop() + 17;
        if (pressed && !mouseDown && GuiScreen.isShiftKeyDown()
                && mx >= outputX && mx < outputX + 16
                && my >= outputY && my < outputY + 16) {
            try {
                Class<?> plugin = Class.forName("mythicbotany.jei.MythicBotanyJeiPlugin");
                plugin.getMethod("showRitual", ItemStack.class).invoke(null, output.copy());
            } catch (ReflectiveOperationException | LinkageError ignored) {
                // JEI is optional.  The ordinary lexicon page remains usable.
            }
        }
        mouseDown = pressed;
    }
}
