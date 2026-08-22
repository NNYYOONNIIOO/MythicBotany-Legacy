package mythicbotany.item;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.BurstProperties;
import vazkii.botania.api.mana.ILensEffect;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.entity.EntityManaBurst;
import vazkii.botania.common.item.equipment.tool.ToolCommons;

/** Alfsteel blade: normal melee combat plus a durability-costing mana pulse on an air swing. */
public class ItemAlfsteelSword extends ItemSword implements ILensEffect {
    private static final String TAG_ATTACKER_USERNAME = "attackerUsername";
    private static final int MAX_DAMAGE = 4600;
    private static final int BURST_MANA = 1000;
    private static final int MANA_PER_DAMAGE = 200;
    private static final double MAX_BURST_DISTANCE = 144.0D;
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("1e9f8a10-5a2e-4c0a-9e01-2c5d8b1a4001");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("1e9f8a10-5a2e-4c0a-9e01-2c5d8b1a4002");

    public ItemAlfsteelSword(Item.ToolMaterial material) {
        super(material);
        setMaxStackSize(1);
        setMaxDamage(MAX_DAMAGE);
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack) {
        if (!entity.world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.getHeldItemMainhand().getItem() == this
                    && player.getCooledAttackStrength(0.0F) >= 1.0F
                    && isAirSwing(player)) {
                player.world.spawnEntity(createBurst(player, EnumHand.MAIN_HAND, stack));
                ToolCommons.damageItem(stack, 1, player, MANA_PER_DAMAGE);
            }
        }
        return false;
    }

    private static boolean isAirSwing(EntityPlayer player) {
        Vec3d start = player.getPositionEyes(1.0F);
        Vec3d look = player.getLook(1.0F);
        Vec3d end = start.add(look.x * 5.0D, look.y * 5.0D, look.z * 5.0D);
        if (player.world.rayTraceBlocks(start, end, false, true, false) != null) return false;

        AxisAlignedBB search = player.getEntityBoundingBox()
                .expand(look.x * 5.0D, look.y * 5.0D, look.z * 5.0D).grow(1.0D);
        List<EntityLivingBase> entities = player.world.getEntitiesWithinAABB(EntityLivingBase.class, search);
        for (EntityLivingBase living : entities) {
            if (living == player || !living.canBeCollidedWith()) continue;
            AxisAlignedBB box = living.getEntityBoundingBox().grow(living.getCollisionBorderSize());
            if (box.calculateIntercept(start, end) != null) return false;
        }
        return true;
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
            builder.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                    new AttributeModifier(ATTACK_DAMAGE_UUID, "Weapon modifier", 12.0D, 0));
            builder.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                    new AttributeModifier(ATTACK_SPEED_UUID, "Weapon modifier", 2.4D, 0));
            return builder.build();
        }
        return super.getAttributeModifiers(slot, stack);
    }

    private static EntityManaBurst createBurst(EntityPlayer player, EnumHand hand, ItemStack source) {
        EntityManaBurst burst = new EntityManaBurst(player, hand);
        burst.setColor(0xB9A7FF);
        burst.setMana(BURST_MANA);
        burst.setStartingMana(BURST_MANA);
        burst.setMinManaLoss(40);
        burst.setManaLossPerTick(4.0F);
        burst.setGravity(0.0F);
        burst.setMotion(burst.motionX * 7.0D, burst.motionY * 7.0D, burst.motionZ * 7.0D);
        ItemStack lens = source.copy();
        ItemNBTHelper.setString(lens, TAG_ATTACKER_USERNAME, player.getName());
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
        String attackerName = ItemNBTHelper.getString(burst.getSourceLens(), TAG_ATTACKER_USERNAME, "");
        EntityPlayer attacker = entity.world.getPlayerEntityByName(attackerName);
        if (attacker != null && entity.getDistanceSq(attacker) > MAX_BURST_DISTANCE * MAX_BURST_DISTANCE) {
            entity.setDead();
            return;
        }

        for (EntityLivingBase living : entities) {
            if (living instanceof EntityPlayer
                    && (living.getName().equals(attackerName)
                    || (FMLCommonHandler.instance().getMinecraftServerInstance() != null
                    && !FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled()))) {
                continue;
            }
            if (living.hurtTime == 0 && burst.getMana() >= BURST_MANA / 3) {
                burst.setMana(burst.getMana() - BURST_MANA / 3);
                float damage = attacker == null ? 13.0F
                        : (float) attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                damage += EnchantmentHelper.getModifierForCreature(stack, living.getCreatureAttribute());
                if (!burst.isFake() && !entity.world.isRemote) {
                    living.attackEntityFrom(attacker == null ? DamageSource.MAGIC : DamageSource.causePlayerDamage(attacker), damage);
                    entity.setDead();
                    break;
                }
            }
        }
    }
}

