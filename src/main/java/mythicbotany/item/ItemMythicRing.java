package mythicbotany.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vazkii.botania.api.mana.IManaItem;

public class ItemMythicRing extends Item implements IBauble, IManaItem {
    public enum Effect {
        MANA, AURA, FIRE, ICE, ANDWARI
    }

    private final Effect effect;

    public ItemMythicRing(Effect effect) {
        this.effect = effect;
        setMaxStackSize(1);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        if (player.world.isRemote || player.ticksExisted % 20 != 0) {
            return;
        }
        switch (effect) {
            case MANA:
                if (getMana(stack) >= 1000 && player.getHealth() < player.getMaxHealth()) {
                    player.heal(1.0F);
                    addMana(stack, -1000);
                }
                break;
            case AURA:
                player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, 0));
                player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 40, 0));
                break;
            case FIRE:
                player.extinguish();
                player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 60, 0));
                break;
            case ICE:
                player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 60, 0));
                player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 60, 0));
                break;
            case ANDWARI:
                player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 220, 0));
                player.addPotionEffect(new PotionEffect(MobEffects.LUCK, 60, 0));
                break;
            default:
                break;
        }
    }

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
    }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
    }

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public int getMana(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0 : Math.max(0, Math.min(getMaxMana(stack), tag.getInteger("Mana")));
    }

    @Override
    public int getMaxMana(ItemStack stack) {
        return 2000000;
    }

    @Override
    public void addMana(ItemStack stack, int amount) {
        int next = Math.max(0, Math.min(getMaxMana(stack), getMana(stack) + amount));
        stack.setTagInfo("Mana", new net.minecraft.nbt.NBTTagInt(next));
    }

    @Override
    public boolean canReceiveManaFromPool(ItemStack stack, TileEntity pool) {
        return getMana(stack) < getMaxMana(stack);
    }

    @Override
    public boolean canReceiveManaFromItem(ItemStack stack, ItemStack otherStack) {
        return getMana(stack) < getMaxMana(stack);
    }

    @Override
    public boolean canExportManaToPool(ItemStack stack, TileEntity pool) {
        return getMana(stack) > 0;
    }

    @Override
    public boolean canExportManaToItem(ItemStack stack, ItemStack otherStack) {
        return getMana(stack) > 0;
    }

    @Override
    public boolean isNoExport(ItemStack stack) {
        return false;
    }
}
