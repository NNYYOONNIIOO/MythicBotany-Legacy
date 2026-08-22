package mythicbotany.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;
import vazkii.botania.common.core.helper.ItemNBTHelper;

public class ItemMythicRing extends Item implements IBauble, IManaItem, IManaTooltipDisplay {
    public enum Effect {
        MANA, AURA, FIRE, ICE, ANDWARI, CURSED_ANDWARI
    }

    private final Effect effect;
    private static final String TAG_MANA = "mana";
    private static final String LEGACY_TAG_MANA = "Mana";

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
            case CURSED_ANDWARI:
                if (getMana(stack) >= 500) {
                    addMana(stack, -500);
                }
                player.addPotionEffect(new PotionEffect(MobEffects.POISON, 30, 0));
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
        int mana = ItemNBTHelper.getInt(stack, TAG_MANA, Integer.MIN_VALUE);
        if (mana == Integer.MIN_VALUE) {
            mana = ItemNBTHelper.getInt(stack, LEGACY_TAG_MANA, 0);
        }
        return MathHelper.clamp(mana, 0, getMaxMana(stack));
    }

    @Override
    public int getMaxMana(ItemStack stack) {
        return 2000000;
    }

    @Override
    public void addMana(ItemStack stack, int amount) {
        int next = Math.max(0, Math.min(getMaxMana(stack), getMana(stack) + amount));
        ItemNBTHelper.setInt(stack, TAG_MANA, next);
    }

    @Override
    public boolean canReceiveManaFromPool(ItemStack stack, TileEntity pool) {
        return true;
    }

    @Override
    public boolean canReceiveManaFromItem(ItemStack stack, ItemStack otherStack) {
        return true;
    }

    @Override
    public boolean canExportManaToPool(ItemStack stack, TileEntity pool) {
        return true;
    }

    @Override
    public boolean canExportManaToItem(ItemStack stack, ItemStack otherStack) {
        return true;
    }

    @Override
    public boolean isNoExport(ItemStack stack) {
        return false;
    }

    @Override
    public float getManaFractionForDisplay(ItemStack stack) {
        return (float) getMana(stack) / (float) getMaxMana(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0D - getManaFractionForDisplay(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return MathHelper.hsvToRGB(getManaFractionForDisplay(stack) / 3.0F, 1.0F, 1.0F);
    }
}
