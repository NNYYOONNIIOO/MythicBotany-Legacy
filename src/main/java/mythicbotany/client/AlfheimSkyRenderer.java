package mythicbotany.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Uses the Botania skybox resource for the Alfheim dimension. */
@SideOnly(Side.CLIENT)
public final class AlfheimSkyRenderer extends IRenderHandler {
    private static final ResourceLocation SKYBOX = new ResourceLocation(
            "mythicbotany", "textures/environment/alfheim_sky.png");

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft minecraft) {
        GlStateManager.pushMatrix();
        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(SKYBOX);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float radius = 160.0F;
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        drawFace(buffer, -radius, -radius, -radius, radius, radius, -radius);
        drawFace(buffer, radius, -radius, radius, radius, radius, -radius);
        drawFace(buffer, -radius, -radius, radius, -radius, radius, -radius);
        drawFace(buffer, -radius, -radius, -radius, radius, -radius, radius);
        drawFace(buffer, -radius, radius, -radius, radius, radius, radius);
        drawFace(buffer, -radius, -radius, radius, radius, -radius, -radius);
        tessellator.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
    }

    private static void drawFace(BufferBuilder buffer, float x1, float y1, float z1,
                                 float x2, float y2, float z2) {
        float x3 = x2;
        float y3 = y1;
        float z3 = z1;
        float x4 = x1;
        float y4 = y2;
        float z4 = z2;
        buffer.pos(x1, y1, z1).tex(0.0D, 1.0D).endVertex();
        buffer.pos(x3, y3, z3).tex(1.0D, 1.0D).endVertex();
        buffer.pos(x2, y2, z2).tex(1.0D, 0.0D).endVertex();
        buffer.pos(x4, y4, z4).tex(0.0D, 0.0D).endVertex();
    }
}
