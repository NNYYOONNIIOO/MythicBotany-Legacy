package mythicbotany.item;

import java.util.Locale;

import mythicbotany.config.MythicBotanyConfig;
import mythicbotany.registry.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Adds the configurable Alfsteel Template drop for Alfsteel Sword kills. */
public final class AlfsteelTemplateDropHandler {
    private static final float DROP_CHANCE = 0.15F;

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (target == null || target.world.isRemote || !isConfiguredTarget(target)) {
            return;
        }

        Entity source = event.getSource().getTrueSource();
        if (!(source instanceof EntityPlayer) || !holdsAlfsteelSword((EntityPlayer) source)) {
            return;
        }
        if (target.world.rand.nextFloat() >= DROP_CHANCE) {
            return;
        }

        event.getDrops().add(new EntityItem(target.world, target.posX, target.posY, target.posZ,
                new ItemStack(ModItems.alfsteelTemplate)));
    }

    private static boolean holdsAlfsteelSword(EntityPlayer player) {
        return player.getHeldItemMainhand().getItem() == ModItems.alfsteelSword
                || player.getHeldItemOffhand().getItem() == ModItems.alfsteelSword;
    }

    private static boolean isConfiguredTarget(EntityLivingBase target) {
        for (String configured : MythicBotanyConfig.alfsteelTemplateDropEntities) {
            if (configured == null || configured.trim().isEmpty()) {
                continue;
            }

            ResourceLocation id;
            try {
                id = new ResourceLocation(normalize(configured));
            } catch (IllegalArgumentException ignored) {
                continue;
            }

            EntityEntry entry = ForgeRegistries.ENTITIES.getValue(id);
            if (entry != null && entry.getEntityClass().isAssignableFrom(target.getClass())) {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String value) {
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return normalized.indexOf(':') >= 0 ? normalized : "minecraft:" + normalized;
    }
}
