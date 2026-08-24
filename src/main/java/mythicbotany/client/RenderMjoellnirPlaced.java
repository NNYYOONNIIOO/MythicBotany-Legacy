package mythicbotany.client;

import mythicbotany.entity.EntityMjoellnirPlaced;
import mythicbotany.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderMjoellnirPlaced extends Render<EntityMjoellnirPlaced> {
    public RenderMjoellnirPlaced(RenderManager manager) {
        super(manager);
    }

    @Override
    public void doRender(EntityMjoellnirPlaced entity, double x, double y, double z,
                         float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.translate((float) x, (float) y, (float) z);
        float yaw = entity.prevRotationYaw
                + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        float pitch = entity.prevRotationPitch
                + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        GlStateManager.rotate(yaw - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, -1.0F);
        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(
                ModBlocks.mjoellnir.getDefaultState(), 1.0F);
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMjoellnirPlaced entity) {
        return null;
    }
}
