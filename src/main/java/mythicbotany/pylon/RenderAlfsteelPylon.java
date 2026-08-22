package mythicbotany.pylon;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

/** Client renderer for the Alfsteel pylon's ring and crystal. */
public class RenderAlfsteelPylon extends TileEntitySpecialRenderer<TileAlfsteelPylon> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            "mythicbotany", "textures/model/pylon_alfsteel.png");
    private final PylonModel model = new PylonModel();

    @Override
    public void render(TileAlfsteelPylon tile, double x, double y, double z, float partialTicks,
                       int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        float time = tile.getWorld() == null ? partialTicks
                : tile.getWorld().getTotalWorldTime() + partialTicks;
        GlStateManager.rotate(time * 1.5F, 0.0F, 1.0F, 0.0F);
        bindTexture(TEXTURE);
        model.render(0.0625F);
        GlStateManager.popMatrix();
    }

    private static final class PylonModel extends ModelBase {
        private final ModelRenderer base = new ModelRenderer(this, 0, 0);
        private final ModelRenderer ring = new ModelRenderer(this, 0, 16);
        private final ModelRenderer crystal = new ModelRenderer(this, 32, 16);

        private PylonModel() {
            textureWidth = 64;
            textureHeight = 64;
            base.addBox(-6.0F, 7.0F, -6.0F, 12, 2, 12);
            ring.addBox(-5.0F, -1.0F, -5.0F, 10, 2, 10);
            crystal.addBox(-3.0F, -10.0F, -3.0F, 6, 10, 6);
            crystal.rotateAngleY = 0.7853982F;
        }

        private void render(float scale) {
            base.render(scale);
            ring.render(scale);
            crystal.render(scale);
        }
    }
}

