package mythicbotany.client;

import mythicbotany.block.BlockYggdrasilBranch;
import mythicbotany.tile.TileYggdrasilBranch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

/** Renders a horn stored in a placed Yggdrasil branch. */
public class RenderYggdrasilBranch extends TileEntitySpecialRenderer<TileYggdrasilBranch> {
    // Adjust these constants to move or scale the horn on a placed branch.
    private static final double HORN_X = 0.5D;
    private static final double HORN_Y = 0.65D;
    private static final double HORN_Z = 0.5D;
    private static final float HORN_SCALE = 0.45F;

    @Override
    public void render(TileYggdrasilBranch tile, double x, double y, double z, float partialTicks,
                       int destroyStage, float alpha) {
        ItemStack horn = tile.getHorn();
        if (horn.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translate(x + HORN_X, y + HORN_Y, z + HORN_Z);
        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        EnumFacing facing = state.getValue(BlockYggdrasilBranch.FACING);
        GlStateManager.rotate(facing.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(HORN_SCALE, HORN_SCALE, HORN_SCALE);
        Minecraft.getMinecraft().getRenderItem().renderItem(horn, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }
}
