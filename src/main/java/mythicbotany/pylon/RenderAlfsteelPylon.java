package mythicbotany.pylon;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import mythicbotany.registry.ModBlocks;

/** Client renderer for the Alfsteel pylon's ring and crystal. */
public class RenderAlfsteelPylon extends TileEntitySpecialRenderer<TileAlfsteelPylon> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            "mythicbotany", "textures/model/pylon_alfsteel.png");
    private final PylonModelNatura model = new PylonModelNatura();

    /** Forwards only this block's builtin/entity item to its bound tile renderer. */
    public static final class ForwardingTEISR extends TileEntityItemStackRenderer {
        private static final TileAlfsteelPylon DUMMY = new TileAlfsteelPylon();
        private final TileEntityItemStackRenderer compose;

        public ForwardingTEISR(TileEntityItemStackRenderer compose) {
            this.compose = compose;
        }

        @Override
        public void renderByItem(ItemStack stack, float partialTicks) {
            if (stack.getItem() == Item.getItemFromBlock(ModBlocks.alfsteelPylon)) {
                TileEntityRendererDispatcher.instance.render(DUMMY, 0.0D, 0.0D, 0.0D, partialTicks);
            } else {
                compose.renderByItem(stack, partialTicks);
            }
        }
    }

    @Override
    public void render(TileAlfsteelPylon tile, double x, double y, double z, float partialTicks,
                       int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }
        boolean renderingItem = tile == ForwardingTEISR.DUMMY;
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        double time = renderingItem || tile.getWorld() == null
                ? partialTicks : tile.getWorld().getTotalWorldTime() + partialTicks;
        if (!renderingItem) {
            time += new java.util.Random(tile.getPos().hashCode()).nextInt(360);
        }
        bindTexture(TEXTURE);

        GlStateManager.translate(x, y + (renderingItem ? 1.35D : 1.5D), z);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5F, 0.0F, -0.5F);
        if (!renderingItem) {
            GlStateManager.rotate((float) time * 1.5F, 0.0F, 1.0F, 0.0F);
        }
        model.renderRing();
        if (!renderingItem) {
            GlStateManager.translate(0.0D, Math.sin(time / 20.0D) / 20.0D - 0.025D, 0.0D);
        }
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        if (!renderingItem) {
            GlStateManager.translate(0.0D, Math.sin(time / 20.0D) / 17.5D, 0.0D);
        }
        GlStateManager.translate(0.5F, 0.0F, -0.5F);
        if (!renderingItem) {
            GlStateManager.rotate((float) -time, 0.0F, 1.0F, 0.0F);
        }
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        model.renderCrystal();
        GlStateManager.enableAlpha();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();

        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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
