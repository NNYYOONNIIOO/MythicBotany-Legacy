package mythicbotany.lexicon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import mythicbotany.registry.ModBlocks;
import vazkii.botania.api.internal.IGuiLexiconEntry;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconRecipeMappings;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.lexicon.page.PageRecipe;

/** Rune-altar-style recipe page with the MythicBotany mana infuser in the centre. */
public final class PageMythicInfuserRecipe extends PageRecipe {
    private static final ResourceLocation OVERLAY = new ResourceLocation(
            "botania", "textures/gui/petalOverlay.png");
    private final List<ItemStack> inputs;
    private final ItemStack output;
    private final int mana;

    public PageMythicInfuserRecipe(String unlocalizedName, ItemStack output,
                                   int mana, List<ItemStack> inputs) {
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
        for (ItemStack input : inputs) LexiconRecipeMappings.map(input, entry, index, true);
        LexiconRecipeMappings.map(new ItemStack(ModBlocks.manaInfuser), entry, index, true);
        if (!output.isEmpty()) LexiconRecipeMappings.map(output, entry, index, true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderRecipe(IGuiLexiconEntry gui, int mx, int my) {
        renderItemAtGridPos(gui, 3, 0, output, false);
        renderItemAtGridPos(gui, 2, 1, new ItemStack(ModBlocks.manaInfuser), false);

        for (int i = 0; i < inputs.size(); i++) {
            renderItemAtAngle(gui, inputs.size() == 0 ? 0F
                    : i * 360F / inputs.size(), inputs.get(i));
        }

        TextureManager render = Minecraft.getMinecraft().renderEngine;
        render.bindTexture(OVERLAY);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1F, 1F, 1F, 1F);
        ((GuiScreen) gui).drawTexturedModalRect(gui.getLeft(), gui.getTop(),
                0, 0, gui.getWidth(), gui.getHeight());
        GlStateManager.disableBlend();

        if (mana > 0) {
            HUDHandler.renderManaBar(gui.getLeft() + gui.getWidth() / 2 - 50,
                    gui.getTop() + 120, 0x0000FF, 0.75F, mana, TilePool.MAX_MANA / 10);
        }
    }
}
