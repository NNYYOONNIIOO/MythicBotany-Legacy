package mythicbotany.item;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Prevents the listed Alfsteel drops from being destroyed by fire or lava. */
public final class AlfsteelFireProtectionHandler {
    private static final Set<ResourceLocation> FIREPROOF_DROPS = new HashSet<>(Arrays.asList(
            id("alfsteel_ingot"),
            id("alfsteel_nugget"),
            id("alfsteel_template"),
            id("mjoellnir"),
            id("alfsteel_sword"),
            id("alfsteel_pick"),
            id("alfsteel_axe"),
            id("alfsteel_helmet"),
            id("alfsteel_chestplate"),
            id("alfsteel_leggings"),
            id("alfsteel_boots"),
            id("mana_ring_greatest"),
            id("aura_ring_greatest"),
            id("alfsteel_block")));

    private static ResourceLocation id(String path) {
        return new ResourceLocation("mythicbotany", path);
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof EntityItem)) {
            return;
        }

        EntityItem entityItem = (EntityItem) event.getEntity();
        ItemStack stack = entityItem.getItem();
        if (!stack.isEmpty() && FIREPROOF_DROPS.contains(stack.getItem().getRegistryName())) {
            // EntityItem takes fire/lava damage through Entity's invulnerability
            // check; this keeps the item entity alive while it is burning.
            entityItem.setEntityInvulnerable(true);
        }
    }
}
