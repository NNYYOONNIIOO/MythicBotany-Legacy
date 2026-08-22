package mythicbotany.item;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import vazkii.botania.api.item.ISequentialBreaker;
import vazkii.botania.api.mana.IManaGivingItem;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.item.equipment.tool.ToolCommons;

/** Alfsteel's TerraPick-like shatterer with a mana bar and a toggleable 3x3 mode. */
public class ItemAlfsteelPick extends ItemPickaxe implements IManaItem, IManaTooltipDisplay, ISequentialBreaker {
    private static final String TAG_MANA = "mana";
    private static final String TAG_ENABLED = "enabled";
    private static final String TAG_TIPPED = "tipped";
    private static final int MAX_MANA = Integer.MAX_VALUE;
    private static final int MANA_PER_BLOCK = 1000;
    private static final List<Material> MATERIALS = Arrays.asList(
            Material.ROCK, Material.IRON, Material.ICE, Material.GLASS,
            Material.PISTON, Material.ANVIL, Material.GRASS, Material.GROUND,
            Material.SAND, Material.SNOW, Material.CRAFTED_SNOW, Material.CLAY);
    public static final int[] LEVELS = {0, 10000, 1000000, 10000000, 100000000, 1000000000};

    public ItemAlfsteelPick(Item.ToolMaterial material) {
        super(material);
        setMaxDamage(0);
        setMaxStackSize(1);
        addPropertyOverride(new ResourceLocation("mythicbotany", "enabled"), (stack, world, entity) -> isEnabled(stack) ? 1.0F : 0.0F);
        addPropertyOverride(new ResourceLocation("mythicbotany", "tipped"), (stack, world, entity) -> isTipped(stack) ? 1.0F : 0.0F);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        return getMana_(stack) >= MANA_PER_BLOCK ? super.getDestroySpeed(stack, state) : 0.0F;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state,
                                    BlockPos pos, EntityLivingBase entityLiving) {
        if (!worldIn.isRemote && !(entityLiving instanceof EntityPlayer
                && ((EntityPlayer) entityLiving).capabilities.isCreativeMode)) {
            addMana(stack, -MANA_PER_BLOCK);
        }
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking() && getLevel(stack) > 0) {
            setEnabled(stack, !isEnabled(stack));
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) return EnumActionResult.PASS;
        if (!world.isRemote) setEnabled(player.getHeldItem(hand), !isEnabled(player.getHeldItem(hand)));
        return EnumActionResult.SUCCESS;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        if (!isEnabled(stack)) return false;
        RayTraceResult ray = ToolCommons.raytraceFromEntity(player.world, player, true, 10.0D);
        if (!player.world.isRemote && ray != null && ray.sideHit != null) {
            breakOtherBlock(player, stack, pos, pos, ray.sideHit);
        }
        return false;
    }

    @Override
    public void breakOtherBlock(EntityPlayer player, ItemStack stack, BlockPos pos,
                                BlockPos originPos, EnumFacing side) {
        IBlockState originState = player.world.getBlockState(pos);
        if (!isEnabled(stack) || player.world.isAirBlock(pos)
                || !MATERIALS.contains(originState.getMaterial())
                || originState.getPlayerRelativeBlockHardness(player, player.world, pos) <= 0.0F
                || !originState.getBlock().canHarvestBlock(player.world, pos, player)) return;
        int level = getLevel(stack);
        int range = level - 1;
        if (range < 0 || getMana_(stack) < MANA_PER_BLOCK) return;
        int rangeY = Math.max(1, range);
        boolean doX = side.getXOffset() == 0;
        boolean doY = side.getYOffset() == 0;
        boolean doZ = side.getZOffset() == 0;
        Vec3i begin = new Vec3i(doX ? -range : 0, doY ? -1 : 0, doZ ? -range : 0);
        Vec3i end = new Vec3i(doX ? range : 0, doY ? rangeY * 2 - 1 : 0, doZ ? range : 0);
        ToolCommons.removeBlocksInIteration(player, stack, player.world, pos, begin, end,
                candidateState -> MATERIALS.contains(candidateState.getMaterial()), isTipped(stack));
    }

    public static int getMana_(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, TAG_MANA, 0);
    }

    public static int getLevel(ItemStack stack) {
        int mana = getMana_(stack);
        for (int i = LEVELS.length - 1; i > 0; i--) if (mana >= LEVELS[i]) return i;
        return 0;
    }

    public static void setMana(ItemStack stack, int mana) {
        ItemNBTHelper.setInt(stack, TAG_MANA, Math.max(0, Math.min(MAX_MANA, mana)));
    }

    public static boolean isEnabled(ItemStack stack) { return ItemNBTHelper.getBoolean(stack, TAG_ENABLED, false); }
    public static void setEnabled(ItemStack stack, boolean enabled) {
        ItemNBTHelper.setBoolean(stack, TAG_ENABLED, enabled && getLevel(stack) > 0);
    }
    public static boolean isTipped(ItemStack stack) { return ItemNBTHelper.getBoolean(stack, TAG_TIPPED, false); }
    public static void setTipped(ItemStack stack, boolean tipped) { ItemNBTHelper.setBoolean(stack, TAG_TIPPED, tipped); }

    @Override public int getMana(ItemStack stack) { return ItemNBTHelper.getInt(stack, TAG_MANA, 0); }
    @Override public int getMaxMana(ItemStack stack) { return MAX_MANA; }
    @Override public void addMana(ItemStack stack, int mana) {
        long value = (long) getMana_(stack) + mana;
        setMana(stack, (int) Math.max(0L, Math.min((long) MAX_MANA, value)));
    }
    @Override public boolean canReceiveManaFromPool(ItemStack stack, TileEntity pool) { return true; }
    @Override public boolean canReceiveManaFromItem(ItemStack stack, ItemStack otherStack) {
        return !(otherStack.getItem() instanceof IManaGivingItem);
    }
    @Override public boolean canExportManaToPool(ItemStack stack, TileEntity pool) { return false; }
    @Override public boolean canExportManaToItem(ItemStack stack, ItemStack otherStack) { return false; }
    @Override public boolean isNoExport(ItemStack stack) { return true; }
    @Override public boolean disposeOfTrashBlocks(ItemStack stack) { return isTipped(stack); }
    @Override public int getEntityLifespan(ItemStack stack, World world) { return Integer.MAX_VALUE; }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack before, ItemStack after, boolean slotChanged) {
        return after.getItem() != this || isEnabled(before) != isEnabled(after);
    }
    @Override public float getManaFractionForDisplay(ItemStack stack) { return (float) getMana(stack) / (float) MAX_MANA; }
    @Override public boolean showDurabilityBar(ItemStack stack) { return true; }
    @Override public double getDurabilityForDisplay(ItemStack stack) { return 1.0D - getManaFractionForDisplay(stack); }
    @Override public int getRGBDurabilityForDisplay(ItemStack stack) {
        return net.minecraft.util.math.MathHelper.hsvToRGB(getManaFractionForDisplay(stack) / 3.0F, 1.0F, 1.0F);
    }
}
