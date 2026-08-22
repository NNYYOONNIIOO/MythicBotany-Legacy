package mythicbotany.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import vazkii.botania.api.mana.IManaItem;

public class ItemMythicRing extends Item implements IBauble {
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
                player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 40, 0));
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
}
