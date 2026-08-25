package mythicbotany.client;

import mythicbotany.block.BlockYggdrasilBranch;
import mythicbotany.registry.ModBlocks;
import mythicbotany.tile.TileYggdrasilBranch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import vazkii.botania.common.item.ModItems;

/** Renders a horn stored in a placed Yggdrasil branch. */
public class RenderYggdrasilBranch extends TileEntitySpecialRenderer<TileYggdrasilBranch> {
    // These constants are intentionally exposed for small visual adjustments.
    public static final double MANA_RESOURCE_X = 0.5D;
    public static final double MANA_RESOURCE_Y = 12.5D / 16.0D;
    public static final double MANA_RESOURCE_Z = 0.5D;
    public static final double FRONT_OFFSET = 3.0D / 16.0D;
    public static final float MANA_RESOURCE_SCALE = 0.45F;
    public static final float HORN_SCALE = 0.45F;
    public static final double HORN_X = 0.5D;
    public static final double HORN_Y = 0.5D / 16.0D + HORN_SCALE / 2.0D;
    public static final double HORN_Z = 0.5D;

    @Override
    public void render(TileYggdrasilBranch tile, double x, double y, double z, float partialTicks,
                       int destroyStage, float alpha) {
        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        EnumFacing facing = state.getValue(BlockYggdrasilBranch.FACING);
        renderManaResource(x, y, z, facing);
        renderStack(tile.getHorn(), x + HORN_X + facing.getXOffset() * FRONT_OFFSET,
                y + HORN_Y, z + HORN_Z + facing.getZOffset() * FRONT_OFFSET,
                facing, HORN_SCALE);
    }

    private static void renderManaResource(double x, double y, double z, EnumFacing facing) {
        renderStack(new ItemStack(ModItems.manaResource, 1, 3),
                x + MANA_RESOURCE_X + facing.getXOffset() * FRONT_OFFSET,
                y + MANA_RESOURCE_Y,
                z + MANA_RESOURCE_Z + facing.getZOffset() * FRONT_OFFSET,
                facing, MANA_RESOURCE_SCALE);
    }

    private static void renderStack(ItemStack stack, double x, double y, double z,
                                     EnumFacing facing, float scale) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(facing.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(scale, scale, scale);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    /** Renders the complete branch item, including its permanent mana resource. */
    public static void renderItem(ItemStack stack, float partialTicks) {
        EnumFacing facing = EnumFacing.byHorizontalIndex(stack.getMetadata() & 3);
        IBlockState state = ModBlocks.yggdrasilBranch.getDefaultState()
                .withProperty(BlockYggdrasilBranch.FACING, facing);

        GlStateManager.pushMatrix();
        try {
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.translate(0.5D, 0.5D, 0.5D);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(-0.5D, -0.5D, -0.5D);
            Minecraft.getMinecraft().getBlockRendererDispatcher()
                    .renderBlockBrightness(state, 1.0F);
            renderManaResource(0.0D, 0.0D, 0.0D, facing);
        } finally {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }
}
