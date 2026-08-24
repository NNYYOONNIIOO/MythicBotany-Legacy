package mythicbotany.mixin;

import mythicbotany.tile.TileManaCollector;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import vazkii.botania.api.subtile.SubTileGenerating;

/** Automatically binds generating flowers to a nearby MythicBotany mana collector. */
public final class ManaCollectorLinker {
    private ManaCollectorLinker() {
    }

    public static void bindAndTransfer(SubTileGenerating flower) {
        if (flower.getWorld() == null || flower.getWorld().isRemote) {
            return;
        }

        TileEntity bound = flower.getBinding() == null
                ? null : flower.getWorld().getTileEntity(flower.getBinding());
        if (bound != null && !bound.isInvalid()) {
            if (bound instanceof TileManaCollector) {
                flower.emptyManaIntoCollector();
            }
            return;
        }

        BlockPos origin = flower.getPos();
        int range = SubTileGenerating.LINK_RANGE;
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    TileEntity tile = flower.getWorld().getTileEntity(origin.add(x, y, z));
                    if (tile instanceof TileManaCollector) {
                        flower.linkToForcefully(tile);
                        flower.emptyManaIntoCollector();
                        return;
                    }
                }
            }
        }
    }
}
