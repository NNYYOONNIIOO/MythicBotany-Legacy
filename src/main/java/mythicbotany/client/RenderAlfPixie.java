package mythicbotany.client;

import mythicbotany.MythicBotany;
import mythicbotany.entity.EntityAlfPixie;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderAlfPixie extends RenderLiving<EntityAlfPixie> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            MythicBotany.MODID, "textures/entity/alf_pixie.png");

    public RenderAlfPixie(RenderManager manager) {
        super(manager, new ModelBiped(0.0F, 0.0F, 64, 32), 0.35F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityAlfPixie entity) {
        return TEXTURE;
    }
}
