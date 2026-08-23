package mythicbotany.item;

import mythicbotany.registry.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Common 1.12 movement behavior for the alfsteel boots. */
public final class AlfsteelArmorHandler {
    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        ItemStack boots = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        if (!boots.isEmpty() && boots.getItem() == ModItems.alfsteelBoots) {
            entity.motionY += 0.75D;
        }
    }

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
}
