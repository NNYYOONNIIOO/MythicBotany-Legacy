package mythicbotany.client;

import mythicbotany.block.BlockYggdrasilBranch;
import mythicbotany.registry.ModBlocks;
import mythicbotany.tile.TileYggdrasilBranch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import java.nio.FloatBuffer;
import vazkii.botania.common.item.ModItems;

/** Renders a horn stored in a placed Yggdrasil branch. */
public class RenderYggdrasilBranch extends TileEntitySpecialRenderer<TileYggdrasilBranch> {
    // Local coordinates are measured from the branch block: X = left/right,
    // Y = up/down, Z = front/back. The complete local pose is rotated with
    // the block, so all four branch directions use the same settings.
    // After branch alignment, X/Y/Z are the model's left/right, up/down and
    // front/back axes. A top-down turn uses rotationY; a left-to-right view
    // turn uses rotationX.
    public static final double MANA_RESOURCE_X = 0.5D;
    public static final double MANA_RESOURCE_Y = 0.9D;
    public static final double MANA_RESOURCE_Z = 0.5D;
    public static final float MANA_RESOURCE_SCALE = 0.8F;
    public static final float MANA_RESOURCE_ROTATION_X = 0.0F;
    // Base pose Y=90, Z=250. North and south receive an additional 180°
    // around the vertical axis when viewed from above.
    public static final float MANA_RESOURCE_ROTATION_Y = 90.0F;
    public static final float MANA_RESOURCE_ROTATION_Z = 250.0F;
    public static final float HORN_SCALE = 1.0F;
    // The requested 180° is seen from left to right, so it is a local-X turn.
    public static final float HORN_ROTATION_X = 180.0F;
    public static final float HORN_ROTATION_Y = 90.0F;
    public static final float HORN_ROTATION_Z = 0.0F;
    public static final double HORN_X = 0.5D;
    public static final double HORN_Y = 0.1D;
    public static final double HORN_Z = 0.25D;
    /** Sixteen pixels in block-local coordinates. */
    public static final double HORN_SIDE_OFFSET = 1.0D;

    @Override
    public void render(TileYggdrasilBranch tile, double x, double y, double z, float partialTicks,
                       int destroyStage, float alpha) {
        OpenGLState glState = OpenGLState.capture();
        glState.push();
        try {
            IBlockState state = tile.getWorld().getBlockState(tile.getPos());
            EnumFacing facing = state.getValue(BlockYggdrasilBranch.FACING);
            int packedLight = tile.getWorld().getCombinedLight(tile.getPos(), 0);
            renderManaResource(x, y, z, facing, packedLight);
            renderStack(tile.getHorn(), x, y, z, facing, getHornX(facing), HORN_Y, HORN_Z,
                    HORN_SCALE, HORN_ROTATION_X, HORN_ROTATION_Y, HORN_ROTATION_Z,
                    packedLight, false);
        } finally {
            glState.pop();
        }
    }

    private static void renderManaResource(double x, double y, double z, EnumFacing facing,
                                           int packedLight) {
        renderStack(new ItemStack(ModItems.manaResource, 1, 3),
                x, y, z, facing, MANA_RESOURCE_X, MANA_RESOURCE_Y, MANA_RESOURCE_Z,
                MANA_RESOURCE_SCALE, MANA_RESOURCE_ROTATION_X,
                getManaResourceRotationY(facing), MANA_RESOURCE_ROTATION_Z, packedLight, true);
    }

    private static float getManaResourceRotationY(EnumFacing facing) {
        switch (facing) {
            case NORTH:
            case SOUTH:
                return MANA_RESOURCE_ROTATION_Y + 180.0F;
            default:
                return MANA_RESOURCE_ROTATION_Y;
        }
    }

    private static double getHornX(EnumFacing facing) {
        switch (facing) {
            case EAST:
            case WEST:
                // The model's own right is local X. The branch rotation then
                // carries this 16-pixel offset to the correct world side.
                return HORN_X + HORN_SIDE_OFFSET;
            default:
                return HORN_X;
        }
    }

