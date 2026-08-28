package mythicbotany.lexicon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import vazkii.botania.api.internal.IGuiLexiconEntry;
import vazkii.botania.api.lexicon.LexiconPage;
import vazkii.botania.common.lexicon.page.PageText;

/** Image page matching Botania's PageImage with a higher caption position. */
public final class PageMythicImage extends LexiconPage {
    // PageText advances ordinary lines by ten pixels, so three lines means
    // moving the caption up by exactly thirty pixels.
    private static final int CAPTION_LINE_HEIGHT = 10;
    private static final int CAPTION_LINES_UP = 3;
    private final ResourceLocation resource;

    public PageMythicImage(String unlocalizedName, String resource) {
        super(unlocalizedName);
        this.resource = new ResourceLocation(resource);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderScreen(IGuiLexiconEntry gui, int mx, int my) {
        TextureManager render = Minecraft.getMinecraft().renderEngine;
        render.bindTexture(resource);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1F, 1F, 1F, 1F);
        ((GuiScreen) gui).drawTexturedModalRect(gui.getLeft(), gui.getTop(),
                0, 0, gui.getWidth(), gui.getHeight());
        GlStateManager.disableBlend();

        int captionY = gui.getTop() + gui.getHeight() - 40
                - CAPTION_LINE_HEIGHT * CAPTION_LINES_UP;
        PageText.renderText(gui.getLeft() + 16, captionY,
                gui.getWidth() - 30, gui.getHeight(), getUnlocalizedName());
    }
}
