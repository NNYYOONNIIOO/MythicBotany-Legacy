package mythicbotany.client;

import mythicbotany.rune.TileRuneHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;

/** Renders the rune inserted into a Rune Holder in 1.12.2. */
public class RenderRuneHolder extends TileEntitySpecialRenderer<TileRuneHolder> {
    @Override
    public void render(TileRuneHolder tile, double x, double y, double z, float partialTicks,
                       int destroyStage, float alpha) {
        renderStack(tile.getRune(), x, y, z);
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
