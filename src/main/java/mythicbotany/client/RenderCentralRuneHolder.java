package mythicbotany.client;

import mythicbotany.rune.TileCentralRuneHolder;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

/** Renders the focus/output item inserted into the central Rune Holder. */
public class RenderCentralRuneHolder extends TileEntitySpecialRenderer<TileCentralRuneHolder> {
    @Override
    public void render(TileCentralRuneHolder tile, double x, double y, double z, float partialTicks,
                       int destroyStage, float alpha) {
        RenderRuneHolder.renderStack(tile.getDisplayStack(), tile, x, y, z, partialTicks);
    }
}
