package mythicbotany.item;

import mythicbotany.MythicBotany;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Adds the alfsteel boots' extra jump height through the jump event used by Botania belts. */
@Mod.EventBusSubscriber(modid = MythicBotany.MODID)
public final class AlfsteelBootJumpHandler {
    private static final ResourceLocation ALFSTEEL_BOOTS =
            new ResourceLocation(MythicBotany.MODID, "alfsteel_boots");

    /* Raising the vanilla jump velocity from about 0.42 to about 0.545 adds
       approximately 0.75 blocks to the jump apex. */
    private static final double EXTRA_JUMP_VELOCITY = 0.125D;

    private AlfsteelBootJumpHandler() {
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!(entity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        if (!boots.isEmpty() && ALFSTEEL_BOOTS.equals(boots.getItem().getRegistryName())) {
            entity.motionY += EXTRA_JUMP_VELOCITY;
        }
    }
}
