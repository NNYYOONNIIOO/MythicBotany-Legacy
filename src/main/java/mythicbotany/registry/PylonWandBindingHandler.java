package mythicbotany.registry;

import mythicbotany.pylon.TileAlfsteelPylon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.botania.common.block.tile.mana.TileSpreader;
import vazkii.botania.common.item.ItemTwigWand;

/**
 * Completes the Forest Wand binding for the Alfsteel pylon in either click
 * order.  Botania 1.12 only invokes the selected tile's bindTo method, while
 * the pylon is a receiver and the spreader owns the actual direction/binding.
 */
public final class PylonWandBindingHandler {
    private static final String BOUND_X = "boundTileX";
    private static final String BOUND_Y = "boundTileY";
    private static final String BOUND_Z = "boundTileZ";

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null || !player.isSneaking()) {
            return;
        }

        ItemStack wand = player.getHeldItem(event.getHand());
        if (wand.isEmpty() || !(wand.getItem() instanceof ItemTwigWand)
                || !wand.hasTagCompound()) {
            return;
        }

        NBTTagCompound tag = wand.getTagCompound();
        if (!tag.hasKey(BOUND_X, 3) || !tag.hasKey(BOUND_Y, 3) || !tag.hasKey(BOUND_Z, 3)
                || tag.getInteger(BOUND_Y) == -1) {
            return;
        }

        BlockPos boundPos = new BlockPos(tag.getInteger(BOUND_X), tag.getInteger(BOUND_Y),
                tag.getInteger(BOUND_Z));
        if (boundPos.equals(event.getPos())) {
            return;
        }

        TileEntity boundTile = player.world.getTileEntity(boundPos);
        TileEntity clickedTile = player.world.getTileEntity(event.getPos());
        boolean bound = false;
        if (boundTile instanceof TileAlfsteelPylon && clickedTile instanceof TileSpreader) {
            bound = ((TileAlfsteelPylon) boundTile).bindTo(player, wand, event.getPos(), event.getFace());
        } else if (boundTile instanceof TileSpreader && clickedTile instanceof TileAlfsteelPylon) {
            bound = ((TileSpreader) boundTile).bindTo(player, wand, event.getPos(), event.getFace());
        }

        if (!bound) {
            return;
        }

        tag.setInteger(BOUND_X, 0);
        tag.setInteger(BOUND_Y, -1);
        tag.setInteger(BOUND_Z, 0);
        event.setCanceled(true);
    }
}
