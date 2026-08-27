package mythicbotany.item;

import java.util.Locale;

import mythicbotany.MythicBotany;
import mythicbotany.config.MythicBotanyConfig;
import mythicbotany.registry.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Adds the configurable Alfsteel Template drop for Alfsteel Sword kills. */
public final class AlfsteelTemplateDropHandler {
    private static final float DROP_CHANCE = 0.15F;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDrops(LivingDropsEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (target == null || target.world.isRemote || !isConfiguredTarget(target)) {
            return;
        }

        EntityPlayer player = getKillingPlayer(event);
        if (player == null || !holdsAlfsteelSword(player)) {
            return;
        }
        if (target.world.rand.nextFloat() >= DROP_CHANCE) {
            return;
        }

        event.getDrops().add(new EntityItem(target.world, target.posX, target.posY, target.posZ,
                new ItemStack(ModItems.alfsteelTemplate)));
    }

    private static boolean holdsAlfsteelSword(EntityPlayer player) {
        return isAlfsteelSword(player.getHeldItemMainhand())
                || isAlfsteelSword(player.getHeldItemOffhand());
    }

    private static boolean isAlfsteelSword(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() == ModItems.alfsteelSword) {
            return true;
        }
        ResourceLocation registryName = stack.getItem().getRegistryName();
        return new ResourceLocation(MythicBotany.MODID, "alfsteel_sword").equals(registryName);
    }

    private static EntityPlayer getKillingPlayer(LivingDropsEvent event) {
        Entity source = event.getSource().getTrueSource();
        if (source instanceof EntityPlayer) {
            return (EntityPlayer) source;
        }

        Entity immediateSource = event.getSource().getImmediateSource();
        return immediateSource instanceof EntityPlayer ? (EntityPlayer) immediateSource : null;
    }

    private static boolean isConfiguredTarget(EntityLivingBase target) {
        String targetId = getEntityId(target);
        for (String configured : MythicBotanyConfig.alfsteelTemplateDropEntities) {
            if (configured == null || configured.trim().isEmpty()) {
                continue;
            }

            String configuredId = normalize(configured);
            if (configuredId.equals(targetId)) {
                return true;
            }

            ResourceLocation id;
            try {
                id = new ResourceLocation(configuredId);
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

    /**
     * EntityList#getEntityString returns legacy names in 1.12.2 (for example,
     * "Vindicator"), while the config uses Forge registry names.
     */
    private static String getEntityId(EntityLivingBase target) {
        String legacyName = EntityList.getEntityString(target);
        return legacyName == null ? null : normalize(legacyName);
    }

    private static String normalize(String value) {
        String normalized = value.trim().replace("\\", "").toLowerCase(Locale.ROOT);
        if (normalized.indexOf(':') < 0) {
            normalized = "minecraft:" + normalized;
        }
        if ("minecraft:vindicator".equals(normalized)) {
            return "minecraft:vindication_illager";
        }
        if ("minecraft:evoker".equals(normalized)
                || "minecraft:evocationillager".equals(normalized)) {
            return "minecraft:evocation_illager";
        }
        return normalized;
    }
}
