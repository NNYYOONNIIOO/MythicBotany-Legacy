package mythicbotany.proxy;

import mythicbotany.client.ModelHandler;
import mythicbotany.client.RenderAlfPixie;
import mythicbotany.entity.EntityAlfPixie;
import mythicbotany.pylon.RenderAlfsteelPylon;
import mythicbotany.pylon.TileAlfsteelPylon;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientProxy extends CommonProxy {
    @Override
    public void preInit() {
        ModelHandler.registerSpecialFlowerModels();
    }

    @Override
    public void init() {
        RenderingRegistry.registerEntityRenderingHandler(EntityAlfPixie.class, RenderAlfPixie::new);
        ClientRegistry.bindTileEntitySpecialRenderer(TileAlfsteelPylon.class, new RenderAlfsteelPylon());
    }
}
