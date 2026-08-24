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

@SideOnly(Side.CLIENT)
public final class RenderMjoellnir extends Render<EntityMjoellnir> {
    public RenderMjoellnir(RenderManager manager) {
        super(manager);
        shadowSize = 0.0F;
    }

    @Override
    public void doRender(EntityMjoellnir entity, double x, double y, double z,
                         float entityYaw, float partialTicks) {
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
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
            dispatcher.renderBlockBrightness(ModBlocks.mjoellnir.getDefaultState(), 1.0F);
        } finally {
            GlStateManager.popMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMjoellnir entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
