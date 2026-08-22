package mythicbotany.rune;

import mythicbotany.registry.ModItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
        markDirty();
        return true;
    }

    public ItemStack takeRune() {
        ItemStack result = rune;
        rune = ItemStack.EMPTY;
        markDirty();
        return result;
    }

    public void dropContents() {
        if (world == null || world.isRemote || rune.isEmpty()) {
            return;
        }
        world.spawnEntity(new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, rune.copy()));
        rune = ItemStack.EMPTY;
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
