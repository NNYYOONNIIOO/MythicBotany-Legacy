package mythicbotany.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/** A damageable Nether star with the full 1,200,000-point Wither Aconite charge. */
public class ItemFadedNetherStar extends Item {
    public static final int MAX_DAMAGE = 1_200_000;
    private static final String CHARGE_TAG = "mythicbotanyCharge";

    public ItemFadedNetherStar() {
        setMaxStackSize(1);
        setMaxDamage(MAX_DAMAGE);
    }

    @Override
    public int getMaxDamage() {
        return MAX_DAMAGE;
    }

    public static int getCharge(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey(CHARGE_TAG, 3)) {
            return Math.max(0, Math.min(MAX_DAMAGE, tag.getInteger(CHARGE_TAG)));
        }
        return Math.max(0, Math.min(MAX_DAMAGE, stack.getItemDamage()));
    }

    public static void setCharge(ItemStack stack, int charge) {
        if (stack != null && !stack.isEmpty()) {
            stack.setItemDamage(Math.max(0, Math.min(MAX_DAMAGE, charge)));
        }
    }

    @Override
    public int getDamage(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey(CHARGE_TAG, 3)) {
            return Math.max(0, Math.min(MAX_DAMAGE, tag.getInteger(CHARGE_TAG)));
        }
        int rawDamage = super.getDamage(stack);
        if (rawDamage > Short.MAX_VALUE) {
            setDamage(stack, rawDamage);
        }
        return Math.max(0, Math.min(MAX_DAMAGE, rawDamage));
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        int bounded = Math.max(0, Math.min(MAX_DAMAGE, damage));
        super.setDamage(stack, Math.min(Short.MAX_VALUE, bounded));
        NBTTagCompound tag = stack.getTagCompound();
        if (bounded > Short.MAX_VALUE) {
            if (tag == null) {
                tag = new NBTTagCompound();
                stack.setTagCompound(tag);
            }
            tag.setInteger(CHARGE_TAG, bounded);
        } else if (tag != null && tag.hasKey(CHARGE_TAG)) {
            tag.removeTag(CHARGE_TAG);
        }
    }

    @Override
    public int getMetadata(int damage) {
        return 0;
    }

    @Override
    public int getMetadata(ItemStack stack) {
        // The charge is durability, never an item-model variant.
        return getCharge(stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);
        // Migrate a high /give metadata value before ItemStack writes Damage as a short.
        getDamage(stack);
    }
}
