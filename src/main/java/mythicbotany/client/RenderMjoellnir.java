package mythicbotany.client;

import mythicbotany.entity.EntityMjoellnir;
import mythicbotany.registry.ModBlocks;
import mythicbotany.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderMjoellnir extends Render<EntityMjoellnir> {
    private final RenderItem renderItem;

    public RenderMjoellnir(RenderManager manager) {
        super(manager);
        renderItem = Minecraft.getMinecraft().getRenderItem();
        shadowSize = 0.0F;
    }

    @Override
    public void doRender(EntityMjoellnir entity, double x, double y, double z,
                         float entityYaw, float partialTicks) {
        ItemStack stack = entity.getItem();
        if (stack.isEmpty()) {
            stack = new ItemStack(ModItems.mjoellnir);
        }

        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher()
                .getModelForState(ModBlocks.mjoellnir.getDefaultState());
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x, y, z);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
            renderItem.renderItem(stack, model);
        } finally {
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMjoellnir entity) {
        return null;
    }
}
