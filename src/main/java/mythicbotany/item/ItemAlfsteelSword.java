package mythicbotany.item;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.BurstProperties;
import vazkii.botania.api.mana.ILensEffect;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.entity.EntityManaBurst;

/** Alfsteel sword with a server-side Botania 1.12 mana-burst beam. */
public class ItemAlfsteelSword extends ItemSword implements IManaItem, IManaTooltipDisplay, ILensEffect {
    private static final String TAG_MANA = "mana";
    private static final String TAG_ATTACKER_USERNAME = "attackerUsername";
    private static final int MAX_MANA = 4000000;
    private static final int BURST_MANA = 1000;
    private static final int MANA_PER_DAMAGE = BURST_MANA;

    public ItemAlfsteelSword(Item.ToolMaterial material) {
        super(material);
        setMaxStackSize(1);
        addPropertyOverride(new ResourceLocation("mythicbotany", "beam"), (stack, world, entity) -> 0.0F);
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack) {
        if (!entity.world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.getHeldItemMainhand().getItem() == this
                    && player.getCooledAttackStrength(0) >= 1.0F
                    && (player.capabilities.isCreativeMode
                    || ManaItemHandler.requestManaExactForTool(stack, player, BURST_MANA, true))) {
                if (player.world.spawnEntity(createBurst(player, EnumHand.MAIN_HAND, stack))) {
                    stack.damageItem(1, player);
                }
            }
        }
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.getCooldownTracker().hasCooldown(this)) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if (player.capabilities.isCreativeMode
                || ManaItemHandler.requestManaExactForTool(stack, player, BURST_MANA, true)) {
            if (!world.isRemote) {
                world.spawnEntity(createBurst(player, hand, stack));
            }
            player.getCooldownTracker().setCooldown(this, 10);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        return super.hitEntity(stack, target, attacker);
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

    @Override public int getMana(ItemStack stack) { return ItemNBTHelper.getInt(stack, TAG_MANA, 0); }
    @Override public int getMaxMana(ItemStack stack) { return MAX_MANA; }
    @Override public void addMana(ItemStack stack, int mana) {
        ItemNBTHelper.setInt(stack, TAG_MANA, Math.max(0, Math.min(MAX_MANA, getMana(stack) + mana)));
    }
    @Override public boolean canReceiveManaFromPool(ItemStack stack, TileEntity pool) { return true; }
    @Override public boolean canReceiveManaFromItem(ItemStack stack, ItemStack otherStack) { return true; }
    @Override public boolean canExportManaToPool(ItemStack stack, TileEntity pool) { return true; }
    @Override public boolean canExportManaToItem(ItemStack stack, ItemStack otherStack) { return true; }
    @Override public boolean isNoExport(ItemStack stack) { return false; }
    @Override public float getManaFractionForDisplay(ItemStack stack) { return (float) getMana(stack) / MAX_MANA; }
    @Override public boolean showDurabilityBar(ItemStack stack) { return true; }
    @Override public double getDurabilityForDisplay(ItemStack stack) { return 1.0D - getManaFractionForDisplay(stack); }
    @Override public int getRGBDurabilityForDisplay(ItemStack stack) {
        return MathHelper.hsvToRGB(getManaFractionForDisplay(stack) / 3.0F, 1.0F, 1.0F);
    }

    @Override public void apply(ItemStack stack, BurstProperties props) { }
    @Override public boolean collideBurst(IManaBurst burst, RayTraceResult pos, boolean isManaBlock, boolean dead, ItemStack stack) { return dead; }
    @Override public boolean doParticles(IManaBurst burst, ItemStack stack) { return true; }

    @Override
    public void updateBurst(IManaBurst burst, ItemStack stack) {
        EntityThrowable entity = (EntityThrowable) burst;
        AxisAlignedBB axis = entity.getEntityBoundingBox()
                .expand(entity.posX - entity.lastTickPosX, entity.posY - entity.lastTickPosY,
                        entity.posZ - entity.lastTickPosZ)
                .grow(1.0D);
        List<EntityLivingBase> entities = entity.world.getEntitiesWithinAABB(EntityLivingBase.class, axis);
        String attacker = ItemNBTHelper.getString(burst.getSourceLens(), TAG_ATTACKER_USERNAME, "");
        for (EntityLivingBase living : entities) {
            if (living instanceof EntityPlayer
                    && (living.getName().equals(attacker)
                    || (FMLCommonHandler.instance().getMinecraftServerInstance() != null
                    && !FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled()))) {
                continue;
            }
            if (living.hurtTime == 0 && burst.getMana() >= MANA_PER_DAMAGE / 3) {
                burst.setMana(burst.getMana() - MANA_PER_DAMAGE / 3);
                if (!burst.isFake() && !entity.world.isRemote) {
                    EntityPlayer player = entity.world.getPlayerEntityByName(attacker);
                    living.attackEntityFrom(player == null ? DamageSource.MAGIC : DamageSource.causePlayerDamage(player), 8.0F);
                    entity.setDead();
                    break;
                }
            }
        }
    }
}
