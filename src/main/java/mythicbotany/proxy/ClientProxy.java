package mythicbotany.proxy;

import mythicbotany.client.RenderAlfPixie;
import mythicbotany.entity.EntityAlfPixie;
import mythicbotany.pylon.RenderAlfsteelPylon;
import mythicbotany.pylon.TileAlfsteelPylon;
import mythicbotany.rune.TileCentralRuneHolder;
import mythicbotany.rune.TileRuneHolder;
import mythicbotany.client.RenderCentralRuneHolder;
import mythicbotany.client.RenderRuneHolder;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        RenderingRegistry.registerEntityRenderingHandler(EntityAlfPixie.class, RenderAlfPixie::new);
        ClientRegistry.bindTileEntitySpecialRenderer(TileAlfsteelPylon.class, new RenderAlfsteelPylon());
        ClientRegistry.bindTileEntitySpecialRenderer(TileRuneHolder.class, new RenderRuneHolder());
        ClientRegistry.bindTileEntitySpecialRenderer(TileCentralRuneHolder.class, new RenderCentralRuneHolder());
        TileEntityItemStackRenderer.instance = new RenderAlfsteelPylon.ForwardingTEISR(
                TileEntityItemStackRenderer.instance);
    }
}
