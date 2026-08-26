package mythicbotany.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.MiscellaneousIcons;

/** Renders the Alfheim portal swirl over the complete game view while travelling. */
@SideOnly(Side.CLIENT)
public final class AlfheimPortalOverlayHandler {
    private static final int MAX_PORTAL_TIME = 120;
    private static int portalTime;

    public static void setPortalTime(int time) {
        portalTime = Math.min(MAX_PORTAL_TIME, Math.max(0, time));
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && portalTime > 0) {
            portalTime--;
        }
    }

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || portalTime <= 0) {
            return;
        }

        TextureAtlasSprite sprite = MiscellaneousIcons.INSTANCE.alfPortalTex;
        Minecraft minecraft = Minecraft.getMinecraft();
        if (sprite == null || minecraft.player == null) {
            return;
        }

        int width = event.getResolution().getScaledWidth();
        int height = event.getResolution().getScaledHeight();
        float strength = Math.min(0.90F, 0.20F + portalTime / (float) MAX_PORTAL_TIME * 0.70F);

        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, strength);
        minecraft.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(0.0D, height, -90.0D).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
        buffer.pos(width, height, -90.0D).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
        buffer.pos(width, 0.0D, -90.0D).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
        buffer.pos(0.0D, 0.0D, -90.0D).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
        tessellator.draw();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }
}
