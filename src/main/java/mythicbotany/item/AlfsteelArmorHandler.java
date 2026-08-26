package mythicbotany.item;

import mythicbotany.registry.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Common 1.12 movement behavior for the alfsteel boots. */
public final class AlfsteelArmorHandler {
    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!(entity instanceof EntityPlayer) || !entity.isInWater()) {
            return;
        }
        ItemStack leggings = entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        if (leggings.isEmpty() || leggings.getItem() != ModItems.alfsteelLeggings) {
            return;
        }
        entity.motionX *= 1.4D;
        entity.motionZ *= 1.4D;
    }

    @SubscribeEvent
    public void onEntityAttacked(LivingHurtEvent event) {
        if (event.getEntityLiving().world.isRemote) {
            return;
        }
        Entity attacker = event.getSource().getImmediateSource();
        if (!(attacker instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) attacker;
        if (!ModItems.hasFullAlfsteelSet(player)) {
            return;
        }
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (!isCritical(player)) {
            return;
        }

        if (ModItems.hasAncientWill(helmet, 0)) {
            event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 20, 1));
        }
        if (ModItems.hasAncientWill(helmet, 1)) {
            float missingHealth = 1.0F - player.getHealth() / player.getMaxHealth();
            event.setAmount(event.getAmount() * (1.0F + missingHealth * 0.5F));
        }
        if (ModItems.hasAncientWill(helmet, 2)) {
            player.heal(event.getAmount() * 0.25F);
        }
        if (ModItems.hasAncientWill(helmet, 3)) {
            event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 1));
        }
        if (ModItems.hasAncientWill(helmet, 4)) {
            event.getSource().setDamageBypassesArmor();
        }
        if (ModItems.hasAncientWill(helmet, 5)) {
            event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.WITHER, 60, 1));
        }
    }

    private static boolean isCritical(EntityPlayer player) {
        return player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder()
                && !player.isInWater() && !player.isPotionActive(MobEffects.BLINDNESS)
                && !player.isRiding();
    }
}
