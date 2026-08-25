package mythicbotany.client;

import mythicbotany.block.BlockYggdrasilBranch;
import mythicbotany.registry.ModBlocks;
import mythicbotany.tile.TileYggdrasilBranch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import java.nio.FloatBuffer;
import vazkii.botania.common.item.ModItems;

/** Renders the permanent resource and the stored Gjallar horn on a branch. */
public class RenderYggdrasilBranch extends TileEntitySpecialRenderer<TileYggdrasilBranch> {
    // Local block coordinates: X = left/right, Y = up/down, Z = front/back.
    // The blockstate rotation is applied to this complete local pose.
    public static final double MANA_RESOURCE_X = 0.5D;
    public static final double MANA_RESOURCE_Y = 0.9D;
    public static final double MANA_RESOURCE_Z = 0.5D;
    public static final float MANA_RESOURCE_SCALE = 0.8F;
    public static final float MANA_RESOURCE_ROTATION_X = 0.0F;
    public static final float MANA_RESOURCE_ROTATION_Y = 90.0F;
    // Native front-view pose Z=160 plus the requested 90-degree adjustment.
    public static final float MANA_RESOURCE_ROTATION_Z = 250.0F;

    public static final float HORN_SCALE = 1.0F;
    // The requested left-to-right 180-degree turn for every branch facing.
    public static final float HORN_ROTATION_X = 180.0F;
    public static final float HORN_ROTATION_Y = 90.0F;
    public static final float HORN_ROTATION_Z = 0.0F;
    public static final double HORN_X = 0.5D;
    public static final double HORN_Y = 0.1D;
    public static final double HORN_Z = 0.25D;
    /** Sixteen pixels in local block coordinates. */
    public static final double HORN_SIDE_OFFSET = 1.0D;

    @Override
    public void render(TileYggdrasilBranch tile, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        OpenGLState glState = OpenGLState.capture();
        glState.push();
        try {
            IBlockState state = tile.getWorld().getBlockState(tile.getPos());
            EnumFacing facing = state.getValue(BlockYggdrasilBranch.FACING);
            int packedLight = tile.getWorld().getCombinedLight(tile.getPos(), 0);
            renderManaResource(x, y, z, facing, packedLight);
            renderStack(tile.getHorn(), x, y, z, facing,
                    getHornX(facing), HORN_Y, HORN_Z,
                    HORN_SCALE, HORN_ROTATION_X, HORN_ROTATION_Y,
                    HORN_ROTATION_Z, packedLight, false);
        } finally {
            glState.pop();
        }
    }

    private static void renderManaResource(double x, double y, double z,
                                           EnumFacing facing, int packedLight) {
        renderStack(new ItemStack(ModItems.manaResource, 1, 3),
                x, y, z, facing,
                MANA_RESOURCE_X, MANA_RESOURCE_Y, MANA_RESOURCE_Z,
                MANA_RESOURCE_SCALE, MANA_RESOURCE_ROTATION_X,
                getManaResourceRotationY(facing), MANA_RESOURCE_ROTATION_Z,
                packedLight, true);
    }

    private static float getManaResourceRotationY(EnumFacing facing) {
        // North and south are the requested top-down 180-degree turns.
        switch (facing) {
            case NORTH:
            case SOUTH:
                return MANA_RESOURCE_ROTATION_Y + 180.0F;
            default:
                return MANA_RESOURCE_ROTATION_Y;
        }
    }

    private static double getHornX(EnumFacing facing) {
        // For east/west, local X is the observer's right in the side view.
        // One local block unit is exactly the requested 16-pixel displacement.
        switch (facing) {
            case EAST:
            case WEST:
                return HORN_X + HORN_SIDE_OFFSET;
            default:
                return HORN_X;
        }
    }

    private static float getBranchRotation(EnumFacing facing) {
        // Must match blockstates/yggdrasil_branch.json exactly.
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

    private static void setLightmap(int packedLight) {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                packedLight & 65535, packedLight >>> 16);
    }

