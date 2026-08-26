package mythicbotany.client;

import mythicbotany.block.BlockReturnPortal;
import mythicbotany.tile.TileReturnPortal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.MiscellaneousIcons;
import vazkii.botania.client.core.proxy.ClientProxy;

/** Renders both portal faces as a translucent slab, matching the active Alfheim portal. */
@SideOnly(Side.CLIENT)
public final class RenderReturnPortal extends TileEntitySpecialRenderer<TileReturnPortal> {
    private static final float TOP = (float) BlockReturnPortal.PORTAL_TOP;
    private static final float BOTTOM = (float) BlockReturnPortal.PORTAL_BOTTOM;
    private static final float ALPHA = 0.42F;

    @Override
    public void render(TileReturnPortal tile, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        TextureAtlasSprite sprite = MiscellaneousIcons.INSTANCE.alfPortalTex;
        if (sprite == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.color(1.0F, 1.0F, 1.0F, ALPHA);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        renderFace(sprite, TOP, false);
        renderFace(sprite, BOTTOM, true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderFace(TextureAtlasSprite sprite, float y, boolean reverse) {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(GL11.GL_QUADS, ClientProxy.POSITION_TEX_LMAP);
        if (reverse) {
            vertex(tessellator, 0.0D, y, 0.0D, sprite.getMinU(), sprite.getMinV());
            vertex(tessellator, 1.0D, y, 0.0D, sprite.getMaxU(), sprite.getMinV());
            vertex(tessellator, 1.0D, y, 1.0D, sprite.getMaxU(), sprite.getMaxV());
            vertex(tessellator, 0.0D, y, 1.0D, sprite.getMinU(), sprite.getMaxV());
        } else {
            vertex(tessellator, 0.0D, y, 1.0D, sprite.getMinU(), sprite.getMaxV());
            vertex(tessellator, 1.0D, y, 1.0D, sprite.getMaxU(), sprite.getMaxV());
            vertex(tessellator, 1.0D, y, 0.0D, sprite.getMaxU(), sprite.getMinV());
            vertex(tessellator, 0.0D, y, 0.0D, sprite.getMinU(), sprite.getMinV());
        }
        tessellator.draw();
    }

    private void vertex(Tessellator tessellator, double x, double y, double z,
                       float u, float v) {
        tessellator.getBuffer().pos(x, y, z).tex(u, v)
                .lightmap(240, 240).endVertex();
    }
}
