package mythicbotany.client;

import mythicbotany.entity.EntityMjoellnir;
import mythicbotany.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Renders the projectile directly with the registered block model. */
@SideOnly(Side.CLIENT)
public final class RenderMjoellnir extends Render<EntityMjoellnir> {
    private final BlockRendererDispatcher blockRenderer;

    public RenderMjoellnir(RenderManager manager) {
        super(manager);
        blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
        shadowSize = 0.0F;
    }

    @Override
    public void doRender(EntityMjoellnir entity, double x, double y, double z,
                         float entityYaw, float partialTicks) {
        float yaw = entity.prevRotationYaw
                + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        float pitch = entity.prevRotationPitch
                + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-pitch, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(-0.5D, -0.5D, -0.5D);
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            blockRenderer.renderBlockBrightness(ModBlocks.mjoellnir.getDefaultState(), 1.0F);
        } finally {
            GlStateManager.popMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMjoellnir entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
