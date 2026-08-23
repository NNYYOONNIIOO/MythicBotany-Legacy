package mythicbotany.tile;

import mythicbotany.registry.ModItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

/** Stores and fills a Gjallar horn using mana supplied by a Mana Spreader. */
public class TileYggdrasilBranch extends ManaTileEntity {
    private static final int MAX_MANA = 10000;
    private static final int MANA_PER_TICK = 10;
    private static final int TICKS_TO_FILL = 600;
    private ItemStack horn = ItemStack.EMPTY;
    private int progress;

    @Override
    public void update() {
        if (world == null || world.isRemote) {
            return;
        }
        if (!horn.isEmpty() && horn.getItem() == ModItems.gjallarHornEmpty
                && horn.getCount() == 1) {
            if (mana >= MANA_PER_TICK) {
                mana -= MANA_PER_TICK;
                progress++;
                if (progress >= TICKS_TO_FILL) {
                    horn = new ItemStack(ModItems.gjallarHornFull);
                    progress = 0;
                }
                sync();
            }
        } else if (progress != 0) {
            progress = 0;
            sync();
        }
    }

    @Override
    public int getMaxMana() {
        return MAX_MANA;
    }

    public boolean insertHorn(ItemStack stack) {
        if (stack == null || stack.isEmpty() || stack.getItem() != ModItems.gjallarHornEmpty
                || stack.getCount() != 1 || !horn.isEmpty()) {
            return false;
        }
        if (world != null && world.isRemote) {
            return true;
        }
        horn = stack.copy();
        progress = 0;
        sync();
        return true;
    }

    public ItemStack takeHorn() {
        ItemStack result = horn.copy();
        if (world == null || !world.isRemote) {
            horn = ItemStack.EMPTY;
            progress = 0;
            sync();
        }
        return result;
    }

    public ItemStack getHorn() {
        return horn.copy();
    }

    public void dropContents() {
        if (world == null || world.isRemote || horn.isEmpty()) {
            return;
        }
        world.spawnEntity(new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D,
                pos.getZ() + 0.5D, horn.copy()));
        horn = ItemStack.EMPTY;
        progress = 0;
        sync();
    }

    private void sync() {
        markDirty();
        if (world != null && !world.isRemote) {
            net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!horn.isEmpty()) {
            compound.setTag("Horn", horn.writeToNBT(new NBTTagCompound()));
        }
        compound.setInteger("Progress", progress);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        horn = compound.hasKey("Horn")
                ? new ItemStack(compound.getCompoundTag("Horn")) : ItemStack.EMPTY;
        progress = Math.max(0, compound.getInteger("Progress"));
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }
}
