package mythicbotany.client;

import mythicbotany.rune.TileRuneHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/** Renders the rune inserted into a Rune Holder in 1.12.2. */
public class RenderRuneHolder extends TileEntitySpecialRenderer<TileRuneHolder> {
    @Override
    public void render(TileRuneHolder tile, double x, double y, double z, float partialTicks,
                       int destroyStage, float alpha) {
        renderStack(tile.getRune(), tile, x, y, z, partialTicks);
    }

    protected static void renderStack(ItemStack stack, TileEntity tile,
                                      double x, double y, double z, float partialTicks) {
        if (stack == null || stack.isEmpty()) return;
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translate(x + 0.5D, y + 0.24D, z + 0.5D);
        if (tile != null) {
            BlockPos pos = tile.getPos();
            long worldTime = tile.getWorld() == null ? 0L : tile.getWorld().getTotalWorldTime();
            int zCoord = pos.getZ();
            int positionPhase = zCoord == 0 ? 87
                    : 7 * pos.getX() + 31 * zCoord + Math.floorMod(pos.getX(), zCoord);
            double wobble = (Item.getIdFromItem(stack.getItem()) % 2 == 0 ? 0.04D : -0.04D)
                    * (worldTime + partialTicks + positionPhase);
            GlStateManager.rotate(31.0F * pos.getX() + 7.0F * pos.getZ(), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((float) (10.0D * Math.sin(wobble)), 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate((float) (10.0D * Math.cos(wobble)), 0.0F, 0.0F, 1.0F);
        }
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.7F, 0.7F, 0.7F);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }
}