    private static void setLightmap(int packedLight) {
        int blockLight = packedLight % 65536;
        int skyLight = packedLight / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                blockLight, skyLight);
    }

    /** Matches the y rotations declared by blockstates/yggdrasil_branch.json. */
    private static float getBranchRotation(EnumFacing facing) {
        switch (facing) {
            case EAST:
                return 90.0F;
            case SOUTH:
                return 180.0F;
            case WEST:
                return 270.0F;
            default:
                return 0.0F;
        }
    }

    private static void renderStack(ItemStack stack, double x, double y, double z,
                                     EnumFacing facing, double localX, double localY,
                                     double localZ, float scale, float rotationX,
                                     float rotationY, float rotationZ, int packedLight,
                                     boolean fullbright) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        float oldLightmapX = OpenGlHelper.lastBrightnessX;
        float oldLightmapY = OpenGlHelper.lastBrightnessY;
        int oldActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        GlStateManager.pushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            GL13.glActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            int blockLight = fullbright ? 240 : packedLight & 65535;
            int skyLight = fullbright ? 240 : packedLight >>> 16;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                    blockLight, skyLight);

            GL13.glActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GlStateManager.enableTexture2D();
            Minecraft.getMinecraft().getTextureManager()
                    .bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            // RenderItem needs the normal item-light setup even for fullbright
            // attachments. Disabling lighting here is what produced black
            // planes when the branch was rendered beside EntityItems.
            RenderHelper.enableStandardItemLighting();
            GL11.glDisable(GL11.GL_CULL_FACE);
            GlStateManager.disableCull();

            GlStateManager.translate(x + 0.5D, y, z + 0.5D);
            // This matches blockstates/yggdrasil_branch.json, so the local
            // attachment pose follows all four branch orientations.
            GlStateManager.rotate(getBranchRotation(facing), 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(localX - 0.5D, localY, localZ - 0.5D);
            GlStateManager.rotate(rotationX, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(rotationY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(rotationZ, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(scale, scale, scale);
            Minecraft.getMinecraft().getRenderItem()
                    .renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
        } finally {
            RenderHelper.disableStandardItemLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                    oldLightmapX, oldLightmapY);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
            GL11.glPopAttrib();
            GL13.glActiveTexture(oldActiveTexture);
            GlStateManager.setActiveTexture(oldActiveTexture);
            synchronizeRenderStateCache();
        }
    }
    private static void prepareItemRenderState(int packedLight) {
        synchronizeCurrentTextureBindings();
        // RenderItem binds item textures on the default unit. A previous
        // EntityItem/TESR may leave the lightmap unit active even though the
        // GlStateManager cache says otherwise.
        GL13.glActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        setLightmap(packedLight);
        GL13.glActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GL11.glEnable(GL11.GL_LIGHTING);
        GlStateManager.enableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void synchronizeCurrentTextureBindings() {
        int activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        synchronizeCurrentTextureUnit(OpenGlHelper.defaultTexUnit);
        synchronizeCurrentTextureUnit(OpenGlHelper.lightmapTexUnit);
        GL13.glActiveTexture(activeTexture);
        GlStateManager.setActiveTexture(activeTexture);
    }

    private static void synchronizeCurrentTextureUnit(int textureUnit) {
        GL13.glActiveTexture(textureUnit);
        int texture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GlStateManager.setActiveTexture(textureUnit);
        GlStateManager.bindTexture(texture);
    }

   /** Renders the complete branch item, including its permanent mana resource. */
    /** Synchronizes GlStateManager after a nested RenderItem call. */
    private static void synchronizeRenderStateCache() {
        synchronizeCurrentTextureBindings();
        OpenGLState.synchronizeCapability(GL11.GL_TEXTURE_2D,
                GL11.glIsEnabled(GL11.GL_TEXTURE_2D),
                GlStateManager::enableTexture2D, GlStateManager::disableTexture2D);
        OpenGLState.synchronizeCapability(GL11.GL_ALPHA_TEST,
                GL11.glIsEnabled(GL11.GL_ALPHA_TEST),
                GlStateManager::enableAlpha, GlStateManager::disableAlpha);
        OpenGLState.synchronizeCapability(GL11.GL_BLEND,
                GL11.glIsEnabled(GL11.GL_BLEND),
                GlStateManager::enableBlend, GlStateManager::disableBlend);
        OpenGLState.synchronizeCapability(GL11.GL_LIGHTING,
                GL11.glIsEnabled(GL11.GL_LIGHTING),
                GlStateManager::enableLighting, GlStateManager::disableLighting);
        OpenGLState.synchronizeCapability(GL11.GL_CULL_FACE,
                GL11.glIsEnabled(GL11.GL_CULL_FACE),
                GlStateManager::enableCull, GlStateManager::disableCull);
        OpenGLState.synchronizeCapability(GL12.GL_RESCALE_NORMAL,
                GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL),
                GlStateManager::enableRescaleNormal, GlStateManager::disableRescaleNormal);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
    public static void renderItem(ItemStack stack, float partialTicks) {
        OpenGLState glState = OpenGLState.capture();
        glState.push();
        try {
            EnumFacing facing = EnumFacing.byHorizontalIndex(stack.getMetadata() & 3);
            IBlockState state = ModBlocks.yggdrasilBranch.getDefaultState()
                    .withProperty(BlockYggdrasilBranch.FACING, facing);
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                    240.0F, 240.0F);
            GlStateManager.translate(0.5D, 0.5D, 0.5D);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(-0.5D, -0.5D, -0.5D);
            Minecraft.getMinecraft().getBlockRendererDispatcher()
                    .renderBlockBrightness(state, 1.0F);
            renderStack(new ItemStack(ModItems.manaResource, 1, 3),
                    0.0D, 0.0D, 0.0D, facing, MANA_RESOURCE_X, MANA_RESOURCE_Y,
                    MANA_RESOURCE_Z, MANA_RESOURCE_SCALE, MANA_RESOURCE_ROTATION_X,
                    getManaResourceRotationY(facing), MANA_RESOURCE_ROTATION_Z,
                    0xF000F0, true);
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
        private final float colorR;
        private final float colorG;
        private final float colorB;
        private final float colorA;
        private final int defaultTexture;
        private final int lightmapTexture;

        private OpenGLState() {
            matrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
            activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
            clientActiveTexture = GL11.glGetInteger(GL13.GL_CLIENT_ACTIVE_TEXTURE);
            lightmapX = OpenGlHelper.lastBrightnessX;
            lightmapY = OpenGlHelper.lastBrightnessY;
            FloatBuffer color = BufferUtils.createFloatBuffer(4);
            GL11.glGetFloat(GL11.GL_CURRENT_COLOR, color);
            colorR = color.get(0);
            colorG = color.get(1);
            colorB = color.get(2);
            colorA = color.get(3);
            defaultTexture = getTextureBinding(OpenGlHelper.defaultTexUnit);
            lightmapTexture = getTextureBinding(OpenGlHelper.lightmapTexUnit);
            GL13.glActiveTexture(activeTexture);
            GlStateManager.setActiveTexture(activeTexture);
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
            synchronizeCachedState();
        }

        private static int getTextureBinding(int textureUnit) {
            GL13.glActiveTexture(textureUnit);
            return GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        }

        /**
         * glPushAttrib restores OpenGL itself, but not GlStateManager's cached
         * state. Synchronize both after nested RenderItem calls; otherwise a
         * later EntityItem can skip enabling lighting/textures and render the
         * attached model as a black quad.
         */
        private void synchronizeCachedState() {
            // glPopAttrib restores the real texture bindings, but not the
            // bindings cached by GlStateManager. If the cache is left stale,
            // the next dropped item can skip its bind call and become black.
            synchronizeTextureUnit(OpenGlHelper.defaultTexUnit, defaultTexture);
            synchronizeTextureUnit(OpenGlHelper.lightmapTexUnit, lightmapTexture);
            GlStateManager.setActiveTexture(activeTexture);

            synchronizeCapability(GL11.GL_TEXTURE_2D, GL11.glIsEnabled(GL11.GL_TEXTURE_2D),
                    GlStateManager::enableTexture2D, GlStateManager::disableTexture2D);
            synchronizeCapability(GL11.GL_ALPHA_TEST, GL11.glIsEnabled(GL11.GL_ALPHA_TEST),
                    GlStateManager::enableAlpha, GlStateManager::disableAlpha);
            synchronizeCapability(GL11.GL_BLEND, GL11.glIsEnabled(GL11.GL_BLEND),
                    GlStateManager::enableBlend, GlStateManager::disableBlend);
            synchronizeCapability(GL11.GL_CULL_FACE, GL11.glIsEnabled(GL11.GL_CULL_FACE),
                    GlStateManager::enableCull, GlStateManager::disableCull);
            synchronizeCapability(GL11.GL_DEPTH_TEST, GL11.glIsEnabled(GL11.GL_DEPTH_TEST),
                    GlStateManager::enableDepth, GlStateManager::disableDepth);
            synchronizeCapability(GL11.GL_LIGHTING, GL11.glIsEnabled(GL11.GL_LIGHTING),
                    GlStateManager::enableLighting, GlStateManager::disableLighting);
            synchronizeCapability(GL12.GL_RESCALE_NORMAL, GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL),
                    GlStateManager::enableRescaleNormal, GlStateManager::disableRescaleNormal);

            GL11.glColor4f(colorR, colorG, colorB, colorA);
            GlStateManager.color(0.0F, 0.0F, 0.0F, 0.0F);
            GlStateManager.color(colorR, colorG, colorB, colorA);
            GL11.glColor4f(colorR, colorG, colorB, colorA);
        }

        private static void synchronizeTextureUnit(int textureUnit, int texture) {
            GL13.glActiveTexture(textureUnit);
            boolean enabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
            GlStateManager.setActiveTexture(textureUnit);
            GlStateManager.bindTexture(texture);
            if (enabled) {
                GlStateManager.disableTexture2D();
                GlStateManager.enableTexture2D();
            } else {
                GlStateManager.enableTexture2D();
                GlStateManager.disableTexture2D();
            }
        }

        private static void synchronizeCapability(int capability, boolean enabled,
                                                   Runnable enable, Runnable disable) {
            if (enabled) {
                GL11.glEnable(capability);
                disable.run();
                enable.run();
            } else {
                GL11.glDisable(capability);
                enable.run();
                disable.run();
            }
        }
    }
}
