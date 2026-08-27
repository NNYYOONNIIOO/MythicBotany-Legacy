package mythicbotany.item;

import java.lang.reflect.Method;
import java.util.Locale;

import mythicbotany.config.MythicBotanyConfig;
import mythicbotany.registry.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
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
        String entityName = getEntityId(target);
        if (entityName == null) {
            return false;
        }
        for (String configured : MythicBotanyConfig.alfsteelTemplateDropEntities) {
            if (configured != null && normalize(configured).equals(normalize(entityName))) {
                return true;
            }
        }
        return false;
    }

    private static String getEntityId(Entity entity) {
        try {
            Method getKey = EntityList.class.getMethod("getKey", Class.class);
            Object key = getKey.invoke(null, entity.getClass());
            if (key != null) {
                return key.toString();
            }
        } catch (Exception ignored) {
            // Older Forge mappings do not expose EntityList#getKey(Class).
        }
        String fallback = EntityList.getEntityString(entity);
        return fallback == null ? null : fallback;
    }

    private static String normalize(String value) {
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return normalized.indexOf(':') >= 0 ? normalized : "minecraft:" + normalized;
    }
}
