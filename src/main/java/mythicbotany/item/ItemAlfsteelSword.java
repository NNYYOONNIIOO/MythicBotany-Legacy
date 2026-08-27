package mythicbotany.item;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mythicbotany.network.NetworkHandler;
import mythicbotany.network.PacketLeftClick;
import mythicbotany.config.MythicBotanyConfig;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.BurstProperties;
import vazkii.botania.api.mana.ILensEffect;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.entity.EntityManaBurst;
import vazkii.botania.common.item.equipment.tool.ToolCommons;

/** Alfsteel blade: normal melee combat plus a durability-costing mana pulse on an air swing. */
public class ItemAlfsteelSword extends ItemSword implements ILensEffect {
    private static final String TAG_ATTACKER_USERNAME = "attackerUsername";
    private static final String TAG_ORIGIN_X = "mythicbotanyPulseOriginX";
    private static final String TAG_ORIGIN_Y = "mythicbotanyPulseOriginY";
    private static final String TAG_ORIGIN_Z = "mythicbotanyPulseOriginZ";
    private static final int MAX_DAMAGE = 4600;
    private static final int BURST_MANA = 1000;
    private static final int MANA_PER_DAMAGE = 200;
    private static final double MAX_BURST_DISTANCE = 144.0D;

    public ItemAlfsteelSword(Item.ToolMaterial material) {
        super(material);
        setMaxStackSize(1);
        setMaxDamage(MAX_DAMAGE);
        MinecraftForge.EVENT_BUS.register(this);
    }

    /** Forge 1.12.2 does not send an empty left click to the server by itself. */
    @SubscribeEvent
    public void leftClick(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getEntityPlayer().world.isRemote
                && !event.getItemStack().isEmpty()
                && event.getItemStack().getItem() == this) {
            NetworkHandler.sendToServer(new PacketLeftClick());
        }
    }

    /** Called by the server packet after the client left-clicks empty space. */
    public void trySpawnBurst(EntityPlayer player, ItemStack stack) {
        if (player.world.isRemote || stack.isEmpty()
                || player.getHeldItemMainhand().getItem() != this
                || player.getCooledAttackStrength(0.0F) < 1.0F) {
            return;
        }
        player.world.spawnEntity(createBurst(player, EnumHand.MAIN_HAND, stack));
        ToolCommons.damageItem(stack, 1, player, MANA_PER_DAMAGE);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        ToolCommons.damageItem(stack, 1, attacker, MANA_PER_DAMAGE);
        return true;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        if (slot == EntityEquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<String, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                    new AttributeModifier(Item.ATTACK_SPEED_MODIFIER, "Weapon modifier", 2.4D, 0));
            builder.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                    new AttributeModifier(Item.ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 12.0D, 0));
            return builder.build();
        }
        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);
        if (!world.isRemote && entity instanceof EntityPlayer) {
            AlfsteelRepairHelper.repair(stack, (EntityPlayer) entity, world.getTotalWorldTime());
        }
    }

    private static EntityManaBurst createBurst(EntityPlayer player, EnumHand hand, ItemStack source) {
        EntityManaBurst burst = new EntityManaBurst(player, hand);
        burst.setColor(0xDD8307);
        burst.setMana(BURST_MANA);
        burst.setStartingMana(BURST_MANA);
        burst.setMinManaLoss(40);
        burst.setManaLossPerTick(4.0F);
        burst.setGravity(0.0F);
        burst.setMotion(burst.motionX * 7.0D, burst.motionY * 7.0D, burst.motionZ * 7.0D);
        ItemStack lens = source.copy();
        ItemNBTHelper.setString(lens, TAG_ATTACKER_USERNAME, player.getName());
        NBTTagCompound tag = lens.hasTagCompound() ? lens.getTagCompound() : new NBTTagCompound();
        tag.setDouble(TAG_ORIGIN_X, player.posX);
        tag.setDouble(TAG_ORIGIN_Y, player.posY);
        tag.setDouble(TAG_ORIGIN_Z, player.posZ);
        lens.setTagCompound(tag);
        burst.setSourceLens(lens);
        return burst;
    }

    @Override public void apply(ItemStack stack, BurstProperties props) { }
    @Override public boolean collideBurst(IManaBurst burst, RayTraceResult pos, boolean isManaBlock, boolean dead, ItemStack stack) { return dead; }
    @Override public boolean doParticles(IManaBurst burst, ItemStack stack) { return true; }

    @Override
    public void updateBurst(IManaBurst burst, ItemStack stack) {
        EntityThrowable entity = (EntityThrowable) burst;
        AxisAlignedBB axis = entity.getEntityBoundingBox()
                .expand(entity.posX - entity.lastTickPosX, entity.posY - entity.lastTickPosY,
                        entity.posZ - entity.lastTickPosZ).grow(1.0D);
        List<EntityLivingBase> entities = entity.world.getEntitiesWithinAABB(EntityLivingBase.class, axis);
        ItemStack lens = burst.getSourceLens();
        NBTTagCompound tag = lens.hasTagCompound() ? lens.getTagCompound() : null;
        double originX = tag != null && tag.hasKey(TAG_ORIGIN_X) ? tag.getDouble(TAG_ORIGIN_X) : entity.posX;
        double originY = tag != null && tag.hasKey(TAG_ORIGIN_Y) ? tag.getDouble(TAG_ORIGIN_Y) : entity.posY;
        double originZ = tag != null && tag.hasKey(TAG_ORIGIN_Z) ? tag.getDouble(TAG_ORIGIN_Z) : entity.posZ;
        double dx = entity.posX - originX;
        double dy = entity.posY - originY;
        double dz = entity.posZ - originZ;
        if (dx * dx + dy * dy + dz * dz > MAX_BURST_DISTANCE * MAX_BURST_DISTANCE) {
            entity.setDead();
            return;
        }

        String attackerName = ItemNBTHelper.getString(lens, TAG_ATTACKER_USERNAME, "");
        EntityPlayer attacker = entity.world.getPlayerEntityByName(attackerName);
        for (EntityLivingBase living : entities) {
            if (living instanceof EntityPlayer
                    && (living.getName().equals(attackerName)
                    || (FMLCommonHandler.instance().getMinecraftServerInstance() != null
                    && !FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled()))) {
                continue;
            }
            if (living.hurtTime == 0 && burst.getMana() >= BURST_MANA / 3) {
                burst.setMana(burst.getMana() - BURST_MANA / 3);
                float damage = 13.0F;
                if (MythicBotanyConfig.alfsteelSwordPulseInheritsWeaponAttributes) {
                    damage = attacker == null ? 13.0F
                            : (float) attacker.getEntityAttribute(
                            SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                    damage += EnchantmentHelper.getModifierForCreature(
                            stack, living.getCreatureAttribute());
                }
                if (!burst.isFake() && !entity.world.isRemote) {
                    living.attackEntityFrom(attacker == null ? DamageSource.MAGIC : DamageSource.causePlayerDamage(attacker), damage);
                    entity.setDead();
                    break;
                }
            }
        }
    }
}
