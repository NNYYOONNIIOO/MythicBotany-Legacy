package mythicbotany.lexicon;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import vazkii.botania.api.internal.IGuiLexiconEntry;
import vazkii.botania.api.lexicon.LexiconPage;
import vazkii.botania.api.lexicon.multiblock.Multiblock;
import vazkii.botania.api.lexicon.multiblock.MultiblockSet;
import vazkii.botania.client.core.handler.MultiblockRenderHandler;
import vazkii.botania.client.lib.LibResources;

/** Botania's multiblock page with the actual supporting-block icon. */
public final class PageMythicMultiblock extends LexiconPage {
    private static final ResourceLocation OVERLAY = new ResourceLocation(
            LibResources.GUI_MULTIBLOCK_OVERLAY);
    private final MultiblockSet set;
    private final Multiblock multiblock;
    private final ItemStack materialIcon;
    private GuiButton button;

    public PageMythicMultiblock(String unlocalizedName, MultiblockSet set,
                                ItemStack materialIcon) {
        super(unlocalizedName);
        this.set = set;
        this.multiblock = set.getForFacing(EnumFacing.SOUTH);
        this.materialIcon = materialIcon == null ? ItemStack.EMPTY : materialIcon.copy();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderScreen(IGuiLexiconEntry gui, int mx, int my) {
        TextureManager render = Minecraft.getMinecraft().renderEngine;
        render.bindTexture(OVERLAY);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableAlpha();
        GlStateManager.color(1F, 1F, 1F, 1F);
        ((GuiScreen) gui).drawTexturedModalRect(gui.getLeft(), gui.getTop(),
                0, 0, gui.getWidth(), gui.getHeight());
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();

        GlStateManager.pushMatrix();
        GlStateManager.translate(gui.getLeft() + gui.getWidth() / 2F,
                gui.getTop() + 90F, gui.getZLevel() + 100F);
        float diagonal = (float) Math.sqrt(multiblock.getXSize() * multiblock.getXSize()
                + multiblock.getZSize() * multiblock.getZSize());
        float scale = -Math.min(60F / multiblock.getYSize(), 90F / diagonal);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.rotate(-20F, 1F, 0F, 0F);
        GlStateManager.rotate(gui.getElapsedTicks(), 0F, 1F, 0F);
        MultiblockRenderHandler.renderMultiblockOnPage(multiblock);
        GlStateManager.popMatrix();

        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        boolean unicode = font.getUnicodeFlag();
        String title = TextFormatting.BOLD + I18n.format(getUnlocalizedName());
        font.setUnicodeFlag(true);
        font.drawString(title, gui.getLeft() + gui.getWidth() / 2
                - font.getStringWidth(title) / 2, gui.getTop() + 16, 0x000000);
        font.setUnicodeFlag(unicode);

        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();
        int iconX = gui.getLeft() + 15;
        int iconY = gui.getTop() + 25;
        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(materialIcon, iconX, iconY);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();

        if (mx >= iconX && mx < iconX + 16 && my >= iconY && my < iconY + 16) {
            List<String> materials = new ArrayList<>();
            materials.add(I18n.format("botaniamisc.materialsRequired"));
            for (ItemStack stack : multiblock.materials) {
                materials.add(" " + TextFormatting.AQUA + stack.getCount() + " "
                        + TextFormatting.GRAY + stack.getDisplayName());
            }
            vazkii.botania.client.core.helper.RenderHelper.renderTooltip(mx, my, materials);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onOpened(IGuiLexiconEntry gui) {
        button = new GuiButton(101, gui.getLeft() + 30, gui.getTop() + gui.getHeight() - 50,
                gui.getWidth() - 60, 20, buttonText());
        gui.getButtonList().add(button);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClosed(IGuiLexiconEntry gui) {
        if (button != null) gui.getButtonList().remove(button);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onActionPerformed(IGuiLexiconEntry gui, GuiButton pressed) {
        if (pressed != button) return;
        MultiblockRenderHandler.setMultiblock(
                MultiblockRenderHandler.currentMultiblock == set ? null : set);
        button.displayString = buttonText();
    }

    @SideOnly(Side.CLIENT)
    private String buttonText() {
        return I18n.format(MultiblockRenderHandler.currentMultiblock == set
                ? "botaniamisc.unvisualize" : "botaniamisc.visualize");
    }
}
