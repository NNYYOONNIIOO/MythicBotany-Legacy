package mythicbotany.client;

import mythicbotany.rune.TileCentralRuneHolder;
import mythicbotany.registry.ModBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;

/** Renders the focus/output item inserted into the central Rune Holder. */
public class RenderCentralRuneHolder extends TileEntitySpecialRenderer<TileCentralRuneHolder> {
    public static final class ItemRenderer extends TileEntityItemStackRenderer {
        private final RenderCentralRuneHolder renderer = new RenderCentralRuneHolder();
        private final TileCentralRuneHolder dummy = new TileCentralRuneHolder();

        @Override
        public void renderByItem(ItemStack stack, float partialTicks) {
            renderer.render(dummy, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        }
    }

    @Override
    public void render(TileCentralRuneHolder tile, double x, double y, double z, float partialTicks,
                       int destroyStage, float alpha) {
        RenderRuneHolder.renderBase(ModBlocks.centralRuneHolder.getDefaultState(), x, y, z);
        RenderRuneHolder.renderStack(tile.getDisplayStack(), x, y, z);
    }
}
