package mythicbotany.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileManaCollector extends ManaTileEntity {
    @Override
    public void update() {
        if (world == null || world.isRemote || mana <= 0) {
            return;
        }
        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity tile = world.getTileEntity(getPos().offset(facing));
            if (tile instanceof ManaTileEntity) {
                ManaTileEntity receiver = (ManaTileEntity) tile;
                int space = receiver.getMaxMana() - receiver.getCurrentMana();
                if (space > 0) {
                    int amount = Math.min(Math.min(mana, space), 1000);
                    receiver.recieveMana(amount);
                    mana -= amount;
                    markDirty();
                    return;
                }
            }
        }
    }
}
