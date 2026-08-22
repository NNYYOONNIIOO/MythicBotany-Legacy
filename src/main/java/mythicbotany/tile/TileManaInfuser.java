package mythicbotany.tile;

import mythicbotany.recipe.InfuserRecipe;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileManaInfuser extends ManaTileEntity {
    private ItemStack input = ItemStack.EMPTY;
    private ItemStack output = ItemStack.EMPTY;

    @Override
    public void update() {
        if (world == null || world.isRemote || input.isEmpty() || !output.isEmpty()) {
            return;
        }
        InfuserRecipe recipe = InfuserRecipe.find(input);
        if (recipe != null && mana >= recipe.getMana()) {
            mana -= recipe.getMana();
            input = ItemStack.EMPTY;
            output = recipe.getOutput();
            markDirty();
            world.notifyBlockUpdate(getPos(), world.getBlockState(getPos()), world.getBlockState(getPos()), 3);
        }
    }

    public boolean insertInput(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !input.isEmpty()) {
            return false;
        }
        input = stack.copy();
        input.setCount(1);
        markDirty();
        return true;
    }

    public ItemStack takeOutput() {
        ItemStack result = output;
        output = ItemStack.EMPTY;
        markDirty();
        return result;
    }

    public void dropContents() {
        if (world == null || world.isRemote) {
            return;
        }
        drop(input);
        drop(output);
        input = ItemStack.EMPTY;
        output = ItemStack.EMPTY;
    }

    private void drop(ItemStack stack) {
        if (!stack.isEmpty()) {
            world.spawnEntity(new EntityItem(world, getPos().getX() + 0.5D, getPos().getY() + 0.5D,
                    getPos().getZ() + 0.5D, stack.copy()));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!input.isEmpty()) {
            compound.setTag("Input", input.writeToNBT(new NBTTagCompound()));
        }
        if (!output.isEmpty()) {
            compound.setTag("Output", output.writeToNBT(new NBTTagCompound()));
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        input = compound.hasKey("Input") ? new ItemStack(compound.getCompoundTag("Input")) : ItemStack.EMPTY;
        output = compound.hasKey("Output") ? new ItemStack(compound.getCompoundTag("Output")) : ItemStack.EMPTY;
    }
}
