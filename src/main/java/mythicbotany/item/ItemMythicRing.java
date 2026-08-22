package mythicbotany.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.botania.api.mana.ManaItemHandler;

public class ItemMythicRing extends Item implements IBauble {
    public enum Effect { MANA, AURA, FIRE, ICE, ANDWARI, CURSED_ANDWARI }
    protected final Effect effect;

    public ItemMythicRing(Effect effect) {
        this.effect = effect;
        setMaxStackSize(1);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (effect == Effect.ICE && event.getEntityLiving() instanceof EntityPlayer
                && event.getSource() == DamageSource.IN_WALL
                && BaublesApi.isBaubleEquipped((EntityPlayer) event.getEntityLiving(), this) >= 0) {
            event.setCanceled(true);
        }
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        if (player.world.isRemote || player.ticksExisted % 20 != 0) return;
        switch (effect) {
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
                if (player instanceof EntityPlayer && ManaItemHandler.requestManaExactForTool(stack, (EntityPlayer) player, 500, true)) {
                    player.addPotionEffect(new PotionEffect(MobEffects.POISON, 30, 0));
                } else {
                    player.addPotionEffect(new PotionEffect(MobEffects.POISON, 30, 0));
                }
                break;
            case MANA:
            case AURA:
            default:
                break;
        }
    }

    @Override public void onEquipped(ItemStack stack, EntityLivingBase player) { }
    @Override public void onUnequipped(ItemStack stack, EntityLivingBase player) { }
    @Override public boolean canEquip(ItemStack stack, EntityLivingBase player) { return true; }
    @Override public boolean canUnequip(ItemStack stack, EntityLivingBase player) { return true; }
}
