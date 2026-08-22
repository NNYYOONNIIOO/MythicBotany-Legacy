package mythicbotany.pylon;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/** Client renderer for the Alfsteel pylon's ring and crystal. */
public class RenderAlfsteelPylon extends TileEntitySpecialRenderer<TileAlfsteelPylon> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            "mythicbotany", "textures/model/pylon_alfsteel.png");
    private final PylonModelNatura model = new PylonModelNatura();

    /** Uses the same TESR for the held, inventory and dropped block item. */
    public static final class ItemRenderer extends TileEntityItemStackRenderer {
        private final RenderAlfsteelPylon renderer = new RenderAlfsteelPylon();
        private final TileAlfsteelPylon dummy = new TileAlfsteelPylon();

        @Override
        public void renderByItem(ItemStack stack, float partialTicks) {
            renderer.render(dummy, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        }
    }

    @Override
    public void render(TileAlfsteelPylon tile, double x, double y, double z, float partialTicks,
                       int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        double time = tile.getWorld() == null ? partialTicks
                : tile.getWorld().getTotalWorldTime() + partialTicks;
        time += new java.util.Random(tile.getPos().hashCode()).nextInt(360);
        bindTexture(TEXTURE);

        GlStateManager.translate(x, y + 1.5D, z);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5F, 0.0F, -0.5F);
        GlStateManager.rotate((float) time * 1.5F, 0.0F, 1.0F, 0.0F);
        model.renderRing();
        GlStateManager.translate(0.0D, Math.sin(time / 20.0D) / 20.0D - 0.025D, 0.0D);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, Math.sin(time / 20.0D) / 17.5D, 0.0D);
        GlStateManager.translate(0.5F, 0.0F, -0.5F);
        GlStateManager.rotate((float) -time, 0.0F, 1.0F, 0.0F);
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        model.renderCrystal();
        GlStateManager.enableAlpha();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();

        GlStateManager.disableBlend();
        GlStateManager.enableRescaleNormal();
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
