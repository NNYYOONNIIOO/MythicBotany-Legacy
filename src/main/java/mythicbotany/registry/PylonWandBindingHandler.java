package mythicbotany.registry;

import mythicbotany.pylon.TileAlfsteelPylon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.block.tile.mana.TileSpreader;
import vazkii.botania.common.item.ItemTwigWand;

/** Completes pylon/spreader bindings in either Forest Wand click order. */
public final class PylonWandBindingHandler {
    private static final BlockPos UNBOUND = new BlockPos(0, -1, 0);

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null || !player.isSneaking()) {
            return;
        }

        ItemStack wand = player.getHeldItem(event.getHand());
        if (wand.isEmpty() || !(wand.getItem() instanceof ItemTwigWand)
                || !ItemTwigWand.getBindMode(wand)) {
            return;
        }

        BlockPos boundPos = ItemTwigWand.getBoundTile(wand);
        if (boundPos.getY() == -1 || boundPos.equals(event.getPos())) {
            return;
        }

        TileEntity boundTile = player.world.getTileEntity(boundPos);
        TileEntity clickedTile = player.world.getTileEntity(event.getPos());
        boolean bound = false;

        if (boundTile instanceof TileAlfsteelPylon && clickedTile instanceof TileSpreader) {
            bound = ((TileAlfsteelPylon) boundTile).bindTo(player, wand,
                    event.getPos(), event.getFace());
        } else if (boundTile instanceof TileSpreader && clickedTile instanceof TileAlfsteelPylon) {
            bound = ((TileSpreader) boundTile).bindTo(player, wand,
                    event.getPos(), event.getFace());
        }

        if (!bound) {
            return;
        }

        ItemTwigWand.setBoundTile(wand, UNBOUND);
        if (!player.world.isRemote) {
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(player.world, boundPos);
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(player.world, event.getPos());
        }
        event.setCanceled(true);
    }
}
