package mythicbotany.client;

import mythicbotany.MythicBotany;
import mythicbotany.entity.EntityAlfPixie;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.client.model.ModelPixie;

public class RenderAlfPixie extends RenderLiving<EntityAlfPixie> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("botania", "textures/model/pixie.png");

    public RenderAlfPixie(RenderManager manager) {
        super(manager, new ModelPixie(), 0.25F);
        shadowSize = 0.0F;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityAlfPixie entity) {
        return TEXTURE;
    }
}
