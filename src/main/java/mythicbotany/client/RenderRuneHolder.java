package mythicbotany.client;

import mythicbotany.rune.TileRuneHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import mythicbotany.registry.ModBlocks;

/** Renders the rune inserted into a Rune Holder in 1.12.2. */
public class RenderRuneHolder extends TileEntitySpecialRenderer<TileRuneHolder> {
    public static final class ItemRenderer extends TileEntityItemStackRenderer {
        private final RenderRuneHolder renderer = new RenderRuneHolder();
        private final TileRuneHolder dummy = new TileRuneHolder();

        @Override
        public void renderByItem(ItemStack stack, float partialTicks) {
            renderer.render(dummy, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        }
    }

    @Override
    public void render(TileRuneHolder tile, double x, double y, double z, float partialTicks,
                       int destroyStage, float alpha) {
        renderBase(ModBlocks.runeHolder.getDefaultState(), x, y, z);
        renderStack(tile.getRune(), x, y, z);
    }

    protected static void renderBase(net.minecraft.block.state.IBlockState state,
                                     double x, double y, double z) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(state, 1.0F);
        GlStateManager.popMatrix();
    }

    protected static void renderStack(ItemStack stack, double x, double y, double z) {
        if (stack == null || stack.isEmpty()) return;
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translate(x + 0.5D, y + 0.24D, z + 0.5D);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.7F, 0.7F, 0.7F);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }
}
