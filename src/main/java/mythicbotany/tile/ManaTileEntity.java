package mythicbotany.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.botania.api.mana.IManaReceiver;

public abstract class ManaTileEntity extends TileEntity implements ITickable, IManaReceiver {
    protected static final int MAX_MANA = 100000;
    protected int mana;

    public BlockPos getBlockPos() {
        return getPos();
    }

    public World getWorld() {
        return world;
    }

    public int getCurrentMana() {
        return mana;
    }

    public int getMaxMana() {
        return MAX_MANA;
    }

    @Override
    public boolean isFull() {
        return mana >= getMaxMana();
    }

    @Override
    public void recieveMana(int amount) {
        if (amount > 0) {
            mana = Math.min(getMaxMana(), mana + amount);
            markDirty();
        }
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return !isFull();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Mana", mana);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        mana = Math.max(0, Math.min(getMaxMana(), compound.getInteger("Mana")));
    }
}
