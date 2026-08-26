package mythicbotany.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.client.render.world.SkyblockSkyRenderer;

/** Uses Botania's canonical animated skybox implementation for Alfheim. */
@SideOnly(Side.CLIENT)
public final class AlfheimSkyRenderer extends IRenderHandler {
    private final IRenderHandler delegate = new SkyblockSkyRenderer();

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft minecraft) {
        delegate.render(partialTicks, world, minecraft);
    }
}
