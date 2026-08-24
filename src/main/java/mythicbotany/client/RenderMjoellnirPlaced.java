package mythicbotany.client;

import mythicbotany.entity.EntityMjoellnirPlaced;
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

/** Renders the fallback entity directly with the registered block model. */
@SideOnly(Side.CLIENT)
public final class RenderMjoellnirPlaced extends Render<EntityMjoellnirPlaced> {
    private final BlockRendererDispatcher blockRenderer;

    public RenderMjoellnirPlaced(RenderManager manager) {
        super(manager);
        blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
        shadowSize = 0.0F;
    }

    @Override
    public void doRender(EntityMjoellnirPlaced entity, double x, double y, double z,
                         float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x, y, z);
            GlStateManager.translate(-0.5D, -0.5D, -0.5D);
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            blockRenderer.renderBlockBrightness(ModBlocks.mjoellnir.getDefaultState(), 1.0F);
        } finally {
            GlStateManager.popMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMjoellnirPlaced entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
