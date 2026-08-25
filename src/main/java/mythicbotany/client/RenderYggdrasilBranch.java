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
    // Local coordinates are measured from the branch block: X = left/right,
    // Y = up/down, Z = front/back. The complete local pose is rotated with
    // the block, so all four branch directions use the same settings.
    // The front-view plane is the local X/Y plane; rotationZ is therefore
    // the visible counter-clockwise/clockwise adjustment.
    public static final double MANA_RESOURCE_X = 0.5D;
    public static final double MANA_RESOURCE_Y = 0.9D;
    public static final double MANA_RESOURCE_Z = 0.5D;
    public static final float MANA_RESOURCE_SCALE = 0.8F;
    public static final float MANA_RESOURCE_ROTATION_X = 0.0F;
    // Base pose Y=90, Z=160; add the requested 90-degree front-view turn.
    public static final float MANA_RESOURCE_ROTATION_Y = 90.0F;
    public static final float MANA_RESOURCE_ROTATION_Z = 250.0F;
    public static final float HORN_SCALE = 1.0F;
    public static final float HORN_ROTATION_X = 0.0F;
    // Base pose Y=90; turn the horn 180 degrees in the front-view plane.
    public static final float HORN_ROTATION_Y = 90.0F;
    public static final float HORN_ROTATION_Z = 180.0F;
    public static final double HORN_X = 0.5D;
    public static final double HORN_Y = 0.1D;
    public static final double HORN_Z = 0.25D;

    @Override
    public void render(TileYggdrasilBranch tile, double x, double y, double z, float partialTicks,
                       int destroyStage, float alpha) {
        OpenGLState glState = OpenGLState.capture();
        glState.push();
        try {
            IBlockState state = tile.getWorld().getBlockState(tile.getPos());
            EnumFacing facing = state.getValue(BlockYggdrasilBranch.FACING);
            renderManaResource(x, y, z, facing);
            renderStack(tile.getHorn(), x, y, z, facing, HORN_X, HORN_Y, HORN_Z,
                    HORN_SCALE, HORN_ROTATION_X, HORN_ROTATION_Y, HORN_ROTATION_Z);
        } finally {
            glState.pop();
        }
    }

    private static void renderManaResource(double x, double y, double z, EnumFacing facing) {
        renderStack(new ItemStack(ModItems.manaResource, 1, 3),
                x, y, z, facing, MANA_RESOURCE_X, MANA_RESOURCE_Y, MANA_RESOURCE_Z,
                MANA_RESOURCE_SCALE, MANA_RESOURCE_ROTATION_X,
                MANA_RESOURCE_ROTATION_Y, MANA_RESOURCE_ROTATION_Z);
    }

    private static void renderStack(ItemStack stack, double x, double y, double z,
                                     EnumFacing facing, double localX, double localY,
                                     double localZ, float scale, float rotationX,
                                     float rotationY, float rotationZ) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        GlStateManager.rotate(facing.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(localX - 0.5D, localY, localZ - 0.5D);
        // GROUND is the same item context used by the current MythicBotany
        // renderer; rotation values therefore correspond directly to the
        // local X/Y/Z axes above instead of the GUI/FIXED transform.
        GlStateManager.rotate(rotationX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(rotationY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(rotationZ, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(scale, scale, scale);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
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
