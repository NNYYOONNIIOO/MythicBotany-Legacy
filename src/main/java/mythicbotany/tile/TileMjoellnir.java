package mythicbotany.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/** Stores the damaged Mjoellnir stack inside its placed block. */
public class TileMjoellnir extends TileEntity {
    private ItemStack item = ItemStack.EMPTY;

    public ItemStack getItem() {
        return item.copy();
    }

    public void setItem(ItemStack stack) {
        item = stack == null ? ItemStack.EMPTY : stack.copy();
        markDirty();
    }

    public ItemStack takeItem() {
        ItemStack result = item.copy();
        item = ItemStack.EMPTY;
        markDirty();
        return result;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!item.isEmpty()) {
            compound.setTag("Item", item.writeToNBT(new NBTTagCompound()));
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        item = compound.hasKey("Item", 10)
                ? new ItemStack(compound.getCompoundTag("Item")) : ItemStack.EMPTY;
    }
}
