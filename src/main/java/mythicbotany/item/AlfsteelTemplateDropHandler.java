package mythicbotany.item;

import java.util.Locale;

import mythicbotany.MythicBotany;
import mythicbotany.config.MythicBotanyConfig;
import mythicbotany.registry.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Adds the configurable Alfsteel Template drop for Alfsteel Sword kills. */
public final class AlfsteelTemplateDropHandler {
    private static final String TAG_LAST_ALFSTEEL_HIT = "mythicbotanyLastAlfsteelHit";
    private static final String TAG_ALFSTEEL_ATTACKER_ID = "mythicbotanyAlfsteelAttackerId";
    private static final String TAG_DROP_ROLLED = "mythicbotanyAlfsteelTemplateDropRolled";

    /** Records the actual weapon used for the last accepted hit. */
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onLivingAttack(LivingAttackEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (target == null || target.world.isRemote) {
            return;
        }

        rememberSwordHit(target, event.getSource(), !event.isCanceled());
    }

    /** Some combat implementations post LivingHurtEvent without a usable attack event. */
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onLivingHurt(LivingHurtEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (target != null && !target.world.isRemote) {
            rememberSwordHit(target, event.getSource(), !event.isCanceled());
        }
    }

    /** Handles deaths even when another mod suppresses the normal drop event. */
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onLivingDeath(LivingDeathEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (!canAttemptDrop(target, event.getSource())) {
            return;
        }

        if (!rollDrop(target)) {
            return;
        }

        if (!spawnTemplate(target)) {
            target.getEntityData().removeTag(TAG_DROP_ROLLED);
        }
    }

    /** Adds the item to vanilla's drop list when that event is available. */
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onLivingDrops(LivingDropsEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (!canAttemptDrop(target, event.getSource()) || !rollDrop(target)) {
            return;
        }

        if (event.isCanceled()) {
            if (!spawnTemplate(target)) {
                target.getEntityData().removeTag(TAG_DROP_ROLLED);
            }
        } else {
            event.getDrops().add(new EntityItem(target.world,
                    target.posX, target.posY, target.posZ,
                    new ItemStack(ModItems.alfsteelTemplate)));
        }
    }

    private static boolean holdsAlfsteelSword(EntityPlayer player) {
        return isAlfsteelSword(player.getHeldItemMainhand())
                || isAlfsteelSword(player.getHeldItemOffhand());
    }

    private static boolean isAlfsteelSword(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() == ModItems.alfsteelSword
                || stack.getItem() instanceof ItemAlfsteelSword) {
            return true;
        }
        ResourceLocation registryName = stack.getItem().getRegistryName();
        return new ResourceLocation(MythicBotany.MODID, "alfsteel_sword").equals(registryName);
    }

    private static EntityPlayer getKillingPlayer(DamageSource damageSource) {
        Entity source = damageSource.getTrueSource();
        if (source instanceof EntityPlayer) {
            return (EntityPlayer) source;
        }

        Entity immediateSource = damageSource.getImmediateSource();
        return immediateSource instanceof EntityPlayer ? (EntityPlayer) immediateSource : null;
    }

    private static EntityPlayer getKillingPlayer(DamageSource damageSource, EntityLivingBase target) {
        EntityPlayer player = getKillingPlayer(damageSource);
        if (player != null) {
            return player;
        }

        NBTTagCompound data = target.getEntityData();
        if (!data.getBoolean(TAG_LAST_ALFSTEEL_HIT) || !data.hasKey(TAG_ALFSTEEL_ATTACKER_ID)) {
            return null;
        }
        Entity attacker = target.world.getEntityByID(data.getInteger(TAG_ALFSTEEL_ATTACKER_ID));
        return attacker instanceof EntityPlayer ? (EntityPlayer) attacker : null;
    }

    private static boolean isConfiguredTarget(EntityLivingBase target) {
        for (String configured : MythicBotanyConfig.alfsteelTemplateDropEntities) {
            if (configured == null || configured.trim().isEmpty()) {
                continue;
            }

            String configuredId = normalize(configured);
            if (matchesVanillaTarget(target, configuredId)) {
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

    private static boolean canAttemptDrop(EntityLivingBase target, DamageSource source) {
        if (target == null || target.world.isRemote || !isConfiguredTarget(target)
                || target.getEntityData().getBoolean(TAG_DROP_ROLLED)) {
            return false;
        }

        EntityPlayer player = getKillingPlayer(source);
        return (player != null && holdsAlfsteelSword(player))
                || target.getEntityData().getBoolean(TAG_LAST_ALFSTEEL_HIT);
    }

    private static boolean rollDrop(EntityLivingBase target) {
        NBTTagCompound data = target.getEntityData();
        if (data.getBoolean(TAG_DROP_ROLLED)) {
            return false;
        }
        data.setBoolean(TAG_DROP_ROLLED, true);
        double chance = MythicBotanyConfig.alfsteelTemplateDropChance;
        if (chance > 1.0D) {
            chance /= 100.0D;
        }
        chance = Math.max(0.0D, Math.min(1.0D, chance));
        return target.world.rand.nextDouble() < chance;
    }

    private static boolean spawnTemplate(EntityLivingBase target) {
        EntityItem item = new EntityItem(target.world, target.posX, target.posY,
                target.posZ, new ItemStack(ModItems.alfsteelTemplate));
        item.setDefaultPickupDelay();
        return target.world.spawnEntity(item);
    }

    private static void rememberSwordHit(EntityLivingBase target, DamageSource source,
                                          boolean accepted) {
        NBTTagCompound data = target.getEntityData();
        EntityPlayer player = getKillingPlayer(source);
        if (accepted && player != null && holdsAlfsteelSword(player)) {
            data.setBoolean(TAG_LAST_ALFSTEEL_HIT, true);
            data.setInteger(TAG_ALFSTEEL_ATTACKER_ID, player.getEntityId());
        } else {
            data.setBoolean(TAG_LAST_ALFSTEEL_HIT, false);
            data.removeTag(TAG_ALFSTEEL_ATTACKER_ID);
        }
    }

    private static boolean matchesVanillaTarget(EntityLivingBase target, String configuredId) {
        if ("minecraft:witch".equals(configuredId)) {
            return target instanceof EntityWitch;
        }
        if ("minecraft:vindication_illager".equals(configuredId)
                || "minecraft:vindicator".equals(configuredId)) {
            return target instanceof EntityVindicator;
        }
        if ("minecraft:evocation_illager".equals(configuredId)
                || "minecraft:evoker".equals(configuredId)
                || "minecraft:evocationillager".equals(configuredId)) {
            return target instanceof EntityEvoker;
        }
        return false;
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
