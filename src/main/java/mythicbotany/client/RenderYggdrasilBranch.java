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
import net.minecraft.client.renderer.texture.TextureMap;
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
    // Native pose plus the requested 90-degree front-view adjustment.
    public static final float MANA_RESOURCE_ROTATION_Z = 250.0F;

    public static final float HORN_SCALE = 1.0F;
    // North/south item-center turn; east/west use their independent Z turn.
    public static final float HORN_ROTATION_X = 180.0F;
    public static final float HORN_ROTATION_Y = 270.0F;
    /** Independent top-down facing angles; initialized to the current pose. */
    public static final float HORN_NORTH_SOUTH_ROTATION_Y = HORN_ROTATION_Y;
    public static final float HORN_EAST_WEST_ROTATION_Y = HORN_ROTATION_Y;
    public static final float HORN_ROTATION_Z = 0.0F;
    /** Extra in-screen flip used only by east/west-facing branches. */
    public static final float HORN_EAST_WEST_ROTATION_Z = 0.0F;
    public static final double HORN_X = 0.5D;
    public static final double HORN_Y = 0.1D;
    public static final double HORN_Z = 0.25D;

    public static final double HORN_LEFT_OFFSET = 0.0D;
    public static final double HORN_FORWARD_OFFSET = 8.0D / 16.0D;

    @Override
    public void render(TileYggdrasilBranch tile, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        OpenGLState state = OpenGLState.capture();
        state.push();
        try {
            IBlockState blockState = tile.getWorld().getBlockState(tile.getPos());
            EnumFacing facing = blockState.getValue(BlockYggdrasilBranch.FACING);
            int packedLight = tile.getWorld().getCombinedLight(tile.getPos(), 0);
            renderManaResource(x, y, z, facing, packedLight);
            renderStack(tile.getHorn(), x, y, z, facing,
                    getHornX(facing), HORN_Y, getHornZ(facing),
                    HORN_SCALE, getHornRotationX(facing), getHornRotationY(facing),
                    getHornRotationZ(facing), packedLight, false);
        } finally {
            state.pop();
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
        // North and south are the two requested top-down 180-degree turns.
        switch (facing) {
            case NORTH:
            case SOUTH:
                return MANA_RESOURCE_ROTATION_Y + 180.0F;
            default:
                return MANA_RESOURCE_ROTATION_Y;
        }
    }

    private static double getHornX(EnumFacing facing) {
        // In the east/west side view, local X is the observer's right. One
        // local block unit is exactly the requested 16-pixel displacement.
        switch (facing) {
            case EAST:
            case WEST:
                return HORN_X + HORN_LEFT_OFFSET;
            default:
                return HORN_X;
        }
    }

    private static double getHornZ(EnumFacing facing) {
        // For east/west branches the requested front-to-back view moves the
        // horn one block unit forward in addition to the leftward offset.
        switch (facing) {
            case EAST:
            case WEST:
                return HORN_Z + HORN_FORWARD_OFFSET;
            default:
                return HORN_Z;
        }
    }

    private static float getHornRotationX(EnumFacing facing) {
        switch (facing) {
            case NORTH:
            case SOUTH:
                return HORN_ROTATION_X;
            default:
                return 0.0F;
        }
    }

    private static float getHornRotationY(EnumFacing facing) {
        switch (facing) {
            case NORTH:
            case SOUTH:
                return HORN_NORTH_SOUTH_ROTATION_Y;
            default:
                return HORN_EAST_WEST_ROTATION_Y;
        }
    }

    private static float getHornRotationZ(EnumFacing facing) {
        switch (facing) {
            case EAST:
            case WEST:
                return HORN_EAST_WEST_ROTATION_Z;
            default:
                return HORN_ROTATION_Z;
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

    private static void renderStack(ItemStack stack, double x, double y, double z,
                                    EnumFacing facing, double localX, double localY,
                                    double localZ, float scale, float rotationX,
                                    float rotationY, float rotationZ, int packedLight,
                                    boolean fullbright) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        OpenGLState state = OpenGLState.capture();
        state.push();
        try {
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            forceColor(1.0F, 1.0F, 1.0F, 1.0F);

            // The permanent resource uses maximum light, but keeps vanilla
            // item lighting enabled. Disabling GL_LIGHTING causes a generated
            // item quad to become a black rectangle after an EntityItem render.
            setLightmap(fullbright ? 0xF000F0 : packedLight);
            GL13.glActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.enableTexture2D();
            Minecraft.getMinecraft().getTextureManager()
                    .bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            // Item/generated models are flat quads. Culling is disabled only
            // inside this scope; the state wrapper restores it afterwards.
            GlStateManager.disableCull();

            // Match blockstates/yggdrasil_branch.json before applying local
            // position and model rotations, so every attachment follows facing.
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
            state.pop();
        }
    }

    private static void setLightmap(int packedLight) {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                packedLight & 65535, packedLight >>> 16);
        GL13.glActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /** Renders the branch block and its permanent resource as an item. */
    public static void renderItem(ItemStack stack, float partialTicks) {
        OpenGLState state = OpenGLState.capture();
        state.push();
        try {
            EnumFacing facing = EnumFacing.byHorizontalIndex(stack.getMetadata() & 3);
            IBlockState blockState = ModBlocks.yggdrasilBranch.getDefaultState()
                    .withProperty(BlockYggdrasilBranch.FACING, facing);
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            setLightmap(0xF000F0);
            GlStateManager.translate(0.5D, 0.5D, 0.5D);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(-0.5D, -0.5D, -0.5D);
            Minecraft.getMinecraft().getBlockRendererDispatcher()
                    .renderBlockBrightness(blockState, 1.0F);
            renderManaResource(0.0D, 0.0D, 0.0D, facing, 0xF000F0);
        } finally {
            state.pop();
        }
    }

    /** Captures and restores all fixed-function state touched by one render. */
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
        private final boolean texture;
        private final boolean alpha;
        private final boolean blend;
        private final boolean lighting;
        private final boolean cull;
        private final boolean depth;
        private final boolean rescale;

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
            texture = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
            alpha = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);
            blend = GL11.glIsEnabled(GL11.GL_BLEND);
            lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
            cull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
            depth = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
            rescale = GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL);
        }

        private static OpenGLState capture() {
            return new OpenGLState();
        }

        private static int captureTexture(int textureUnit) {
            GL13.glActiveTexture(textureUnit);
            return GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
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
            restoreCapability(GL11.GL_TEXTURE_2D, texture,
                    GlStateManager::enableTexture2D, GlStateManager::disableTexture2D);
            restoreCapability(GL11.GL_ALPHA_TEST, alpha,
                    GlStateManager::enableAlpha, GlStateManager::disableAlpha);
            restoreCapability(GL11.GL_BLEND, blend,
                    GlStateManager::enableBlend, GlStateManager::disableBlend);
            restoreCapability(GL11.GL_LIGHTING, lighting,
                    GlStateManager::enableLighting, GlStateManager::disableLighting);
            restoreCapability(GL11.GL_CULL_FACE, cull,
                    GlStateManager::enableCull, GlStateManager::disableCull);
            restoreCapability(GL11.GL_DEPTH_TEST, depth,
                    GlStateManager::enableDepth, GlStateManager::disableDepth);
            restoreCapability(GL12.GL_RESCALE_NORMAL, rescale,
                    GlStateManager::enableRescaleNormal, GlStateManager::disableRescaleNormal);
            forceColor(colorR, colorG, colorB, colorA);
        }
    }

    private static void restoreTexture(int textureUnit, int texture) {
        GL13.glActiveTexture(textureUnit);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GlStateManager.setActiveTexture(textureUnit);
        GlStateManager.bindTexture(texture);
    }

    private static void restoreCapability(int capability, boolean enabled,
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

    private static void forceColor(float red, float green, float blue, float alpha) {
        GL11.glColor4f(red, green, blue, alpha);
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.color(red, green, blue, alpha);
        GL11.glColor4f(red, green, blue, alpha);
    }
}
