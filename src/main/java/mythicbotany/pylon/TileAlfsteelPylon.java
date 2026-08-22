package mythicbotany.pylon;

import mythicbotany.tile.ManaTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * Alfsteel pylon for the Botania 1.12 mana API.
 *
 * Botania 1.12 exposes mana receivers but not the later spark attachment
 * interfaces, so this tile uses the stable receiver contract and forwards
 * stored mana to adjacent MythicBotany mana tiles.
 */
public class TileAlfsteelPylon extends ManaTileEntity {
    private static final int TRANSFER_PER_TICK = 2000;

    /** Kept as a block-break hook for parity with later pylon implementations. */
    public void detachSpark() {
    }

    @Override
    public void update() {
        if (world == null || world.isRemote || mana <= 0) {
            return;
        }
        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity tile = world.getTileEntity(pos.offset(facing));
            if (tile instanceof ManaTileEntity && tile != this) {
                ManaTileEntity receiver = (ManaTileEntity) tile;
                int space = receiver.getMaxMana() - receiver.getCurrentMana();
                if (space > 0) {
                    int amount = Math.min(Math.min(mana, space), TRANSFER_PER_TICK);
                    receiver.recieveMana(amount);
                    mana -= amount;
                    markDirty();
                    return;
                }
            }
        }
    }
}