    private static void renderStack(ItemStack stack, double x, double y, double z,
                                    EnumFacing facing, double localX, double localY,
                                    double localZ, float scale, float rotationX,
                                    float rotationY, float rotationZ, int packedLight,
                                    boolean fullbright) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        int matrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        int activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        int defaultTexture = captureTexture(OpenGlHelper.defaultTexUnit);
        int lightmapTexture = captureTexture(OpenGlHelper.lightmapTexUnit);
        GL13.glActiveTexture(activeTexture);
        GlStateManager.setActiveTexture(activeTexture);
        float oldLightmapX = OpenGlHelper.lastBrightnessX;
        float oldLightmapY = OpenGlHelper.lastBrightnessY;
        FloatBuffer color = BufferUtils.createFloatBuffer(4);
        GL11.glGetFloat(GL11.GL_CURRENT_COLOR, color);
        float oldColorR = color.get(0);
        float oldColorG = color.get(1);
        float oldColorB = color.get(2);
        float oldColorA = color.get(3);

        synchronizeRenderStateCache();
        GlStateManager.pushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.color(0.0F, 0.0F, 0.0F, 0.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            setLightmap(fullbright ? 0xF000F0 : packedLight);
            GL13.glActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.enableTexture2D();
            // RenderItem binds the block atlas itself. Avoid manually binding
            // it here, which is what caused stale texture-cache black planes.
            GL11.glDisable(GL11.GL_CULL_FACE);
            GlStateManager.disableCull();

            GlStateManager.translate(x + 0.5D, y, z + 0.5D);
            GlStateManager.rotate(getBranchRotation(facing), 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(localX - 0.5D, localY, localZ - 0.5D);
            GlStateManager.rotate(rotationX, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(rotationY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(rotationZ, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(scale, scale, scale);
            Minecraft.getMinecraft().getRenderItem()
                    .renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
        } finally {
            GL11.glMatrixMode(matrixMode);
            GlStateManager.popMatrix();
            GL11.glPopAttrib();

            restoreTexture(OpenGlHelper.defaultTexUnit, defaultTexture);
            restoreTexture(OpenGlHelper.lightmapTexUnit, lightmapTexture);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                    oldLightmapX, oldLightmapY);
            GL13.glActiveTexture(activeTexture);
            GlStateManager.setActiveTexture(activeTexture);
            synchronizeRenderStateCache();
            GL11.glColor4f(oldColorR, oldColorG, oldColorB, oldColorA);
            GlStateManager.color(0.0F, 0.0F, 0.0F, 0.0F);
            GlStateManager.color(oldColorR, oldColorG, oldColorB, oldColorA);
        }
    }

    private static int captureTexture(int textureUnit) {
        GL13.glActiveTexture(textureUnit);
        return GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
    }

    private static void restoreTexture(int textureUnit, int texture) {
        GL13.glActiveTexture(textureUnit);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GlStateManager.setActiveTexture(textureUnit);
        GlStateManager.bindTexture(texture);
    }

    private static void synchronizeRenderStateCache() {
        synchronizeTextureCache();
        synchronizeCapability(GL11.GL_TEXTURE_2D, GL11.glIsEnabled(GL11.GL_TEXTURE_2D),
                GlStateManager::enableTexture2D, GlStateManager::disableTexture2D);
        synchronizeCapability(GL11.GL_ALPHA_TEST, GL11.glIsEnabled(GL11.GL_ALPHA_TEST),
                GlStateManager::enableAlpha, GlStateManager::disableAlpha);
        synchronizeCapability(GL11.GL_BLEND, GL11.glIsEnabled(GL11.GL_BLEND),
                GlStateManager::enableBlend, GlStateManager::disableBlend);
        synchronizeCapability(GL11.GL_LIGHTING, GL11.glIsEnabled(GL11.GL_LIGHTING),
                GlStateManager::enableLighting, GlStateManager::disableLighting);
        synchronizeCapability(GL11.GL_CULL_FACE, GL11.glIsEnabled(GL11.GL_CULL_FACE),
                GlStateManager::enableCull, GlStateManager::disableCull);
        synchronizeCapability(GL12.GL_RESCALE_NORMAL, GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL),
                GlStateManager::enableRescaleNormal, GlStateManager::disableRescaleNormal);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void synchronizeTextureCache() {
        int activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        synchronizeTextureUnit(OpenGlHelper.defaultTexUnit);
        synchronizeTextureUnit(OpenGlHelper.lightmapTexUnit);
        GL13.glActiveTexture(activeTexture);
        GlStateManager.setActiveTexture(activeTexture);
    }

    private static void synchronizeTextureUnit(int textureUnit) {
        GL13.glActiveTexture(textureUnit);
        int texture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GlStateManager.setActiveTexture(textureUnit);
        GlStateManager.bindTexture(texture);
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

    /** Renders the branch block and its permanent resource as an item. */
    public static void renderItem(ItemStack stack, float partialTicks) {
        OpenGLState glState = OpenGLState.capture();
        glState.push();
        try {
            EnumFacing facing = EnumFacing.byHorizontalIndex(stack.getMetadata() & 3);
            IBlockState state = ModBlocks.yggdrasilBranch.getDefaultState()
                    .withProperty(BlockYggdrasilBranch.FACING, facing);
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            setLightmap(0xF000F0);
            GlStateManager.translate(0.5D, 0.5D, 0.5D);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(-0.5D, -0.5D, -0.5D);
            Minecraft.getMinecraft().getBlockRendererDispatcher()
                    .renderBlockBrightness(state, 1.0F);
            renderManaResource(0.0D, 0.0D, 0.0D, facing, 0xF000F0);
        } finally {
            glState.pop();
        }
    }

    /** Captures and restores the fixed-function state used by this TESR. */
    private static final class OpenGLState {
        private final int matrixMode;
        private final int activeTexture;
        private final int clientActiveTexture;
        private final int defaultTexture;
        private final int lightmapTexture;
        private final float lightmapX;
        private final float lightmapY;
        private final float colorR;
        private final float colorG;
        private final float colorB;
        private final float colorA;

        private OpenGLState() {
            matrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
            activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
            clientActiveTexture = GL11.glGetInteger(GL13.GL_CLIENT_ACTIVE_TEXTURE);
            defaultTexture = captureTexture(OpenGlHelper.defaultTexUnit);
            lightmapTexture = captureTexture(OpenGlHelper.lightmapTexUnit);
            GL13.glActiveTexture(activeTexture);
            GlStateManager.setActiveTexture(activeTexture);
            lightmapX = OpenGlHelper.lastBrightnessX;
            lightmapY = OpenGlHelper.lastBrightnessY;
            FloatBuffer color = BufferUtils.createFloatBuffer(4);
            GL11.glGetFloat(GL11.GL_CURRENT_COLOR, color);
            colorR = color.get(0);
            colorG = color.get(1);
            colorB = color.get(2);
            colorA = color.get(3);
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
            GL11.glMatrixMode(matrixMode);
            GlStateManager.popMatrix();
            GL11.glPopClientAttrib();
            GL11.glPopAttrib();
            restoreTexture(OpenGlHelper.defaultTexUnit, defaultTexture);
            restoreTexture(OpenGlHelper.lightmapTexUnit, lightmapTexture);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                    lightmapX, lightmapY);
            GL13.glActiveTexture(activeTexture);
            GlStateManager.setActiveTexture(activeTexture);
            GL13.glClientActiveTexture(clientActiveTexture);
            GL11.glMatrixMode(matrixMode);
            synchronizeRenderStateCache();
            GL11.glColor4f(colorR, colorG, colorB, colorA);
            GlStateManager.color(0.0F, 0.0F, 0.0F, 0.0F);
            GlStateManager.color(colorR, colorG, colorB, colorA);
        }
    }
}
