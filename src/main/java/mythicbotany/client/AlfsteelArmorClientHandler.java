package mythicbotany.client;

import mythicbotany.registry.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/** Removes the vanilla speed-based FOV change while alfsteel leggings are worn. */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = "mythicbotany")
public final class AlfsteelArmorClientHandler {
    private AlfsteelArmorClientHandler() {
    }

    @SubscribeEvent
    public static void onFovUpdate(FOVUpdateEvent event) {
        EntityPlayer player = event.getEntity();
        ItemStack leggings = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        if (!leggings.isEmpty() && leggings.getItem() == ModItems.alfsteelLeggings) {
            event.setNewfov(event.getFov());
        }
    }
}
