package mythicbotany.client;

import mythicbotany.block.BlockYggdrasilBranch;
import mythicbotany.registry.ModBlocks;
import mythicbotany.tile.TileYggdrasilBranch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import vazkii.botania.common.item.ModItems;

/** Renders a horn stored in a placed Yggdrasil branch. */
public class RenderYggdrasilBranch extends TileEntitySpecialRenderer<TileYggdrasilBranch> {
    // 坐标说明：完成方块朝向旋转后，X 为左右，Y 为上下，Z 为正面/背面。
    // 物品在枝干顶面时，正面视角的旋转使用 Y 轴；位置仍由 X/Y/Z 控制。
    public static final double MANA_RESOURCE_X = 0.5D;
    public static final double MANA_RESOURCE_Y = 12.5D / 16.0D;
    public static final double MANA_RESOURCE_Z = 0.5D;
    public static final double FRONT_OFFSET = 3.0D / 16.0D;
    public static final float MANA_RESOURCE_SCALE = 0.45F;
    public static final float MANA_RESOURCE_ROTATION_X = 0.0F;
    public static final float MANA_RESOURCE_ROTATION_Y = 90.0F;
    public static final float MANA_RESOURCE_ROTATION_Z = 0.0F;
    public static final float HORN_SCALE = 0.45F;
    public static final float HORN_ROTATION_X = 0.0F;
    public static final float HORN_ROTATION_Y = 180.0F;
    public static final float HORN_ROTATION_Z = 0.0F;
    public static final double HORN_X = 0.5D;
    public static final double HORN_Y = 0.5D / 16.0D + HORN_SCALE / 2.0D;
    public static final double HORN_Z = 0.5D;

    @Override
    public void render(TileYggdrasilBranch tile, double x, double y, double z, float partialTicks,
                       int destroyStage, float alpha) {
        OpenGLState glState = OpenGLState.capture();
        glState.push();
        try {
            IBlockState state = tile.getWorld().getBlockState(tile.getPos());
            EnumFacing facing = state.getValue(BlockYggdrasilBranch.FACING);
            renderManaResource(x, y, z, facing);
            renderStack(tile.getHorn(), x + HORN_X + facing.getXOffset() * FRONT_OFFSET,
                    y + HORN_Y, z + HORN_Z + facing.getZOffset() * FRONT_OFFSET,
                    facing, HORN_SCALE, HORN_ROTATION_X, HORN_ROTATION_Y, HORN_ROTATION_Z);
        } finally {
            glState.pop();
        }
    }

    private static void renderManaResource(double x, double y, double z, EnumFacing facing) {
        renderStack(new ItemStack(ModItems.manaResource, 1, 3),
                x + MANA_RESOURCE_X + facing.getXOffset() * FRONT_OFFSET,
                y + MANA_RESOURCE_Y,
                z + MANA_RESOURCE_Z + facing.getZOffset() * FRONT_OFFSET,
                facing, MANA_RESOURCE_SCALE, MANA_RESOURCE_ROTATION_X, MANA_RESOURCE_ROTATION_Y, MANA_RESOURCE_ROTATION_Z);
    }

    private static void renderStack(ItemStack stack, double x, double y, double z,
                                     EnumFacing facing, float scale,
                                     float rotationX, float rotationY, float rotationZ) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(facing.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
        // Base item/generated plane is placed on the branch top first.
        GlStateManager.rotate(90.0F + rotationX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(rotationY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(rotationZ, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(scale, scale, scale);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    /** Renders the complete branch item, including its permanent mana resource. */
    public static void renderItem(ItemStack stack, float partialTicks) {
        OpenGLState glState = OpenGLState.capture();
        glState.push();
        try {
            EnumFacing facing = EnumFacing.byHorizontalIndex(stack.getMetadata() & 3);
            IBlockState state = ModBlocks.yggdrasilBranch.getDefaultState()
                    .withProperty(BlockYggdrasilBranch.FACING, facing);
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.translate(0.5D, 0.5D, 0.5D);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(-0.5D, -0.5D, -0.5D);
            Minecraft.getMinecraft().getBlockRendererDispatcher()
                    .renderBlockBrightness(state, 1.0F);
            renderManaResource(0.0D, 0.0D, 0.0D, facing);
        } finally {
            glState.pop();
        }
    }

    /**
     * Item rendering in a TESR runs inside the inventory renderer, so every
     * server and client OpenGL attribute touched by the branch renderer must
     * be restored, not just the model matrix.
     */
    private static final class OpenGLState {
        private final int matrixMode;
        private final int activeTexture;
        private final int clientActiveTexture;
        private final float lightmapX;
        private final float lightmapY;

        private OpenGLState() {
            matrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
            activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
            clientActiveTexture = GL11.glGetInteger(GL13.GL_CLIENT_ACTIVE_TEXTURE);
            lightmapX = OpenGlHelper.lastBrightnessX;
            lightmapY = OpenGlHelper.lastBrightnessY;
        }

        private static OpenGLState capture() {
            return new OpenGLState();
        }

        private void push() {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPushClientAttrib(GL11.GL_CLIENT_PIXEL_STORE_BIT
                    | GL11.GL_CLIENT_VERTEX_ARRAY_BIT);
            GL11.glMatrixMode(matrixMode);
            GlStateManager.pushMatrix();
        }

        private void pop() {
            // A nested item renderer may leave another matrix mode selected.
            // Select the original stack before balancing our pushMatrix().
            GL11.glMatrixMode(matrixMode);
            GlStateManager.popMatrix();
            GL11.glPopClientAttrib();
            GL11.glPopAttrib();

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                    lightmapX, lightmapY);
            GL13.glActiveTexture(activeTexture);
            GL13.glClientActiveTexture(clientActiveTexture);
            GL11.glMatrixMode(matrixMode);
        }
    }
}
