package mythicbotany.client;

import mythicbotany.entity.EntityMjoellnirPlaced;
import mythicbotany.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderMjoellnirPlaced extends Render<EntityMjoellnirPlaced> {
    private static final ModelResourceLocation MJOELLNIR_MODEL =
            new ModelResourceLocation("mythicbotany:mjoellnir", "normal");

    public RenderMjoellnirPlaced(RenderManager manager) {
        super(manager);
    }

    @Override
    public void doRender(EntityMjoellnirPlaced entity, double x, double y, double z,
                         float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.translate((float) x - 0.5F, (float) y - 0.5F, (float) z - 0.5F);
        float yaw = entity.prevRotationYaw
                + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        float pitch = entity.prevRotationPitch
                + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        GlStateManager.rotate(yaw - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, -1.0F);
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        net.minecraft.block.state.IBlockState state = ModBlocks.mjoellnir.getDefaultState();
        IBakedModel model = dispatcher.getBlockModelShapes().getModelForState(state);
        if (model == dispatcher.getBlockModelShapes().getModelManager().getMissingModel()) {
            model = dispatcher.getBlockModelShapes().getModelManager().getModel(MJOELLNIR_MODEL);
        }
        dispatcher.getBlockModelRenderer().renderModelBrightness(model, state, 1.0F, false);
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMjoellnirPlaced entity) {
        return null;
    }
}
