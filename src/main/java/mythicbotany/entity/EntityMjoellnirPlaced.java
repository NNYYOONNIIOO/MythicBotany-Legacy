package mythicbotany.entity;

import mythicbotany.item.MjoellnirHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/** A stationary, block-modelled Mjoellnir left behind after a failed return. */
public final class EntityMjoellnirPlaced extends Entity {
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(
            EntityMjoellnirPlaced.class, DataSerializers.ITEM_STACK);

    public EntityMjoellnirPlaced(World world) {
        super(world);
        setSize(0.8F, 0.8F);
        setNoGravity(true);
    }

    public EntityMjoellnirPlaced(World world, double x, double y, double z, ItemStack stack) {
        this(world);
        setPosition(x, y, z);
        setItem(stack);
    }

    @Override
    protected void entityInit() {
        dataManager.register(ITEM, ItemStack.EMPTY);
    }

    public ItemStack getItem() {
        return dataManager.get(ITEM);
    }

    private void setItem(ItemStack stack) {
        dataManager.set(ITEM, stack == null ? ItemStack.EMPTY : stack.copy());
    }

    @Override
    public void onUpdate() {
        setNoGravity(true);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        super.onUpdate();
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (world.isRemote) {
            return true;
        }
        if (!MjoellnirHandler.canHold(player)) {
            player.sendStatusMessage(new TextComponentTranslation(
                    "message.mythicbotany.mjoellnir_heavy_pick"), true);
            return true;
        }
        ItemStack stack = getItem();
        if (!stack.isEmpty() && !player.addItemStackToInventory(stack.copy())) {
            player.dropItem(stack.copy(), false);
        }
        setItem(ItemStack.EMPTY);
        setDead();
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        ItemStack stack = getItem();
        if (!stack.isEmpty()) {
            compound.setTag("Item", stack.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        setItem(compound.hasKey("Item", 10)
                ? new ItemStack(compound.getCompoundTag("Item")) : ItemStack.EMPTY);
    }
}
