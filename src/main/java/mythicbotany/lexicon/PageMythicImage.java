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
import vazkii.botania.common.lexicon.page.PageText;
import vazkii.botania.api.lexicon.LexiconPage;

/** A Hydroangeas-style full-page image with a localized caption. */
public final class PageMythicImage extends LexiconPage {
    private final ResourceLocation resource;
    private final String title;
    private final String description;

    public PageMythicImage(String caption, String resource) {
        this(caption, "", resource);
    }

    public PageMythicImage(String title, String description, String resource) {
        super("");
        this.title = title == null ? "" : title;
        this.description = description == null ? "" : description;
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
        ((GuiScreen) gui).drawModalRectWithCustomSizedTexture(
                gui.getLeft(), gui.getTop(), 0, 0,
                gui.getWidth(), gui.getHeight(), 512, 512);
        GlStateManager.disableBlend();

        if (!title.isEmpty()) {
            PageText.renderText(gui.getLeft() + 16, gui.getTop() + gui.getHeight() - 62,
                    gui.getWidth() - 30, gui.getHeight(), title);
        }
        if (!description.isEmpty()) {
            PageText.renderText(gui.getLeft() + 16, gui.getTop() + gui.getHeight() - 40,
                    gui.getWidth() - 30, gui.getHeight(), description);
        }
    }
}
