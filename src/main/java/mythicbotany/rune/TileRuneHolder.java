package mythicbotany.rune;

import mythicbotany.registry.ModItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileRuneHolder extends TileEntity {
    private ItemStack rune = ItemStack.EMPTY;

    public ItemStack getRune() {
        return rune.copy();
    }

    public boolean insertRune(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !isRitualRune(stack) || !rune.isEmpty()) {
            return false;
        }
        rune = stack.copy();
        rune.setCount(1);
        sync();
        return true;
    }

    public ItemStack takeRune() {
        ItemStack result = rune;
        rune = ItemStack.EMPTY;
        sync();
        return result;
    }

    public void dropContents() {
        if (world == null || world.isRemote || rune.isEmpty()) {
            return;
        }
        world.spawnEntity(new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, rune.copy()));
        rune = ItemStack.EMPTY;
        sync();
    }

    public static boolean isRitualRune(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        return item == ModItems.asgardRune || item == ModItems.vanaheimRune
                || item == ModItems.alfheimRune || item == ModItems.midgardRune
                || item == ModItems.joetunheimRune || item == ModItems.muspelheimRune
                || item == ModItems.niflheimRune || item == ModItems.nidavellirRune
                || item == ModItems.helheimRune
                || item == vazkii.botania.common.item.ModItems.rune;
    }

    private void sync() {
        markDirty();
        if (world != null && !world.isRemote) {
            net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
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

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!rune.isEmpty()) {
            compound.setTag("Rune", rune.writeToNBT(new NBTTagCompound()));
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        rune = compound.hasKey("Rune") ? new ItemStack(compound.getCompoundTag("Rune")) : ItemStack.EMPTY;
    }
}
