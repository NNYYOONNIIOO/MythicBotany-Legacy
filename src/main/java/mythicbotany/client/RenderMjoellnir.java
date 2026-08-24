package mythicbotany.client;

import mythicbotany.entity.EntityMjoellnir;
import mythicbotany.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderMjoellnir extends Render<EntityMjoellnir> {
    /** Easy-to-tune model-only yaw offset; entity flight yaw remains separate. */
    private static final float MODEL_Y_ROTATION = 90.0F;

    public RenderMjoellnir(RenderManager manager) {
        super(manager);
        shadowSize = 0.0F;
    }

    @Override
    public void doRender(EntityMjoellnir entity, double x, double y, double z,
                         float entityYaw, float partialTicks) {
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        float yaw = entity.prevRotationYaw
                + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        float pitch = entity.prevRotationPitch
                + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x, y + 1.5D, z);
            EntityLivingBase thrower = entity.getThrower();
            if (thrower != null) {
                double entityX = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
                double entityY = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks;
                double entityZ = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
                double targetX = thrower.prevPosX + (thrower.posX - thrower.prevPosX) * partialTicks;
                double targetY = thrower.prevPosY + (thrower.posY - thrower.prevPosY) * partialTicks + thrower.getEyeHeight() * 0.5D;
                double targetZ = thrower.prevPosZ + (thrower.posZ - thrower.prevPosZ) * partialTicks;
                double directionX = targetX - entityX;
                double directionY = targetY - entityY;
                double directionZ = targetZ - entityZ;
                double length = Math.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
                if (length > 1.0E-6D) {
                    directionX /= length;
                    directionY /= length;
                    directionZ /= length;
                    double axisX = directionZ;
                    double axisZ = -directionX;
                    double axisLength = Math.sqrt(axisX * axisX + axisZ * axisZ);
                    float angle = (float) (Math.acos(Math.max(-1.0D, Math.min(1.0D, directionY))) * 180.0D / Math.PI);
                    if (axisLength > 1.0E-6D) {
                        GlStateManager.rotate(angle, (float) (axisX / axisLength), 0.0F, (float) (axisZ / axisLength));
                    } else if (directionY < 0.0D) {
                        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                    }
                    GlStateManager.rotate(MODEL_Y_ROTATION, 0.0F, 1.0F, 0.0F);
                }
            } else {
                GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(-MODEL_Y_ROTATION, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(-pitch, 1.0F, 0.0F, 0.0F);
            }
            if (entity.isReturning()) {
                // Flip around the model centre so the handle, rather than the head,
                // points at the player during the return flight.
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            }
            GlStateManager.translate(-0.5D, -0.5D, -0.5D);
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            dispatcher.renderBlockBrightness(ModBlocks.mjoellnir.getDefaultState(), 1.0F);
        } finally {
            GlStateManager.popMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMjoellnir entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
