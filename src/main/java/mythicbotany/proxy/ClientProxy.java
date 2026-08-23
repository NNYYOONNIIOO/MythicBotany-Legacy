package mythicbotany.proxy;

import mythicbotany.client.RenderAlfPixie;
import mythicbotany.entity.EntityAlfPixie;
import mythicbotany.pylon.RenderAlfsteelPylon;
import mythicbotany.pylon.TileAlfsteelPylon;
import mythicbotany.rune.TileCentralRuneHolder;
import mythicbotany.rune.TileRuneHolder;
import mythicbotany.client.RenderCentralRuneHolder;
import mythicbotany.client.RenderRuneHolder;
import mythicbotany.tile.TileManaInfuser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientProxy extends CommonProxy {
    private static boolean itemStackRendererRegistered;

    @Override
    public void init() {
        RenderingRegistry.registerEntityRenderingHandler(EntityAlfPixie.class, RenderAlfPixie::new);
        ClientRegistry.bindTileEntitySpecialRenderer(TileAlfsteelPylon.class, new RenderAlfsteelPylon());
        ClientRegistry.bindTileEntitySpecialRenderer(TileRuneHolder.class, new RenderRuneHolder());
        ClientRegistry.bindTileEntitySpecialRenderer(TileCentralRuneHolder.class, new RenderCentralRuneHolder());
        if (!itemStackRendererRegistered) {
            TileEntityItemStackRenderer.instance = new RenderAlfsteelPylon.ForwardingTEISR(
                    TileEntityItemStackRenderer.instance);
            itemStackRendererRegistered = true;
        }
    }

    @Override
    public void handleInfuserEffect(final int x, final int y, final int z, final int mana,
                                    final int requirement, final boolean complete) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                TileManaInfuser.spawnProgressParticles(new BlockPos(x, y, z), mana, requirement, complete);
            }
        });
    }
}
